package org.xpen.hello.dl.djl.clip;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

/**
 * CLIP
 * 图像和文本描述匹配度的示例
 * 1.需要下载clip.pt模型(约600M),第一次会自动去aws下载放到/.djl.ai/cache/repo/model/undefined/ai/djl/localmodelzoo/xxx/clip/clip
 * 2.需要下载tokenizer.json(约2M),第一次会自动去huggingface下载(需VPN)放到/.cache/huggingface/hub/models--openai--clip-vit-base-patch32/snapshots/xxx
 *
 */
public class ImageTextComparison {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTextComparison.class);

    public static void main(String[] args) throws Exception {
        String[] imageFiles = {
            "dog.jpg",
            "fruit.jpg",
            "train.jpg",
            "cat.jpg",
            "horse.jpg",
            "cook.jpg",
            "kitchen.jpg",
            "street.jpg",
            "cat_remote_control.jpg"
        };

        String[] textDescriptions = {
            "A dog is lying on the floor next to a bike.",
            "A lot of fruits containing pears, kiwis.",
            "A passenger train is on the tracks next to a body of water.",
            "A close-up of a cat's face.",
            "A white horse is standing in a field with other farm animals.",
            "A person is cooking in a kitchen with a pot on the stove.",
            "A modern kitchen with a prominent wooden door.",
            "A person standing beside a cart down a city street.",
            "A small kitten is lying next to a white remote control."
        };

        compareImagesAndTexts(imageFiles, textDescriptions);
    }

    static void compareImagesAndTexts(String[] imageFiles, String[] textDescriptions) throws Exception {
        try (ClipModel model = new ClipModel()) {
            for (String imageFile : imageFiles) {
                Path imagePath = Paths.get("src/test/resources/cv/", imageFile);
                Image img = ImageFactory.getInstance().fromFile(imagePath);

                // 比较当前图片与所有文本描述，获取 logit 分数
                float[] logits = new float[textDescriptions.length];
                for (int i = 0; i < textDescriptions.length; i++) {
                    logits[i] = model.compareTextAndImage(img, textDescriptions[i])[0];
                }

                // 将 logits 转换为概率
                double[] probabilities = softmax(logits);

                // 找出 Top-3 的索引
                Integer[] top3Indices = IntStream.range(0, probabilities.length)
                        .boxed()
                        .sorted(Comparator.comparingDouble(i -> -probabilities[i]))
                        .limit(3)
                        .toArray(Integer[]::new);

                LOGGER.info("Top 3 matches for '{}':", imageFile);
                for (int i = 0; i < top3Indices.length; i++) {
                    int index = top3Indices[i];
                    LOGGER.info("  {}. '{}' (Probability: {})", i + 1, textDescriptions[index], probabilities[index]);
                }
                LOGGER.info("--------------------------------------------------");
            }
        }
    }

    /**
     * 计算 softmax
     * @param logits 原始 logit 分数
     * @return 概率数组
     */
    private static double[] softmax(float[] logits) {
    	// 将logits转换为double数组，进行指数计算
        double[] logitsDouble = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            logitsDouble[i] = logits[i];
        }    	
        double[] exps = Arrays.stream(logitsDouble).map(Math::exp).toArray();
        double sumExps = Arrays.stream(exps).sum();
        return Arrays.stream(exps).map(d -> d / sumExps).toArray();
    }
    
}