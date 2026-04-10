package org.xpen.hello.dl.djl.cv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.Joints;
import ai.djl.modality.cv.translator.YoloPoseTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;

/**
 * 姿态估计
 *
 */
public class PoseEstimation {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(PoseEstimation.class);

	public static void main(String[] args) throws Exception {
        Joints[] joints = predict();
        LOGGER.info("{}", Arrays.toString(joints));
	}
	
    public static Joints[] predict() throws Exception {
        Path imageFile = Paths.get("src/test/resources/dl/pose_soccer.png");
        Image img = ImageFactory.getInstance().fromFile(imageFile);

        Criteria<Image, Joints[]> criteria =
                Criteria.builder()
                        .setTypes(Image.class, Joints[].class)
                        .optModelUrls("djl://ai.djl.pytorch/yolo11n-pose")
                        .optTranslatorFactory(new YoloPoseTranslatorFactory())
                        .build();

        try (ZooModel<Image, Joints[]> pose = criteria.loadModel();
                Predictor<Image, Joints[]> predictor = pose.newPredictor()) {
            Joints[] allJoints = predictor.predict(img);
            for (Joints joints : allJoints) {
                img.drawJoints(joints);
            }
            CvUtil.showImage(img, "Pose Estimation");
            return allJoints;
        }
    }
    
    // 保留原有保存图片方法，如不需要可删除
    private static void saveJointsImage(Image img, Joints[] allJoints) throws IOException {
        Path outputDir = Paths.get("target");
        Files.createDirectories(outputDir);
        for (Joints joints : allJoints) {
            img.drawJoints(joints);
        }
        Path imagePath = outputDir.resolve("joints.png");
        img.save(Files.newOutputStream(imagePath), "png");
        LOGGER.info("Pose image has been saved in: {}", imagePath);
    }

}