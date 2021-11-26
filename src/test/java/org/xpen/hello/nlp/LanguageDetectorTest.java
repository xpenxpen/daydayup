package org.xpen.hello.nlp;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

/**
 * 演示语言检测
 *
 */
public class LanguageDetectorTest {

	@Test
	public void test() throws Exception {

		//加载模型
		InputStream is = new FileInputStream("D:/opensource/opennlp/langdetect-183.bin");
		LanguageDetectorModel model = new LanguageDetectorModel(is);
		LanguageDetector myCategorizer = new LanguageDetectorME(model);
		
		String inputTexts[] = new String[] {
				"你好世界，哈哈",
				"Hello World.",
				"こんにちは",
				"Bonjour. Un café.",
				"안녕하세요"
		};
		
		for (String txt : inputTexts) {
			Language bestLanguage = myCategorizer.predictLanguage(txt);
			System.out.println(txt);
			System.out.println("Best language: " + bestLanguage.getLang());
			System.out.println("Best language confidence: " + bestLanguage.getConfidence());
		}

	}

}
