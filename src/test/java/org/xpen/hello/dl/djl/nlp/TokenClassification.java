package org.xpen.hello.dl.djl.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.huggingface.translator.TokenClassificationTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.translator.NamedEntity;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 词元分类
 * 命名实体识别(NER)
 * 支持2种模型，请自行切换测试比较模型的性能差异
 * 0: ml6team/bert-base-uncased-city-country-ner (约400M)
 * 1: dbmdz/bert-large-cased-finetuned-conll03-english (约1.2G)
 * 
 */
public class TokenClassification {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenClassification.class);

    public static void main(String[] args) throws Exception {
        int modelType = 1;
        String[] inputs = {
            "My name is Wolfgang and I live in Berlin.",
            "Bill Gates (Seattle, Washington) is an American computer programmer and entrepreneur who cofounded Microsoft Corporation",
    	};

        Criteria<String, NamedEntity[]> criteria;
        if (modelType == 0) {
            criteria = Criteria.builder()
        			.optApplication(Application.NLP.TOKEN_CLASSIFICATION)
                    .setTypes(String.class, NamedEntity[].class)
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/ml6team/bert-base-uncased-city-country-ner")
                    .optEngine("PyTorch")
                    .optTranslatorFactory(new TokenClassificationTranslatorFactory())
                    .optProgress(new ProgressBar())
                    .build();
		} else {
            criteria = Criteria.builder()
        			.optApplication(Application.NLP.TOKEN_CLASSIFICATION)
                    .setTypes(String.class, NamedEntity[].class)
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/dbmdz/bert-large-cased-finetuned-conll03-english")
                    .optEngine("PyTorch")
                    .optTranslatorFactory(new TokenClassificationTranslatorFactory())
                    .optProgress(new ProgressBar())
                    .build();
		}

        try (ZooModel<String, NamedEntity[]> model = criteria.loadModel();
                Predictor<String, NamedEntity[]> predictor = model.newPredictor()) {
        	for (String input : inputs) {
                LOGGER.info("input: {}", input);
	            NamedEntity[] nes = predictor.predict(input);
	            for (NamedEntity ne : nes) {
	                LOGGER.info("NamedEntity: {}", ne);
	            }
        	}
        }
    }
}
