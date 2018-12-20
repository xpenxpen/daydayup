package org.xpen.cv.opencv;

import static org.bytedeco.javacpp.flandmark.flandmark_detect;
import static org.bytedeco.javacpp.flandmark.flandmark_free;
import static org.bytedeco.javacpp.flandmark.flandmark_init;
import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_FILLED;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.flandmark.FLANDMARK_Model;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * 演示opencv人脸识别
 * 以及使用flandmark作进一步的眼睛，鼻子，嘴检测
 * 
 * 需要下载flandmark data https://github.com/uricamic/flandmark/tree/master/data
 * 然后设置CASCADE_FILE和FLANDMARK_MODEL
 * 
 * Use JavaCV to detect faces in an image, and save a marked-faces version of the image.
 * 出处 http://fivedots.coe.psu.ac.th/~ad/jg/nui07/index.html
 * @author Andrew Davison
 * 
 * javacv1.4后eclipse里已经可以直接运行，或者可以到命令行下运行
 * mvn package exec:java -Dexec.mainClass=org.xpen.cv.opencv.FaceDetection -Dmaven.test.skip=true
 * javacv1.4.1会崩溃，改用javacv1.4即可
 * 
 *
 */
public class FaceDetection {
    // cascade definition for face detection
    private static final String CASCADE_FILE = "D:/git/opensource/flandmark/data/haarcascade_frontalface_alt.xml";
    private static final String FLANDMARK_MODEL = "D:/git/opensource/flandmark/data/flandmark_model.dat";
    
    private static final int SCALE = 2;
    // scaling factor to reduce size of input image


    private static final String IN_FOLDER = "src/test/resources/cv/";
    private static final String OUT_FOLDER = "target/";
    
    private static FLANDMARK_Model model;

    public static void main(String[] args) {

        // preload the opencv_objdetect module to work around a known bug
        Loader.load(opencv_objdetect.class);
        model = flandmark_init(FLANDMARK_MODEL);
        if (model == null) {
            System.out.println("Structure model wasn't created. Corrupted file flandmark_model.dat?");
            System.exit(1);
        }


        go("lena.jpg");
        go("group.jpg");
        
        flandmark_free(model);

    }
    
    private static void go(String fileName) {
        // load an image
        System.out.println("Loading image from " + IN_FOLDER + fileName);
        IplImage origImg = cvLoadImage(IN_FOLDER + fileName);

        // convert to grayscale
        IplImage grayImg = cvCreateImage(cvGetSize(origImg), IPL_DEPTH_8U, 1);
        cvCvtColor(origImg, grayImg, CV_BGR2GRAY);

        // scale the grayscale (to speed up face detection)
        //IplImage smallImg = IplImage.create(grayImg.width() / SCALE, grayImg.height() / SCALE, IPL_DEPTH_8U, 1);
        //cvResize(grayImg, smallImg, CV_INTER_LINEAR);

        // equalize the small grayscale
        //cvEqualizeHist(smallImg, smallImg);

        // create temp storage, used during object detection
        CvMemStorage storage = CvMemStorage.create();

        // instantiate a classifier cascade for face detection
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
        System.out.println("Detecting faces...");
        CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        // CV_HAAR_DO_ROUGH_SEARCH);
        // 0);
        cvClearMemStorage(storage);

        // iterate over the faces and draw yellow rectangles around them
        int total = faces.total();
        System.out.println("Found " + total + " face(s)");
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            cvRectangle(origImg, cvPoint(r.x(), r.y()), // undo the scaling
                    cvPoint((r.x() + r.width()), (r.y() + r.height())), CvScalar.YELLOW, 6, CV_AA, 0);
            
            flandmark(r, grayImg, origImg);
        }

        if (total > 0) {
            System.out.println("Saving marked-faces version in " + OUT_FOLDER + fileName);
            cvSaveImage(OUT_FOLDER + fileName, origImg);
        }
        
        cvReleaseImage(origImg);
        cvReleaseImage(grayImg);
        //cvReleaseImage(grayImg);
    }

    //使用flandmark作进一步的眼睛，鼻子，嘴检测
    private static void flandmark(CvRect r, IplImage grayImg, IplImage origImg) {
        int[] bbox = new int[4];
        bbox[0] = r.x();
        bbox[1] = r.y();
        bbox[2] = r.x() + r.width();
        bbox[3] = r.y() + r.height();
        
        long ms = System.currentTimeMillis();
        double[] landmarks = new double[2 * model.data().options().M()];
        if (flandmark_detect(grayImg, bbox, model, landmarks) != 0) {
            System.out.println("Error during detection.");
        }
        ms = System.currentTimeMillis() - ms;
        System.out.println("Landmarks detected in " + ms + " ms.");

        double[] bb = new double[4];
        model.bb().get(bb);
        cvRectangle(origImg, cvPoint(bbox[0], bbox[1]), cvPoint(bbox[2], bbox[3]), CV_RGB(255, 0, 0));
        cvRectangle(origImg, cvPoint((int)bb[0], (int)bb[1]), cvPoint((int)bb[2], (int)bb[3]), CV_RGB(0, 0, 255));
        cvCircle(origImg, cvPoint((int)landmarks[0], (int)landmarks[1]), 3, CV_RGB(0, 0, 255), CV_FILLED, 8, 0);
        for (int i = 2; i < landmarks.length; i += 2) {
            cvCircle(origImg, cvPoint((int)landmarks[i], (int)landmarks[i+1]), 3, CV_RGB(255, 0, 0), CV_FILLED, 8, 0);
        }
        System.out.print("detection = \t[");
        for (int ii = 0; ii < landmarks.length; ii += 2) {
            System.out.printf("%.2f ", landmarks[ii]);
        }
        System.out.println("]");
        System.out.print("\t\t[");
        for (int ii = 1; ii < landmarks.length; ii += 2) {
            System.out.printf("%.2f ", landmarks[ii]);
        }
        System.out.println("]");
    }

}
