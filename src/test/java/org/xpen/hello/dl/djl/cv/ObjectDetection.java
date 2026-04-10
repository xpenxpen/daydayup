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
import ai.djl.modality.cv.translator.YoloV8TranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 目标检测
 * 支持3种模型，请自行切换测试比较模型的性能差异
 * 0: ssd_300_resnet50(default)
 * 1: yolov5s
 * 2: yolov8n
 * 3: yolov8n(onnx)(暂不支持可能需要自行下载模型)
 * 
 */
public class ObjectDetection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDetection.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    private static void predict() throws Exception {
        int modelType = 1; // 0: ssd_300_resnet50(default), 1: yolov5s, 2: yolov8n, 3: yolov8n(onnx)
        String[] images = {
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

        Criteria<Image, DetectedObjects> criteria;
        if (modelType == 0) {
			criteria = Criteria.builder()
                    .optApplication(Application.CV.OBJECT_DETECTION)
                    .setTypes(Image.class, DetectedObjects.class)
                    .optArgument("threshold", 0.5)
                    .optEngine("PyTorch")
                    .optProgress(new ProgressBar())
                    .build();
		} else if (modelType == 1) {
			criteria = Criteria.builder()
					.optApplication(Application.CV.OBJECT_DETECTION)
					.setTypes(Image.class, DetectedObjects.class)
					.optModelUrls("djl://ai.djl.pytorch/yolov5s")
                    .optArgument("threshold", 0.5)
					.optEngine("PyTorch")
					.optProgress(new ProgressBar())
					.build();
		} else if (modelType == 2) {
			criteria = Criteria.builder()
					.optApplication(Application.CV.OBJECT_DETECTION)
					.setTypes(Image.class, DetectedObjects.class)
					.optModelUrls("djl://ai.djl.pytorch/yolov8n")
					.optEngine("PyTorch")
					.optProgress(new ProgressBar())
					.build();
		} else {
			criteria = Criteria.builder()
					.optApplication(Application.CV.OBJECT_DETECTION)
					.setTypes(Image.class, DetectedObjects.class)
					.optModelUrls("ai.djl.onnxruntime/yolo11n")
                    .optEngine("OnnxRuntime")
                    .optArgument("maxBox", 1000)
                    .optTranslatorFactory(new YoloV8TranslatorFactory())
					.optProgress(new ProgressBar())
					.build();
		}

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
