package org.xpen.hello.dl.djl.cv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 生成对抗网络BigGAN图片生成
 * IMAGE_NET_CLASSES需要下载并放入指定目录:
 * https://raw.githubusercontent.com/pytorch/hub/master/imagenet_classes.txt
 * 
 */
public final class BigGan {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigGan.class);
    
    private static final String IMAGE_NET_CLASSES = "D:/opensource/imagenet/imagenet_classes.txt";

    public static void main(String[] args) throws Exception {
        int[] input = {100, 207, 971, 970, 933};
        printClassNames(input);
        Image[] generatedImages = BigGan.generate(input);
        LOGGER.info("Using PyTorch Engine. {} images generated.", generatedImages.length);
        saveImages(generatedImages);
    }

    private static void saveImages(Image[] generatedImages) throws Exception {
        Path outputPath = Paths.get("target/gan/");
        Files.createDirectories(outputPath);

        for (int i = 0; i < generatedImages.length; ++i) {
            Path imagePath = outputPath.resolve("image" + i + ".png");
            generatedImages[i].save(Files.newOutputStream(imagePath), "png");
        }
        LOGGER.info("Generated images have been saved in: {}", outputPath);
    }

    public static Image[] generate(int[] input) throws Exception {
        Criteria<int[], Image[]> criteria = Criteria.builder()
                .optApplication(Application.CV.IMAGE_GENERATION)
                .setTypes(int[].class, Image[].class)
				.optModelUrls("djl://ai.djl.pytorch/biggan-deep")
                .optFilter("size", "256")
                .optArgument("truncation", 0.4f)
                .optEngine("PyTorch")
                .optProgress(new ProgressBar())
                .build();


        
        try (ZooModel<int[], Image[]> model = criteria.loadModel();
                Predictor<int[], Image[]> generator = model.newPredictor()) {
            return generator.predict(input);
        }
    }

    private static void printClassNames(int[] input) throws Exception {
        List<String> classNames = Files.readAllLines(Paths.get(IMAGE_NET_CLASSES));
        System.out.println("Input class names:");
        for (int idx : input) {
        	LOGGER.info(idx + ": " + classNames.get(idx));
        }
    }
}
