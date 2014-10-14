package org.xpen.hello.xml.xslt;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

public class XsltTest {
    
    //测试XSLT转换一个xml
    @Test
    public void testTransform1() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("src/test/resources/xml/xslt/1/discussionForumHome.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File("src/test/resources/xml/xslt/1/discussionForumHome.xml"));
        File output = new File("target/discussionForumHome.html");
        FileOutputStream fos = new FileOutputStream(output);
        Result result = new StreamResult(fos);
        transformer.transform(text, result);
    }
    
    //测试XSLT合并多个xml
    @Test
    public void testTransform2() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        //由于 Java API 只接受一个源文档，因此必须从样式表自身装入辅助源文档。
        //这是通过 document() 函数实现的,见merge.xsl
        Source xslt = new StreamSource(new File("src/test/resources/xml/xslt/2/merge.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File("src/test/resources/xml/xslt/2/index.xml"));
        File output = new File("target/index.html");
        FileOutputStream fos = new FileOutputStream(output);
        Result result = new StreamResult(fos);
        transformer.transform(text, result);
    }
}
