package org.xpen.hello.dl.djl.face;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.hello.dl.djl.cv.CvUtil;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 人脸检测
 * 支持2种模型，请自行切换测试比较模型的性能差异
 * 0: Light(UltraNet) (ultranet.pt，1M)
 * https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB
 * 1: RetinaFace (retinaface.pt模型，100M)
 * 
 */
public final class FaceDetection {

    private static final Logger LOGGER = LoggerFactory.getLogger(FaceDetection.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    private static void predict() throws Exception {
        int modelType = 0; // 0: Light(UltraNet), 1: RetinaFace
		String[] images = {
			"largest_selfie.jpg",
			"group.jpg",
			"lena.jpg"
		};

        double confThresh = 0.85f;
        double nmsThresh = 0.45f;
        double[] variance = {0.1f, 0.2f};
        int topK = 5000;

        String modelUrl;
        String modelName = null;
        int[][] scales;
        int[] steps;

        if (modelType == 0) {
            modelUrl = "https://resources.djl.ai/test-models/pytorch/ultranet.zip";
            scales = new int[][] {{10, 16, 24}, {32, 48}, {64, 96}, {128, 192, 256}};
            steps = new int[] {8, 16, 32, 64};
        } else if (modelType == 1) {
            modelUrl = "https://resources.djl.ai/test-models/pytorch/retinaface.zip";
            modelName = "retinaface";
            scales = new int[][] {{16, 32}, {64, 128}, {256, 512}};
            steps = new int[] {8, 16, 32};
        } else {
            throw new IllegalArgumentException("Unsupported modelType: " + modelType);
        }

        FaceDetectionTranslator translator =
                new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        Criteria.Builder<Image, DetectedObjects> builder =
                Criteria.builder()
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelUrls(modelUrl)
                        .optTranslator(translator)
                        .optProgress(new ProgressBar())
                        .optEngine("PyTorch");

        if (modelName != null) {
            builder.optModelName(modelName);
        }

        Criteria<Image, DetectedObjects> criteria = builder.build();

        try (ZooModel<Image, DetectedObjects> model = criteria.loadModel();
                Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            for (String imageName : images) {
                Path imageFile = Paths.get("src/test/resources/cv/" + imageName);
                Image img = ImageFactory.getInstance().fromFile(imageFile);

                DetectedObjects detection = predictor.predict(img);
                saveBoundingBoxImage(img, imageName, detection);
                LOGGER.info("Found {} face(s)", detection.items().size());
                LOGGER.info("[{}] {}", imageName, detection);
                CvUtil.showImage(img, imageName);
            }
        }
    }

    private static void saveBoundingBoxImage(Image img, String imageName, DetectedObjects detection) throws IOException {
        Path outputDir = Paths.get("target/face");
        Files.createDirectories(outputDir);

        img.drawBoundingBoxes(detection);

        String outputName = imageName.replaceFirst("\\.[^.]+$", ".png");
        Path imagePath = outputDir.resolve(outputName);
        img.save(Files.newOutputStream(imagePath), "png");
        LOGGER.info("Face detection result image has been saved in: {}", imagePath);
    }
}
