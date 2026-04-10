package org.xpen.hello.nlp;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

/**
 * HanLP简单演示
 * 需要自行下载hanlp数据包大约600MB
 * 需要设置src/test/resources/hanlp.properties第一行为下载的数据目录
 *
 */
public class HelloHanLp {
    
	@Test
	public void testHello() throws Exception {
        HanLP.Config.enableDebug();
		System.out.println(HanLP.segment("你好，欢迎使用HanLP汉语处理包！"));
		System.out.println(HanLP.segment("王国维和服务员"));
		System.out.println(HanLP.segment("王国维穿和服"));
		System.out.println(HanLP.segment("伊拉克维和部队"));
	}

	/**
	 * NLP分词
	 */
	@Test
	public void testNLPTokenizer() throws Exception {
        HanLP.Config.enableDebug();
		System.out.println(HanLP.segment("我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
		System.out.println(NLPTokenizer.segment("我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
		//注意观察下面两个“希望”的词性、两个“晚霞”的词性
		System.out.println(NLPTokenizer.analyze("我的希望是希望张晚霞的背影被晚霞映红").translateLabels());
		System.out.println(NLPTokenizer.analyze("支援臺灣正體香港繁體：微软公司於1975年由比爾·蓋茲和保羅·艾倫創立。"));
	}

	/**
	 * CRF分词
	 */
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
	
	/**
	 * 中国人名识别
	 * 目前分词器基本上都默认开启了中国人名识别，比如HanLP.segment()接口中使用的分词器等等，
	 * 用户不必手动开启
	 * 代码只是为了强调
	 */
	@Test
	public void testChName() throws Exception {
		String[] testCase = new String[]{
		        "签约仪式前，秦光荣、李纪恒、仇和等一同会见了参加签约的企业家。",
		        "王国强、高峰、汪洋、张朝阳光着头、韩寒、小四",
		        "张浩和胡健康复员回家了",
		        "王总和小丽结婚了",
		        "编剧邵钧林和稽道青说",
		        "这里有关天培的有关事迹",
		        "龚学平等领导,邓颖超生前",
		        };
		Segment segment = HanLP.newSegment().enableNameRecognize(true);
		for (String sentence : testCase)
		{
		    List<Term> termList = segment.seg(sentence);
		    System.out.println(termList);
		}
	}
	
	/**
	 * 日本人名识别
	 */
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
	
	/**
	 * 地名识别
	 */
	@Test
	public void testPlace() throws Exception {
		String[] testCase = new String[]{
		        "武胜县新学乡政府大楼门前锣鼓喧天",
		        "蓝翔给宁夏固原市彭阳县红河镇黑牛沟村捐赠了挖掘机",
		        "世纪公园位于上海市浦东新区锦绣路1001号，东邻芳甸路，南临花木路，西北靠锦绣路",
		};
		Segment segment1 = HanLP.newSegment();
		Segment segment2 = HanLP.newSegment().enablePlaceRecognize(true);
		for (String sentence : testCase)
		{
		    List<Term> termList = segment1.seg(sentence);
		    System.out.println(termList);
		    
		    termList = segment2.seg(sentence);
		    System.out.println(termList);
		}
	}
	
	/**
	 * 机构名识别
	 */
	@Test
	public void testOrganization() throws Exception {
		String[] testCase = new String[]{
			    "我在上海林原科技有限公司兼职工作，",
			    "我经常在台川喜宴餐厅吃饭，",
			    "偶尔去地中海影城看电影。",
			    "参与北京电影学院和美国辛普森公司的活动 ，由交通银行北京分行与麦当劳赞助，巴政府和中共中央顾问委员会指导，中央电视台报道",
			    "济南杨铭宇餐饮管理有限公司是由杨先生创办的餐饮企业",
			    "上海振鼎鸡实业发展有限公司成立于1996年",
		};
		Segment segment1 = HanLP.newSegment();
		Segment segment2 = HanLP.newSegment().enableOrganizationRecognize(true);
		for (String sentence : testCase)
		{
		    List<Term> termList = segment1.seg(sentence);
		    System.out.println(termList);
		    
		    termList = segment2.seg(sentence);
		    System.out.println(termList);
		}
	}
	
	/**
	 * 关键词提取
	 */
	@Test
	public void testExtractKeyword() throws Exception {
		String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
		List<String> keywordList = HanLP.extractKeyword(content, 5);
		System.out.println(keywordList);
	}
	
	/**
	 * 自动摘要
	 */
	@Test
	public void testExtractSummary() throws Exception {
		String document = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
		        "算法可以宽泛的分为三类，\n" +
		        "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
		        "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
		        "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
		List<String> sentenceList = HanLP.extractSummary(document, 3);
		System.out.println(sentenceList);		
	}
	
	/**
	 * 简繁转换
	 */
	@Test
	public void testSimplfiedTranditionChineseConvert() throws Exception {
        System.out.println(HanLP.convertToTraditionalChinese("用笔记本电脑写程序"));
        System.out.println(HanLP.convertToSimplifiedChinese("「以後等妳當上皇后，就能買士多啤梨慶祝了」"));
        System.out.println(HanLP.convertToSimplifiedChinese("用筆記本電腦寫程式"));
	}
	
	/**
	 * 文本推荐
	 * (句子级别，从一系列句子中挑出与输入句子最相似的那一个)
	 */
	@Test
	public void testSuggester() throws Exception {
        Suggester suggester = new Suggester();
        String[] titleArray =
        (
                "威廉王子发表演说 呼吁保护野生动物\n" +
                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                "英报告说空气污染带来“公共健康危机”"
        ).split("\\n");
        for (String title : titleArray)
        {
            suggester.addSentence(title);
        }

        System.out.println(suggester.suggest("发言", 1));       // 语义
        System.out.println(suggester.suggest("危机公共", 1));   // 字符
        System.out.println(suggester.suggest("mayun", 1));      // 拼音		
	}
	
	/**
	 * 依存句法分析
	 */
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
