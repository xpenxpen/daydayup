package org.xpen.hello.xml.decentxml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.core.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 演示JDK自带的Transformer无法保留原来xml的空格
 *
 */
public class DomTest {

    public static void main(String[] args) {

        try {
            String infilepath = "src/test/resources/xml/decentxml/file.xml";
            String outfilepath = "target/xml/xmlOutDom.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(infilepath);

            // Get the root element
            Node company = doc.getFirstChild();

            // Get the staff element , it may not working if tag has spaces, or
            // whatever weird characters in front...it's better to use
            // getElementsByTagName() to get it directly.
            // Node staff = company.getFirstChild();

            // Get the staff element by tag name directly
            Node staff = doc.getElementsByTagName("staff").item(0);
            
            NodeList childNodes = staff.getChildNodes();
            for (int i =0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                System.out.println(item.getNodeType()+"-->"+item.getNodeName()+"->"+item.getNodeValue());
            }
            
            //last one is #text node
            Node newLineSpaceNode = childNodes.item(childNodes.getLength()-1);
            Node cloneSpaceNode = newLineSpaceNode.cloneNode(false);
            Node cloneSpaceNode2 = newLineSpaceNode.cloneNode(false);

            // update staff attribute
            NamedNodeMap attr = staff.getAttributes();
            Node nodeAttr = attr.getNamedItem("id");
            nodeAttr.setTextContent("2");

            // append a new node to staff
            Element age = doc.createElement("age");
            age.appendChild(doc.createTextNode("28"));
            staff.appendChild(age);
            staff.appendChild(cloneSpaceNode);
            
            Element age2 = doc.createElement("age2");
            age2.appendChild(doc.createTextNode("282"));
            staff.appendChild(age2);
            staff.appendChild(cloneSpaceNode2);

            // loop the staff child node
            NodeList list = staff.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                // get the salary element, and update the value
                if ("salary".equals(node.getNodeName())) {
                    node.setTextContent("2000000");
                }

                // remove firstname
                if ("firstname".equals(node.getNodeName())) {
                    staff.removeChild(node);
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File outFile = new File(outfilepath);
            FileUtils.mkdir(outFile.getParentFile(), true);
            StreamResult result = new StreamResult(outFile);
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
    }
}