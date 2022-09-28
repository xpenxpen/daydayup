package org.xpen.hello.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.jupiter.api.Test;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;

public class AnalyzerTest {

	@Test
	public void test01() {
		Analyzer a1 = new StandardAnalyzer();
		Analyzer a3 = new SimpleAnalyzer();
		Analyzer a4 = new WhitespaceAnalyzer();
		String txt = "this is my house, I come from Shanghai.";
		
		AnalyzerUtils.displayToken(txt, a1);
		AnalyzerUtils.displayToken(txt, a3);
		AnalyzerUtils.displayToken(txt, a4);
	}
	
	@Test
	public void test02() {
		Analyzer a1 = new StandardAnalyzer();
		Analyzer a3 = new SimpleAnalyzer();
		Analyzer a4 = new WhitespaceAnalyzer();
        Analyzer a5 = new CJKAnalyzer();
		Analyzer a6 = new MMSegAnalyzer();
		Analyzer a7 = new SmartChineseAnalyzer();
		String txt = "我来自中国上海华东师范大学。我不研究生命科学。我不是研究生。";
		
		AnalyzerUtils.displayToken(txt, a1);
		AnalyzerUtils.displayToken(txt, a3);
		AnalyzerUtils.displayToken(txt, a4);
		AnalyzerUtils.displayToken(txt, a5);
        AnalyzerUtils.displayToken(txt, a6);
        AnalyzerUtils.displayToken(txt, a7);
	}
	
	@Test
	public void test03() {
		Analyzer a1 = new StandardAnalyzer();
		Analyzer a2 = new EnglishAnalyzer();
		Analyzer a3 = new SimpleAnalyzer();
		Analyzer a4 = new WhitespaceAnalyzer();
		String txt = "how are you thank you";
		
		AnalyzerUtils.displayAllTokenInfo(txt, a1);
		System.out.println("------------------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a2);
		System.out.println("------------------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a3);
		System.out.println("------------------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a4);
		
	}
	
	@Test
	public void test04() {
		Analyzer a1 = new StandardAnalyzer();
		Analyzer a3 = new SimpleAnalyzer();
		Analyzer a4 = new WhitespaceAnalyzer();
        Analyzer a5 = new CJKAnalyzer();
		Analyzer a6 = new MMSegAnalyzer();
		Analyzer a7 = new SmartChineseAnalyzer();
		String txt = "王国维和服务员";
		
		AnalyzerUtils.displayToken(txt, a1);
		AnalyzerUtils.displayToken(txt, a3);
		AnalyzerUtils.displayToken(txt, a4);
		AnalyzerUtils.displayToken(txt, a5);
        AnalyzerUtils.displayToken(txt, a6);
        AnalyzerUtils.displayToken(txt, a7);
	}
}
