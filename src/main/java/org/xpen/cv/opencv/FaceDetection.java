package org.xpen.cv.opencv;

//import static org.bytedeco.flandmark.global.flandmark.flandmark_detect;
//import static org.bytedeco.flandmark.global.flandmark.flandmark_free;
//import static org.bytedeco.flandmark.global.flandmark.flandmark_init;
//import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
//import static org.bytedeco.opencv.global.opencv_core.cvPoint;
//import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
//import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
//import static org.bytedeco.opencv.global.opencv_imgproc.CV_FILLED;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvCircle;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
//import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;
//import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;
//
//import org.bytedeco.flandmark.FLANDMARK_Model;
//import org.bytedeco.javacpp.Loader;
//import org.bytedeco.opencv.global.opencv_objdetect;
//import org.bytedeco.opencv.opencv_core.CvMemStorage;
//import org.bytedeco.opencv.opencv_core.IplImage;
//import org.bytedeco.opencv.opencv_core.Mat;
//import org.bytedeco.opencv.opencv_core.Rect;
//import org.bytedeco.opencv.opencv_core.RectVector;
//import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

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
 * javacv1.5以后改用新API，升级到opencv3以后API变化导致flandmark已经无法运行，且flandmark已经多年无人维护
 * 考虑改用FacemarkKazemiTest
 *
 */
@Deprecated
public class FaceDetection {
    // cascade definition for face detection
    private static final String CASCADE_FILE = "D:/git/opensource/flandmark/data/haarcascade_frontalface_alt.xml";
    private static final String FLANDMARK_MODEL = "D:/git/opensource/flandmark/data/flandmark_model.dat";
    
    private static final int SCALE = 2;
    // scaling factor to reduce size of input image


    private static final String IN_FOLDER = "src/test/resources/cv/";
    private static final String OUT_FOLDER = "target/";
    
//    private static FLANDMARK_Model model;

    public static void main(String[] args) {
//
//        // preload the opencv_objdetect module to work around a known bug
//        Loader.load(opencv_objdetect.class);
//        model = flandmark_init(FLANDMARK_MODEL);
//        if (model == null) {
//            System.out.println("Structure model wasn't created. Corrupted file flandmark_model.dat?");
//            System.exit(1);
//        }
//
//
//        go("lena.jpg");
//        go("group.jpg");
//        
//        flandmark_free(model);

    }
    
//    private static void go(String fileName) {
//        // load an image
//        System.out.println("Loading image from " + IN_FOLDER + fileName);
//        //IplImage origImg = cvLoadImage(IN_FOLDER + fileName);
//        Mat origImg = imread(IN_FOLDER + fileName);
//
//        // convert to grayscale
//        //IplImage grayImg = cvCreateImage(cvGetSize(origImg), IPL_DEPTH_8U, 1);
//        Mat grayImg = new Mat();
//        cvtColor(origImg, grayImg, CV_BGR2GRAY);
//
//        // scale the grayscale (to speed up face detection)
//        //IplImage smallImg = IplImage.create(grayImg.width() / SCALE, grayImg.height() / SCALE, IPL_DEPTH_8U, 1);
//        //cvResize(grayImg, smallImg, CV_INTER_LINEAR);
//
//        // equalize the small grayscale
//        //cvEqualizeHist(smallImg, smallImg);
//
//        // create temp storage, used during object detection
//        CvMemStorage storage = CvMemStorage.create();
//
//        // instantiate a classifier cascade for face detection
//        CascadeClassifier faceCascade = new CascadeClassifier(CASCADE_FILE);
//        System.out.println("Detecting faces...");
////        CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage, 1.1, 3, CASCADE_FIND_BIGGEST_OBJECT | CASCADE_DO_ROUGH_SEARCH);
////        // 0);
//        cvClearMemStorage(storage);
//        
//        RectVector faces = new RectVector();
//        faceCascade.detectMultiScale(grayImg, faces);
//
//        // iterate over the faces and draw yellow rectangles around them
//        long total = faces.size();
//        System.out.println("Found " + total + " face(s)");
//        final int[] bbox = new int[4];
//        final double[] landmarks = new double[2 * model.data().options().M()];
//        IplImage origIplImg = new IplImage(origImg);
//        IplImage grayIplImg = new IplImage(grayImg);
//        
//        for (int i = 0; i < total; i++) {
//            Rect rect = faces.get(i);
//            
//            flandmark(rect, grayIplImg, origIplImg);
//        }
//
//        if (total > 0) {
//            System.out.println("Saving marked-faces version in " + OUT_FOLDER + fileName);
//            cvSaveImage(OUT_FOLDER + fileName, origIplImg);
//        }
//        
//        //cvReleaseImage(origIplImg);
//        //cvReleaseImage(grayIplImg);
//    }
//
//    //使用flandmark作进一步的眼睛，鼻子，嘴检测
//    private static void flandmark(Rect r, IplImage grayImg, IplImage origImg) {
//        int[] bbox = new int[4];
//        bbox[0] = r.x();
//        bbox[1] = r.y();
//        bbox[2] = r.x() + r.width();
//        bbox[3] = r.y() + r.height();
//        
//        long ms = System.currentTimeMillis();
//        double[] landmarks = new double[2 * model.data().options().M()];
//        if (flandmark_detect(grayImg, bbox, model, landmarks) != 0) {
//            System.out.println("Error during detection.");
//        }
//        ms = System.currentTimeMillis() - ms;
//        System.out.println("Landmarks detected in " + ms + " ms.");
//
//        double[] bb = new double[4];
//        model.bb().get(bb);
//        cvRectangle(origImg, cvPoint(bbox[0], bbox[1]), cvPoint(bbox[2], bbox[3]), CV_RGB(255, 0, 0));
//        cvRectangle(origImg, cvPoint((int)bb[0], (int)bb[1]), cvPoint((int)bb[2], (int)bb[3]), CV_RGB(0, 0, 255));
//        cvCircle(origImg, cvPoint((int)landmarks[0], (int)landmarks[1]), 3, CV_RGB(0, 0, 255), CV_FILLED, 8, 0);
//        for (int i = 2; i < landmarks.length; i += 2) {
//            cvCircle(origImg, cvPoint((int)landmarks[i], (int)landmarks[i+1]), 3, CV_RGB(255, 0, 0), CV_FILLED, 8, 0);
//        }
//        System.out.print("detection = \t[");
//        for (int ii = 0; ii < landmarks.length; ii += 2) {
//            System.out.printf("%.2f ", landmarks[ii]);
//        }
//        System.out.println("]");
//        System.out.print("\t\t[");
//        for (int ii = 1; ii < landmarks.length; ii += 2) {
//            System.out.printf("%.2f ", landmarks[ii]);
//        }
//        System.out.println("]");
//    }

}
