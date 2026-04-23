package org.xpen.hello.dl.djl.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.huggingface.translator.FillMaskTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 使用DistilBERT做填空
 * 
 */
public class FillMask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FillMask.class);

    public static void main(String[] args) throws Exception {
        String[] inputs = {
            "Hello I'm a [MASK] model.",
            "生活的真谛是[MASK]。",
            "[MASK]山は高くて有名です",
            "[MASK]山很高很有名",
            "千树万树[MASK]花开",
    	};

        Criteria<String, Classifications> criteria =
                Criteria.builder()
						.optApplication(Application.NLP.FILL_MASK)
                        .setTypes(String.class, Classifications.class)
                        .optModelUrls("djl://ai.djl.huggingface.pytorch/distilbert-base-multilingual-cased")
                        .optEngine("PyTorch")
                        .optTranslatorFactory(new FillMaskTranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<String, Classifications> model = criteria.loadModel();
                Predictor<String, Classifications> predictor = model.newPredictor()) {
        	for (String input : inputs) {
	            Classifications res = predictor.predict(input);
	            LOGGER.info("{}, {}", input, res);
        	}
        }
    }
}
