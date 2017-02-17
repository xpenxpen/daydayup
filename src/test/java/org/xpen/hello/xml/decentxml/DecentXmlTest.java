package org.xpen.hello.xml.decentxml;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.XMLIOSource;
import de.pdark.decentxml.XMLParser;
import de.pdark.decentxml.XMLWriter;


/**
 * 演示decentxml可以在保留原来xml的空格的基础上修改xml
 *
 */
public class DecentXmlTest {

    public static void main(String[] args) throws Exception {
        String infilepath = "src/test/resources/xml/decentxml/file.xml";
        String outfilepath = "target/xml/xmlOutDecent.xml";
        XMLIOSource source = new XMLIOSource(new File(infilepath));
        XMLParser parser = new XMLParser();
        Document document = parser.parse(source);
        
        Element root = document.getRootElement();
        List<Element> elements = root.getChildren();
        System.out.println(elements.size());
        Element element = elements.get(0);
        element.addAttribute("star", "5");
        
        XMLWriter writer = new XMLWriter(new FileWriter(outfilepath));
        document.toXML(writer);
        writer.close();
    }

}
