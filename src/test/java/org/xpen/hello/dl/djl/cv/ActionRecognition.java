package org.xpen.hello.dl.djl.cv;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 动作识别
 *
 */
public class ActionRecognition {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRecognition.class);

	public static void main(String[] args) throws Exception {
        predict();
	}
	
    public static void predict() throws Exception {
    	String[] images = {"src/test/resources/dl/action_dance.jpg",
    			"src/test/resources/dl/action_discus_throw.png"};

        Criteria<URL, Classifications> criteria =
                Criteria.builder()
                        .setTypes(URL.class, Classifications.class)
                        .optModelUrls(
                                "djl://ai.djl.pytorch/Human-Action-Recognition-VIT-Base-patch16-224")
                        .optEngine("PyTorch")
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<URL, Classifications> inception = criteria.loadModel();
                Predictor<URL, Classifications> action = inception.newPredictor()) {
        	for (String image : images) {
				Path imageFile = Paths.get(image);
	            URL url = imageFile.toUri().toURL();
	            Classifications classification =  action.predict(url);
				LOGGER.info("Image: {}, Classifications: {}", image, classification);
        	}
        	
        }
    }

}
