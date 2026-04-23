package org.xpen.hello.dl.djl.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.huggingface.translator.QuestionAnsweringTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.pytorch.zoo.nlp.qa.PtBertQATranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 抽取式问答
 * 支持3种模型，请自行切换测试比较模型的性能差异
 * 0: deepset/minilm-uncased-squad2 (约130M)
 * 1: distilbert-base-uncased-distilled-squad (约250M)
 * 2: mxnet bert_qa (约400M)
 * 
 */
public final class ExtractiveQa {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractiveQa.class);

    public static void main(String[] args) throws Exception {
        predict();
    }

    public static void predict() throws Exception {
        int modelType = 0;
   	
    	//每段文章都会有N个数量不等的问题
    	String[][][] inputs = {
    		{{"BBC Japan was a general entertainment Channel. "
                        + "Which operated between December 2004 and April 2006. "
                        + "It ceased operations after its Japanese distributor folded."},
    		 {"When did BBC Japan start broadcasting?"},
    		},
    		
    		{{"The Yuan dynasty (Chinese: 元朝; pinyin: Yuán Cháo), officially the Great Yuan (Chinese: 大元),"
                    + "was the empire or ruling dynasty of China established by Kublai Khan."},
    		 {"What is the Yuan dynasty's official name?",
              "What is the Chinese name for the Yuan dynasty?",
              "Who established the Yuan dynasty?",},
    		},
    		
    		{{"Bill Gates (Seattle, Washington) is an American computer programmer and entrepreneur "
    					+ "who cofounded Microsoft Corporation"},
   			 {"Who founded Microsoft",
			  "What is Bill Gates' occupation",
			  "Where was Bill Gates born",},
    		},
    	};

        Criteria<QAInput, String> criteria;
        if (modelType == 0) {
            criteria = Criteria.builder()
					.optApplication(Application.NLP.QUESTION_ANSWER)
                    .setTypes(QAInput.class, String.class)
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/deepset/minilm-uncased-squad2")
                    .optEngine("PyTorch")
                    .optTranslatorFactory(new QuestionAnsweringTranslatorFactory())
                    .optArgument("detail", true)
                    .optProgress(new ProgressBar())
                    .build();
		} else if (modelType == 1) {
            criteria = Criteria.builder()
					.optApplication(Application.NLP.QUESTION_ANSWER)
                    .setTypes(QAInput.class, String.class)
                    .optModelUrls("djl://ai.djl.pytorch/bertqa")
                    .optModelName("distilbert-base-uncased-distilled-squad")
                    .optEngine("PyTorch")
                    .optTranslatorFactory(new PtBertQATranslatorFactory())
                    .optProgress(new ProgressBar())
                    .build();
		} else {
			criteria = Criteria.builder()
				    .optApplication(Application.NLP.QUESTION_ANSWER)
				    .setTypes(QAInput.class, String.class)
				    .optEngine("MXNet")
				    .optProgress(new ProgressBar())
				    .build();
		}

        try (ZooModel<QAInput, String> model = criteria.loadModel();
                Predictor<QAInput, String> predictor = model.newPredictor()) {
        	for (String[][] inputs2 : inputs) {
				String paragraph = inputs2[0][0];
				for (int i = 0; i < inputs2[1].length; i++) {
					String question = inputs2[1][i];
					QAInput qaInput = new QAInput(question, paragraph);
					LOGGER.info("Question: {}", qaInput.getQuestion());
					String predict = predictor.predict(qaInput);
					LOGGER.info("predict: {}", predict);
				}
        	}
        }
    }
}
