package org.xpen.cv.tesseract;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

/**
 * 演示tesseract文字识别
 * 需要下载tessdata https://github.com/tesseract-ocr/tessdata
 * 然后设置TESSDATA_PREFIX
 * javacv1.4后eclipse里已经可以直接运行，或者可以到命令行下运行
 * mvn package exec:java -Dexec.mainClass=org.xpen.cv.tesseract.TesseractTest -Dmaven.test.skip=true
 * 
 *
 */
public class TesseractTest {
    
    private static final String TESSDATA_PREFIX = "D:/git/opensource/tessdata/";


    public static void main(String[] args) throws Exception {
        
        
        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(TESSDATA_PREFIX, "eng") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
        PIX image = lept.pixRead("src/test/resources/cv/eurotext.png");
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        lept.pixDestroy(image);
    }

    
}
