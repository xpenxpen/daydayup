package org.xpen.hello.dl.djl.cv;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.CategoryMask;
import ai.djl.modality.cv.translator.SemanticSegmentationTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 语义分割
 * deeplabv3
 * 
 */
public final class SegSemanticSegmentation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegSemanticSegmentation.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    public static void predict() throws Exception {
        Path imageFile = Paths.get("src/test/resources/cv/dog.jpg");
        ImageFactory factory = ImageFactory.getInstance();
        Image img = factory.fromFile(imageFile);

        Criteria<Image, CategoryMask> criteria =
                Criteria.builder()
						.optApplication(Application.CV.SEMANTIC_SEGMENTATION)
                        .setTypes(Image.class, CategoryMask.class)
                        .optModelUrls("djl://ai.djl.pytorch/deeplabv3")
                        .optTranslatorFactory(new SemanticSegmentationTranslatorFactory())
                        .optEngine("PyTorch")
                        .optProgress(new ProgressBar())
                        .build();
        Image bg = factory.fromFile(Paths.get("src/test/resources/dl/stars-in-the-night-sky.jpg"));
        try (ZooModel<Image, CategoryMask> model = criteria.loadModel();
                Predictor<Image, CategoryMask> predictor = model.newPredictor()) {
            CategoryMask mask = predictor.predict(img);

            // Highlights the detected object on the image with random opaque colors.
            Image img1 = img.duplicate();
            mask.drawMask(img1, 255);
            saveSemanticImage(img1, "semantic_instances1.png");

            // Highlights the detected object on the image with random colors.
            Image img2 = img.duplicate();
            mask.drawMask(img2, 180, 0);
            saveSemanticImage(img2, "semantic_instances2.png");

            // Highlights only the dog with blue color.
            Image img3 = img.duplicate();
            mask.drawMask(img3, 12, Color.BLUE.brighter().getRGB(), 180);
            saveSemanticImage(img3, "semantic_instances3.png");

            // Extract dog from the image
            Image dog = mask.getMaskImage(img, 12);
            dog = dog.resize(img.getWidth(), img.getHeight(), true);
            saveSemanticImage(dog, "semantic_instances4.png");

            // Replace background with an image
            bg = bg.resize(img.getWidth(), img.getHeight(), true);
            bg.drawImage(dog, true);
            saveSemanticImage(bg, "semantic_instances5.png");
        }
    }

    private static void saveSemanticImage(Image img, String fileName) throws Exception {
        Path outputDir = Paths.get("target/semantic_segmentation");
        Files.createDirectories(outputDir);

        Path imagePath = outputDir.resolve(fileName);
        img.save(Files.newOutputStream(imagePath), "png");
        LOGGER.info("Segmentation result image has been saved in: {}", imagePath.toAbsolutePath());
        CvUtil.showImage(img, fileName);
    }
}
