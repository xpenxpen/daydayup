package org.xpen.hello.nlp;

import java.util.List;

import org.junit.Test;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

/**
 * HanLP分词
 * 需要自行下载hanlp数据包大约600MB
 * 需要设置src/test/resources/hanlp.properties第一行为下载的数据目录
 *
 */
public class HelloHanLp {

	@Test
	public void test() throws Exception {
        HanLP.Config.enableDebug();
		System.out.println(HanLP.segment("你好，欢迎使用HanLP汉语处理包！"));
		System.out.println(HanLP.segment("王国维和服务员"));
		System.out.println(HanLP.segment("王国维穿和服"));
		System.out.println(HanLP.segment("伊拉克维和部队"));
	}

	@Test
	public void testNLPTokenizer() throws Exception {
        HanLP.Config.enableDebug();
		System.out.println(HanLP.segment("我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
		System.out.println(NLPTokenizer.segment("我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
		//注意观察下面两个“希望”的词性、两个“晚霞”的词性
		System.out.println(NLPTokenizer.analyze("我的希望是希望张晚霞的背影被晚霞映红").translateLabels());
		System.out.println(NLPTokenizer.analyze("支援臺灣正體香港繁體：微软公司於1975年由比爾·蓋茲和保羅·艾倫創立。"));
	}

	@Test
	public void testCrf() throws Exception {
        HanLP.Config.enableDebug();
        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
        String[] tests = new String[]{
            "商品和服务",
            "上海华安工业（集团）公司董事长谭旭光和秘书胡花蕊来到美国纽约现代艺术博物馆参观",
            "微软公司於1975年由比爾·蓋茲和保羅·艾倫創立，18年啟動以智慧雲端、前端為導向的大改組。" // 支持繁体中文
        };
        for (String sentence : tests) {
            System.out.println(analyzer.analyze(sentence));
        }
    }
	
	@Test
	public void testJpName() throws Exception {
		String[] testCase = new String[]{
		        "北川景子参演了林诣彬导演的《速度与激情3》",
		        "林志玲亮相网友:确定不是波多野结衣？",
		};
		Segment segment1 = HanLP.newSegment();
		Segment segment2 = HanLP.newSegment().enableJapaneseNameRecognize(true);
		for (String sentence : testCase) {
		    List<Term> termList = segment1.seg(sentence);
		    System.out.println(termList);
		    
		    termList = segment2.seg(sentence);
		    System.out.println(termList);
		}		
	}
	
	@Test
	public void testSimplfiedTranditionChineseConvert() throws Exception {
        System.out.println(HanLP.convertToTraditionalChinese("用笔记本电脑写程序"));
        System.out.println(HanLP.convertToSimplifiedChinese("「以後等妳當上皇后，就能買士多啤梨慶祝了」"));
        System.out.println(HanLP.convertToSimplifiedChinese("用筆記本電腦寫程式"));
	}
	
	@Test
	public void testDependencyParser() throws Exception {
        CoNLLSentence sentence = HanLP.parseDependency("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--)
        {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null)
        {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
	}

}
