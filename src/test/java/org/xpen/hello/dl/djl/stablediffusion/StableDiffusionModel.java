package org.xpen.hello.dl.djl.stablediffusion;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.Arrays;

/**
 * 负责加载 Stable Diffusion 所需的各个子模型
 * 协调文本编码,UNet去噪,VAE编解码
 * 组织整个文生图/图生图推理流程
 *
 */
public class StableDiffusionModel {

    @FunctionalInterface
    public interface StepImageCallback {
        void accept(int step, int totalSteps, long timestep, Image image) throws IOException;
    }

    private static final int HEIGHT = 512;
    private static final int WIDTH = 512;
    /** scheduler 时间步的偏移量*/
    private static final int OFFSET = 1;
    /** classifier-free guidance 的引导强度*/
    private static final float GUIDANCE_SCALE = 7.5f;
    /** 图生图时保留原图信息的强度*/
    private static final float STRENGTH = 0.5f;

    /** 图片 -> latent*/
    private Predictor<Image, NDArray> vaeEncoder;
    /** latent -> 图片*/
    private Predictor<NDArray, Image> vaeDecoder;
    /** 文本 -> embedding*/
    private Predictor<String, NDList> textEncoder;
    /** 扩散模型每一步预测噪声*/
    private Predictor<NDList, NDList> unetExecutor;
    private Device device;

    public StableDiffusionModel(Device device) throws ModelException, IOException {
        this.device = device;
        String type = device.getDeviceType();
        if (!"cpu".equals(type) && !"gpu".equals(type)) {
            throw new UnsupportedOperationException(type + " device not supported!");
        }
        Criteria<NDList, NDList> unetCriteria = Criteria.builder()
            .setTypes(NDList.class, NDList.class)
            .optModelUrls("https://resources.djl.ai/demo/pytorch/stable-diffusion/"
                            + type
                            + "/unet_traced_model.zip")
            .optEngine("PyTorch")
            .optProgress(new ProgressBar())
            .optTranslator(new NoopTranslator())
            .optDevice(device)
            .build();
        this.unetExecutor = unetCriteria.loadModel().newPredictor();
        Criteria<NDArray, Image> decoderCriteria = Criteria.builder()
            .setTypes(NDArray.class, Image.class)
            .optModelUrls("https://resources.djl.ai/demo/pytorch/stable-diffusion/"
                            + type
                            + "/vae_decode_model.zip")
            .optEngine("PyTorch")
            .optTranslator(new ImageDecoder())
            .optProgress(new ProgressBar())
            .optDevice(device)
            .build();
        this.vaeDecoder = decoderCriteria.loadModel().newPredictor();
        Criteria<String, NDList> criteria = Criteria.builder()
            .setTypes(String.class, NDList.class)
            .optModelUrls("https://resources.djl.ai/demo/pytorch/stable-diffusion/"
                            + type
                            + "/text_encoder.zip")
            .optEngine("PyTorch")
            .optProgress(new ProgressBar())
            .optTranslator(new TextEncoder())
            .optDevice(device)
            .build();
        this.textEncoder = criteria.loadModel().newPredictor();
    }

    public Image generateImageFromText(String prompt, int steps) throws ModelException, IOException, TranslateException {
        return generateImageFromText(prompt, steps, null);
    }

    public Image generateImageFromText(String prompt, int steps, StepImageCallback stepImageCallback)
            throws ModelException, IOException, TranslateException {
        return generateImageFromImage(prompt, null, steps, stepImageCallback);
    }

    public Image generateImageFromImage(String prompt, Image image, int steps) throws ModelException, IOException, TranslateException {
        return generateImageFromImage(prompt, image, steps, null);
    }

