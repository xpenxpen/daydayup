package org.xpen.cv.tesseract;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.global.leptonica;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

/**
 * 演示tesseract文字识别
 * 中文识别准确率还不是很高
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
        
        String[] lans = new String[] {"eng", "chi_sim"};
        String[] pngs = new String[] {"eurotext.png", "chitext.png"};
        
        for (int i = 0; i < lans.length; i++) {
        	String lan = lans[i];
        	String png = pngs[i];

            TessBaseAPI api = new TessBaseAPI();
            // Initialize tesseract-ocr
            if (api.Init(TESSDATA_PREFIX, lan) != 0) {
                System.err.println("Could not initialize tesseract.");
                System.exit(1);
            }

            // Open input image with leptonica library
            PIX image = leptonica.pixRead("src/test/resources/cv/" + png);
            api.SetImage(image);
            // Get OCR result
            outText = api.GetUTF8Text();
            System.out.println("----------");
            String out = outText.getString();
            if (lan.equals("chi_sim")) {
            	out = out.replace(" ", "");
            }
            System.out.println("OCR output:\n" + out);

            // Destroy used object and release memory
            api.End();
            outText.deallocate();
            leptonica.pixDestroy(image);
            api.close();
        }
    }

    
}
