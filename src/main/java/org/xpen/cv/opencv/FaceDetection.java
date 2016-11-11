package org.xpen.cv.opencv;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * 演示人脸识别
 * Use JavaCV to detect faces in an image, and save a marked-faces version of the image.
 * 出处 http://fivedots.coe.psu.ac.th/~ad/jg/nui07/index.html
 * @author Andrew Davison
 * 
 * eclipse里无法运行，需要到命令行下运行
 * mvn package exec:java -Dexec.mainClass=org.xpen.cv.opencv.FaceDetection -Dmaven.test.skip=true
 * 
 *
 */
public class FaceDetection {
    private static final int SCALE = 2;
    // scaling factor to reduce size of input image

    // cascade definition for face detection
    private static final String CASCADE_FILE = "src/test/resources/cv/haarcascade_frontalface_alt.xml";

    private static final String IN_FOLDER = "src/test/resources/cv/";
    private static final String OUT_FOLDER = "target/";

    public static void main(String[] args) {

        // preload the opencv_objdetect module to work around a known bug
        Loader.load(opencv_objdetect.class);

        go("lena.jpg");
        go("group.jpg");
    }
    
    private static void go(String fileName) {
        // load an image
        System.out.println("Loading image from " + IN_FOLDER + fileName);
        IplImage origImg = cvLoadImage(IN_FOLDER + fileName);

        // convert to grayscale
        IplImage grayImg = cvCreateImage(cvGetSize(origImg), IPL_DEPTH_8U, 1);
        cvCvtColor(origImg, grayImg, CV_BGR2GRAY);

        // scale the grayscale (to speed up face detection)
        IplImage smallImg = IplImage.create(grayImg.width() / SCALE, grayImg.height() / SCALE, IPL_DEPTH_8U, 1);
        cvResize(grayImg, smallImg, CV_INTER_LINEAR);

        // equalize the small grayscale
        cvEqualizeHist(smallImg, smallImg);

        // create temp storage, used during object detection
        CvMemStorage storage = CvMemStorage.create();

        // instantiate a classifier cascade for face detection
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
        System.out.println("Detecting faces...");
        CvSeq faces = cvHaarDetectObjects(smallImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        // CV_HAAR_DO_ROUGH_SEARCH);
        // 0);
        cvClearMemStorage(storage);

        // iterate over the faces and draw yellow rectangles around them
        int total = faces.total();
        System.out.println("Found " + total + " face(s)");
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            cvRectangle(origImg, cvPoint(r.x() * SCALE, r.y() * SCALE), // undo the scaling
                    cvPoint((r.x() + r.width()) * SCALE, (r.y() + r.height()) * SCALE), CvScalar.YELLOW, 6, CV_AA, 0);
        }

        if (total > 0) {
            System.out.println("Saving marked-faces version in " + OUT_FOLDER + fileName);
            cvSaveImage(OUT_FOLDER + fileName, origImg);
        }
    }

}
