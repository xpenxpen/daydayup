package org.xpen.hello.dl.djl.cv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.translator.YoloSegmentationTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 实例分割
 * yolo11n-seg
 * 
 * 实例分割同时识别对象类别和每个对象的像素级分割掩码。
 */
public class SegInstanceSegmentation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegInstanceSegmentation.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    public static void predict() throws Exception {
        String[] images = {
            "bike.jpg",
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

        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                		.optApplication(Application.CV.INSTANCE_SEGMENTATION)
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelUrls("djl://ai.djl.pytorch/yolo11n-seg")
                        .optTranslatorFactory(new YoloSegmentationTranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<Image, DetectedObjects> model = criteria.loadModel();
                Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
        	
        	for (String imageName : images) {
        		Path imageFile = Paths.get("src/test/resources/cv/" + imageName);
                Image img = ImageFactory.getInstance().fromFile(imageFile);
            	
                DetectedObjects detection = predictor.predict(img);
                saveBoundingBoxImage(img, imageName, detection);
                LOGGER.info("{}", detection);
        	}
        }
    }

    private static void saveBoundingBoxImage(Image img, String image, DetectedObjects detection) throws IOException {
        Path outputDir = Paths.get("target");
        Files.createDirectories(outputDir);

        img.drawBoundingBoxes(detection);
        CvUtil.showImage(img, image);

        Path imagePath = outputDir.resolve(image.replace(".jpg", ".png"));
        // OpenJDK can't save jpg with alpha channel
        img.save(Files.newOutputStream(imagePath), "png");
        LOGGER.info("Detected objects image has been saved in: {}", imagePath);
    }
}
