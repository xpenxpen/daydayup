package org.xpen.hello.dl.djl.cv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.huggingface.translator.ZeroShotObjectDetectionTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.VisionLanguageInput;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.translator.YoloWorldTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 0样本目标检测
 * 模型不仅能检测训练集中的已知类别，还能通过文本描述检测和识别未见过的新类别。
 * 支持2种模型，请自行切换测试比较模型的性能差异
 * 0: yolov8s-worldv2
 * 1: owlv2-base-patch16
 * 
 */
public final class ObjectDetectionZeroShot {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDetection.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    private static void predict() throws Exception {
    	int modelType = 1;
    	//yolov8s-worldv2问题多,只能检测cat_remote_control
    	String[][] images = {
    			{"dog.jpg", "bike,tree"},
    			{"fruit.jpg", "pear,kiwi"},
    			{"train.jpg", "sea,building"},
    			{"cat.jpg", "kitten,flower"},
    			{"horse.jpg", "hen,chick,donkey,horse,pig,cow,sheep"},
    			{"cook.jpg", "pot,lamp"},
    			{"kitchen.jpg", "door,lamp"},
    			{"street.jpg", "cart"},
    			{"cat_remote_control.jpg", "kitten,remote control"}
    			};

        Criteria<VisionLanguageInput, DetectedObjects> criteria;
        if (modelType == 0) {
        	criteria = Criteria.builder()
				.optApplication(Application.CV.ZERO_SHOT_OBJECT_DETECTION)
				.setTypes(VisionLanguageInput.class, DetectedObjects.class)
				.optModelUrls("djl://ai.djl.pytorch/yolov8s-worldv2")
				.optEngine("PyTorch")
                .optTranslatorFactory(new YoloWorldTranslatorFactory())
				.optProgress(new ProgressBar())
				.build();
        } else {
            criteria = Criteria.builder()
    			.optApplication(Application.CV.ZERO_SHOT_OBJECT_DETECTION)
                .setTypes(VisionLanguageInput.class, DetectedObjects.class)
                .optModelUrls("djl://ai.djl.huggingface.pytorch/google/owlv2-base-patch16")
                .optEngine("PyTorch")
                .optDevice(Device.cpu()) // Only support CPU
                .optTranslatorFactory(new ZeroShotObjectDetectionTranslatorFactory())
                .optProgress(new ProgressBar())
                .build();
        }

        try (ZooModel<VisionLanguageInput, DetectedObjects> model = criteria.loadModel();
                Predictor<VisionLanguageInput, DetectedObjects> predictor = model.newPredictor()) {
  	
        	for (String[] data : images) {
        		String imageName = data[0];
				Path imageFile = Paths.get("src/test/resources/cv/" + imageName);
	            Image img = ImageFactory.getInstance().fromFile(imageFile);
	            //img = img.resize(640, 480, false);
	            
	            String[] candidates = new String[] {};
	            if (data[1] != null) {
	            	candidates = data[1].split(",");
	            }
	            VisionLanguageInput input =
	                    new VisionLanguageInput(img, candidates);
	        	
	            DetectedObjects detection = predictor.predict(input);
	            saveBoundingBoxImage(img, imageName, detection);
	            LOGGER.info("{}", detection);
        	}
        }
    }

    private static void saveBoundingBoxImage(Image img, String image, DetectedObjects detection)
            throws IOException {
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
