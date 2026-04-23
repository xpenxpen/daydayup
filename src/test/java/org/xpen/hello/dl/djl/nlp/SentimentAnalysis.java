package org.xpen.hello.dl.djl.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 使用DistilBERT做情感分析
 * 
 */
public class SentimentAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalysis.class);

    public static void main(String[] args) throws Exception {
        SentimentAnalysis.predict();
    }

    public static void predict() throws Exception {
        String[] inputs = {
            "I like DJL. DJL is the best DL framework!",
            "The food is not delicious.",
			"The movie is so boring.",
			"The weather is good today.",
		};

        Criteria<String, Classifications> criteria =
                Criteria.builder()
						.optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                        .setTypes(String.class, Classifications.class)
                        .optModelUrls("djl://ai.djl.pytorch/distilbert")
                        .optEngine("PyTorch")
                        // This model was traced on CPU and can only run on CPU
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<String, Classifications> model = criteria.loadModel();
                Predictor<String, Classifications> predictor = model.newPredictor()) {
        	for (String input : inputs) {
            	Classifications predict = predictor.predict(input);
                LOGGER.info("input Sentence: {}, SentimentAnalysis: {}", input, predict);
        	}
        }
    }
}
