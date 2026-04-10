package org.xpen.hello.dl.djl.cv;

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
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input;
import ai.djl.modality.cv.translator.Sam2TranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * SAM2分割
 * 
 */
public class SegSegmentAnything2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegSegmentAnything2.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    public static void predict() throws Exception {
        String[] images = {
                "truck.jpg",
            };

        Criteria<Sam2Input, DetectedObjects> criteria =
                Criteria.builder()
        				.optApplication(Application.CV.MASK_GENERATION)
                        .setTypes(Sam2Input.class, DetectedObjects.class)
                        .optModelUrls("djl://ai.djl.pytorch/sam2-hiera-tiny")
                        .optTranslatorFactory(new Sam2TranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<Sam2Input, DetectedObjects> model = criteria.loadModel();
                Predictor<Sam2Input, DetectedObjects> predictor = model.newPredictor()) {
        	
        	for (String imageName : images) {
        		Path imageFile = Paths.get("src/test/resources/cv/" + imageName);
                Image img = ImageFactory.getInstance().fromFile(imageFile);
            	
                Sam2Input input = Sam2Input.builder(img)
                    	.addPoint(575, 750)
                    	.addBox(425, 600, 700, 875)
                    	.build();
                
                DetectedObjects detection = predictor.predict(input);
                showMask(input, imageName, detection);
                LOGGER.info("{}", detection);
        	}
        }
    }

    private static void showMask(Sam2Input input, String image, DetectedObjects detection) throws Exception {
        Path outputDir = Paths.get("target");
        Files.createDirectories(outputDir);

        Image img = input.getImage();
        img.drawBoundingBoxes(detection, 0.8f);
        img.drawMarks(input.getPoints());
        for (Rectangle rect : input.getBoxes()) {
            img.drawRectangle(rect, 0xff0000, 6);
        }
        
        CvUtil.showImage(img, image);

        Path imagePath = outputDir.resolve(image.replace(".jpg", ".png"));
        // OpenJDK can't save jpg with alpha channel
        img.save(Files.newOutputStream(imagePath), "png");
        LOGGER.info("Segmentation result image has been saved in: {}", imagePath);
    }
}
