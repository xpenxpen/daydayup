package org.xpen.hello.pdf;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 演示用IText生成一个简单的pdf文件
 *
 */
public class ItextTest {

    /** Path to the resulting PDF file. */
    public static final String RESULT = "target/hello.pdf";

    public static void main(String[] args) throws DocumentException, IOException {
        new ItextTest().createPdf(RESULT);
    }

    public void createPdf(String filename) throws DocumentException, IOException {
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        document.add(new Paragraph("Hello World!"));
        // step 5
        document.close();
    }
}