package org.xpen.hello.xml.xslt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * 演示了XSLT转换XML,并用flying-saucer转为PDF
 *
 */
public class XsltTest {
    
    private static final String OUTPUT_FOLDER = "target/xml/xslt/";
    
    //测试XSLT转换一个XML,并转为PDF
    @Test
    public void testTransform1() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("src/test/resources/xml/xslt/1/discussionForumHome.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File("src/test/resources/xml/xslt/1/discussionForumHome.xml"));
        File outputFolder = new File(OUTPUT_FOLDER + "1/");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        
        File output = new File(outputFolder, "discussionForumHome.html");
        FileOutputStream fos = new FileOutputStream(output);
        Result result = new StreamResult(fos);
        transformer.transform(text, result);
        
        //转为PDF
        convertHtmlToPdf(OUTPUT_FOLDER + "1/discussionForumHome.html", OUTPUT_FOLDER + "1/discussionForumHome.pdf");
    }
    
    //测试XSLT合并多个XML,并转为PDF
    @Test
    public void testTransform2() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        //由于 Java API 只接受一个源文档，因此必须从样式表自身装入辅助源文档。
        //这是通过 document() 函数实现的,见merge.xsl
        Source xslt = new StreamSource(new File("src/test/resources/xml/xslt/2/merge.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File("src/test/resources/xml/xslt/2/index.xml"));
        File outputFolder = new File(OUTPUT_FOLDER + "2/");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        
        File output = new File(outputFolder, "index.html");
        FileOutputStream fos = new FileOutputStream(output);
        Result result = new StreamResult(fos);
        transformer.transform(text, result);
        
        //复制图片
        FileUtils.copyFile(new File("src/test/resources/xml/xslt/2/geneva.jpg"), new File(outputFolder, "geneva.jpg"));
        FileUtils.copyFile(new File("src/test/resources/xml/xslt/2/london.jpg"), new File(outputFolder, "london.jpg"));
        FileUtils.copyFile(new File("src/test/resources/xml/xslt/2/paris.jpg"), new File(outputFolder, "paris.jpg"));
        FileUtils.copyFile(new File("src/test/resources/xml/xslt/2/roma.jpg"), new File(outputFolder, "roma.jpg"));
        
        //转为PDF
        convertHtmlToPdf(OUTPUT_FOLDER + "2/index.html", OUTPUT_FOLDER + "2/index.pdf");
    }
    
    private boolean convertHtmlToPdf(String inputFile, String outputFile) throws Exception {

        OutputStream os = new FileOutputStream(outputFile);
        ITextRenderer renderer = new ITextRenderer();
        String url = new File(inputFile).toURI().toURL().toString();

        renderer.setDocument(url);

        // 解决中文支持问题
        // ITextFontResolver fontResolver = renderer.getFontResolver();
        // fontResolver.addFont("C:/Windows/Fonts/SIMSUN.TTC",
        // BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        // 解决图片的相对路径问题
        // renderer.getSharedContext().setBaseURL("file:/D:/");
        renderer.layout();
        renderer.createPDF(os);

        os.flush();
        os.close();
        return true;
    }
 
}
