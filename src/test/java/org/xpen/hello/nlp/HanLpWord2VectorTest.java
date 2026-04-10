package org.xpen.hello.nlp;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

/**
 * 语义距离,类推问题
 * 出处: https://github.com/hankcs/HanLP/wiki/word2vec
 *
 * 下载维基百科预训练词向量并自行替换MODEL_FILE_NAME: 
 * https://pan.baidu.com/s/1qYFozrY
 */
public class HanLpWord2VectorTest {
	
    private static final String TRAIN_FILE_NAME = "x";
    private static final String MODEL_FILE_NAME = "D:/opensource/hanlp/hanlp-wiki-vec-zh/hanlp-wiki-vec-zh.txt";
    
    private static WordVectorModel wordVectorModel;
    
    @BeforeAll
    public static void setUp() throws Exception {
    	wordVectorModel = trainOrLoadModel();
    }
	
	/**
	 * 语义距离
	 */
	@Test
	public void testWord2Vector() throws Exception {
        printNearest("中国", wordVectorModel);
        printNearest("美丽", wordVectorModel);
        printNearest("购买", wordVectorModel);
        printNearest("刘备", wordVectorModel);

        // 文档向量
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
        String[] documents = new String[]{
            "山东苹果丰收",
            "农民在江苏种水稻",
            "奥运会女排夺冠",
            "世界锦标赛胜出",
            "中国足球失败",
        };

        System.out.println(docVectorModel.similarity(documents[0], documents[1]));
        System.out.println(docVectorModel.similarity(documents[0], documents[4]));

        for (int i = 0; i < documents.length; i++) {
            docVectorModel.addDocument(i, documents[i]);
        }

        printNearestDocument("体育", documents, docVectorModel);
        printNearestDocument("农业", documents, docVectorModel);
        printNearestDocument("我要看比赛", documents, docVectorModel);
        printNearestDocument("要不做饭吧", documents, docVectorModel);
	}
	
	/**
	 * 词语类比
	 */
	@Test
	public void testAnalogy() throws Exception {
		printAnalogy("日本", "自民党", "共和党", wordVectorModel);
		printAnalogy("诸葛亮", "刘备", "曹操", wordVectorModel);
		printAnalogy("中国", "北京", "东京", wordVectorModel);
		printAnalogy("程序员", "男人", "女人", wordVectorModel);
		printAnalogy("医生", "男人", "女人", wordVectorModel);
		printAnalogy("国王", "男人", "女人", wordVectorModel);
		printAnalogy("普京", "俄罗斯", "美国", wordVectorModel);
	}
	
	private void printAnalogy(String a, String b, String c, WordVectorModel model) {
		System.out.println(model.analogy(a, b, c, 5));
	}
	
    private void printNearest(String word, WordVectorModel model) {
        System.out.printf("\n                                                Word     Cosine\n------------------------------------------------------------------------\n");
        for (Map.Entry<String, Float> entry : model.nearest(word, 5)) {
            System.out.printf("%50s\t\t%f\n", entry.getKey(), entry.getValue());
        }
    }

    private void printNearestDocument(String document, String[] documents, DocVectorModel model) {
        printHeader(document);
        for (Map.Entry<Integer, Float> entry : model.nearest(document)) {
            System.out.printf("%50s\t\t%f\n", documents[entry.getKey()], entry.getValue());
        }
    }

    private void printHeader(String query) {
        System.out.printf("\n%50s          Cosine\n------------------------------------------------------------------------\n", query);
    }

    private static WordVectorModel trainOrLoadModel() throws Exception {
        if (!IOUtil.isFileExisted(MODEL_FILE_NAME))
        {
            if (!IOUtil.isFileExisted(TRAIN_FILE_NAME))
            {
                System.err.println("语料不存在，请阅读文档了解语料获取与格式：https://github.com/hankcs/HanLP/wiki/word2vec");
                System.exit(1);
            }
            Word2VecTrainer trainerBuilder = new Word2VecTrainer();
            return trainerBuilder.train(TRAIN_FILE_NAME, MODEL_FILE_NAME);
        }

        return loadModel();
    }

    private static WordVectorModel loadModel() throws Exception {
        return new WordVectorModel(MODEL_FILE_NAME);
    }

}