    public Image generateImageFromImage(String prompt, Image image, int steps, StepImageCallback stepImageCallback)
            throws ModelException, IOException, TranslateException {
        try (NDManager manager = NDManager.newBaseManager(device, "PyTorch")) {
            // Step 1: 构建文本embedding
         	/*
        	 * classifier-free guidance 的标准做法
        	 * 对同一文本做两次编码,一次正常编码(prompt),一次空文本编码(uncond),
        	 * 然后在每一步扩散时都让模型预测两次噪声,
        	 * 最后把两次预测的结果按一定权重融合来得到最终的噪声预测
        	 */
            NDList textEncoding = textEncoder.predict(prompt);
            NDList uncondEncoding = textEncoder.predict("");
            textEncoding.attach(manager);
            uncondEncoding.attach(manager);
            NDArray textEncodingArray = textEncoding.get(1);
            NDArray uncondEncodingArray = uncondEncoding.get(1);
            NDArray embeddings = textEncodingArray.concat(uncondEncodingArray);
            // Step 2: 初始化 scheduler 和 latent
            PndmScheduler scheduler = new PndmScheduler(manager);
            scheduler.initTimesteps(steps, OFFSET);
            Shape latentInitShape = new Shape(1, 4, HEIGHT / 8, WIDTH / 8);
            NDArray latent;
            if (image != null) {
            	/*
            	 * 图生图时img2img 的常见做法:
            	 * 先把输入图片编码成 latent,
            	 * 再根据 scheduler 和 STRENGTH 加入适量噪声,
            	 * 根据 STRENGTH 缩短时间步，让生成结果保留部分原图结构
            	 * 最后从这个带噪声的 latent 开始扩散迭代
            	 */
                loadImageEncoder();
                latent = vaeEncoder.predict(image);
                NDArray noise = manager.randomNormal(latent.getShape());
                // Step 2.5: reset timestep to reflect on the given image
                int initTimestep = (int) (steps * STRENGTH) + OFFSET;
                initTimestep = Math.min(initTimestep, steps);
                int[] timestepsArr = scheduler.getTimesteps();
                int timesteps = timestepsArr[timestepsArr.length - initTimestep];
                latent = scheduler.addNoise(latent, noise, timesteps);
                int tStart = Math.max(steps - initTimestep + OFFSET, 0);
                scheduler.setTimesteps(Arrays.copyOfRange(timestepsArr, tStart, timestepsArr.length));
            } else {
            	//文生图时直接从随机噪声开始
                latent = manager.randomNormal(latentInitShape);
            }
            // Step 3: 循环去噪
            int[] schedulerTimesteps = scheduler.getTimesteps();
            ProgressBar pb = new ProgressBar("Generating", steps);
            pb.start(0);
            Image lastImage = null;
            for (int i = 0; i < schedulerTimesteps.length; i++) {
                long t = schedulerTimesteps[i];
                //3.1 复制 latent 因为要同时跑“有条件”和“无条件”两路预测，所以把 latent 拼成两份
                NDArray latentModelOutput = latent.concat(latent);
                //3.2 调 UNet 预测噪声
				NDArray noisePred = unetExecutor.predict(
						new NDList(latentModelOutput, manager.create(t), embeddings)).get(0);
				//3.3 classifier-free guidance 融合
				//noise = uncond + scale * (text - uncond)
                NDList splitNoisePred = noisePred.split(2);
                NDArray noisePredText = splitNoisePred.get(0);
                NDArray noisePredUncond = splitNoisePred.get(1);
                NDArray scaledNoisePredUncond = noisePredText.add(noisePredUncond.neg());
                scaledNoisePredUncond = scaledNoisePredUncond.mul(GUIDANCE_SCALE);
                noisePred = noisePredUncond.add(scaledNoisePredUncond);
                //3.4 交给 scheduler 更新 latent
                latent = scheduler.step(noisePred, (int) t, latent);
                if (stepImageCallback != null) {
                    lastImage = vaeDecoder.predict(latent);
                    stepImageCallback.accept(i + 1, schedulerTimesteps.length, t, lastImage);
                }
                pb.increment(1);
            }
            pb.end();
            // Step 4: 把最终 latent 解码成图片
            if (lastImage != null) {
                return lastImage;
            }
            return vaeDecoder.predict(latent);
        }
    }

    /**
     * 图生图是懒加载,按需初始化
     */
    private void loadImageEncoder() throws ModelException, IOException {
        if (vaeEncoder != null) {
            return;
        }
        Criteria<Image, NDArray> criteria = Criteria.builder()
	        .setTypes(Image.class, NDArray.class)
	        .optModelUrls("https://resources.djl.ai/demo/pytorch/stable-diffusion/"
	                        + device.getDeviceType()
	                        + "/vae_encode_model.zip")
	        .optEngine("PyTorch")
	        .optTranslator(new ImageEncoder(HEIGHT, WIDTH))
	        .optProgress(new ProgressBar())
	        .optDevice(device)
	        .build();
        vaeEncoder = criteria.loadModel().newPredictor();
    }
}
