package org.xpen.hello.bean.orika.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * freemarker演示
 * 自动生成flatterner
 * 根据PersonFlattener.xml的映射定义自动生成一个bean之间复制的代码
 *
 */
public class CodeGenTest {

    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        try {
            cfg.setDirectoryForTemplateLoading(new File(
                    "src/main/resources/bean/orika/ftl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        
        InputSource inputSource = new InputSource(CodeGenTest.class.getClassLoader().getResourceAsStream(
                "bean/orika/flattener/PersonFlattener.xml"));
        Document document = null;
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(CodeGenTest.class.getClassLoader().getResourceAsStream(
                    "bean/orika/flattener/PersonFlattener.xml"));
        } catch (ParserConfigurationException e2) {
            e2.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        String packageName = null;
        String className = null;
        try {
            packageName = xpath.evaluate("/flattener/package-name", document);
            className = xpath.evaluate("/flattener/class-name", document);
        } catch (XPathExpressionException e1) {
            e1.printStackTrace();
        }

        String packageDir = packageName.replaceAll("\\.", "/");

        NodeModel nodeModel= null;
        try {
            nodeModel = freemarker.ext.dom.NodeModel.parse(inputSource);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        try {
            Template template = cfg.getTemplate("MappingConfigurer.ftl");
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("doc",nodeModel);
            String baseDir = "src/test/java";
            
            //String className = "PersonMappingConfigurer3";
            File outputDir = new File(baseDir + "/" + packageDir);
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    System.out.println("Failed to create code gen dirs");
                }
            }
            
            File OutputFile = new File(outputDir, className + ".java");
            
            FileOutputStream fos = new FileOutputStream(OutputFile);
            Writer out = new OutputStreamWriter(fos);
            template.process(root, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        
        
    }

}
