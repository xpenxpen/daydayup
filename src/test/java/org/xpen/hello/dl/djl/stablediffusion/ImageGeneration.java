package org.xpen.hello.dl.djl.stablediffusion;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ai.djl.Device;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

/**
 * StableDiffusion
 * 文生图/图生图
 * 1.需要下载unet_traced_model.pt模型(约3.2G),第一次会自动去aws下载放到/.djl.ai/cache/repo/model/undefined/ai/djl/localmodelzoo/xxx/unet_traced_model
 * 2.需要下载vae_decode_model.pt模型(约300M),第一次会自动去aws下载放到/.djl.ai/cache/repo/model/undefined/ai/djl/localmodelzoo/xxx/vae_decode_model
 * 3.需要下载text_encoder.pt模型(约500M),第一次会自动去aws下载放到/.djl.ai/cache/repo/model/undefined/ai/djl/localmodelzoo/xxx/text_encoder
 * 4.需要下载tokenizer.json(约2M),第一次会自动去huggingface下载(需VPN)放到/.cache/huggingface/hub/models--openai--clip-vit-base-patch32/snapshots/xxx
 * 5.需要下载vae_encode_model.pt模型(约300M)(如果需要使用图生图功能)
 *
 */
public class ImageGeneration {

    public static void main(String[] args) throws Exception {
        int type = 0; // 0:文生图, 1:图生图
        boolean saveEachStep = false; //是否保存每一步的中间图像
        StableDiffusionModel model = new StableDiffusionModel(Device.cpu());
        //String text = "Photograph of an astronaut riding a horse in desert";
        //String text = "Zelda raise a sword";
        //String text = "brazier with fire, pixel art";
        String originalImage = "src/test/resources/cv/dog.jpg";
        String text = "make the dog riding bike";
        Path outputDir = Paths.get("target/StableDiffusion").resolve(text);
        StableDiffusionModel.StepImageCallback stepImageCallback =
                saveEachStep
                        ? (step, totalSteps, timestep, image) ->
                                saveStepImage(image, outputDir, step, totalSteps, timestep)
                        : null;
        Image result;
        if (type == 0) {
            result = model.generateImageFromText(text, 50, stepImageCallback);
            saveImage(result, "generated", outputDir);
        } else {
            Path imageFile = Paths.get(originalImage);
            Image inputImage = ImageFactory.getInstance().fromFile(imageFile);
            result = model.generateImageFromImage(text, inputImage, 40, stepImageCallback);
            saveImage(result, "generated-img2img", outputDir);
        }
    }

    public static void saveImage(Image image, String name, Path outputPath) throws IOException {
        Files.createDirectories(outputPath);
        Path imagePath = outputPath.resolve(name + ".png");
        try (OutputStream outputStream = Files.newOutputStream(imagePath)) {
            image.save(outputStream, "png");
        }
    }

    public static void saveImage(Image image, String name, String path) throws IOException {
        saveImage(image, name, Paths.get(path));
    }

    private static void saveStepImage(Image image, Path outputDir, int step, int totalSteps, long timestep) throws IOException {
        int width = String.valueOf(totalSteps).length();
        String fileName = String.format("step-%0" + width + "d-t%03d", step, timestep);
        saveImage(image, fileName, outputDir);
    }

}
