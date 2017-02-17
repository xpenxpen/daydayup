package org.xpen.hello.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.junit.Test;

/**
 * 演示用fop生成一个带中文的pdf文件
 *
 */
public class FopTest {

    //演示用fop生成一个带中文的pdf文件
    @Test
    public void testChinese() throws Exception {
        
        // Step 1: Construct a FopFactory by specifying a reference to the configuration file
        // (reuse if you plan to render multiple documents!)
        FopFactory fopFactory = FopFactory.newInstance(new File("src/test/resources/pdf/fop/fop.xconf"));

        // Step 2: Set up output stream.
        File outFile = new File("target/pdf/abc.pdf");
        FileUtils.forceMkdirParent(outFile);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));

        // Step 3: Construct fop with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        // Step 4: Setup JAXP using identity transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("src/test/resources/pdf/fop/name2fo.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        // Step 5: Setup input and output for XSLT transformation
        // Setup input stream
        Source src = new StreamSource(new File("src/test/resources/pdf/fop/name.xml"));

        // Resulting SAX events (the generated FO) must be piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Step 6: Start XSLT transformation and FOP processing
        transformer.transform(src, res);

        // Clean-up
        out.close();
    }

}
