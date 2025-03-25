package org.xpen.cv.opencv;

import static org.bytedeco.opencv.global.opencv_face.drawFacemarks;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.helper.opencv_core.RGB;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2fVector;
import org.bytedeco.opencv.opencv_core.Point2fVectorVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_face.FacemarkKazemi;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

/**
 * kazemiFacemark演示
 * 出处: javacv官方例子
 *
 * 下载Kazemi model : 
 * https://raw.githubusercontent.com/opencv/opencv_3rdparty/contrib_face_alignment_20170818/face_landmark_model.dat
 */
public class FacemarkKazemiTest {
	
    // cascade definition for face detection
    private static final String CASCADE_FILE = "D:/git/opensource/flandmark/data/haarcascade_frontalface_alt.xml";
    private static final String LANDMARK_MODEL = "D:/git/opensource/flandmark/face_landmark_model.dat";

    private static final String IN_FOLDER = "src/test/resources/cv/";
    
    private static CascadeClassifier faceDetector;
    private static FacemarkKazemi facemark;

	public static void main(String[] args) throws Exception {
		
        faceDetector = new CascadeClassifier(CASCADE_FILE);
        facemark = FacemarkKazemi.create();
        facemark.loadModel(LANDMARK_MODEL);
        
        go("lena.jpg");
        go("group.jpg");

	}

	private static void go(String fileName) throws Exception {
        //load an image
        System.out.println("Loading image from " + IN_FOLDER + fileName);
        Mat img = imread(IN_FOLDER + fileName);
        
        //convert to gray scale and equalize histogram for better detection
        Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);
        equalizeHist(gray, gray);
       
        //Find face
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces);
        System.out.println("Found " + faces.size() + " face(s)");
        
        // Variable for landmarks. 
        // Landmarks for one face is a vector of points
        // There can be more than one face in the image.
        Point2fVectorVector landmarks = new Point2fVectorVector();

        //Run landmark detector
        boolean success = facemark.fit(img, faces, landmarks);
        
        if (success) {
            for (long i = 0; i < landmarks.size(); i++) {
                Point2fVector v = landmarks.get(i);
                drawFacemarks(img, v, Scalar.YELLOW);
            }
        }
        
        for (int i = 0; i < faces.size(); i++) {
            Rect r = faces.get(i);
            int[] bbox = new int[4];
            bbox[0] = r.x();
            bbox[1] = r.y();
            bbox[2] = r.x() + r.width();
            bbox[3] = r.y() + r.height();
            rectangle(img, new Point(bbox[0], bbox[1]), new Point(bbox[2], bbox[3]), RGB(255, 0, 0));
        }

        //Display results 
        //imshow("Kazemi Facial Landmark", img);
        //cvWaitKey(0);
        show(img, "output");
        //Save results
        //imwrite ("kazemi_landmarks.jpg", img);
	}

    private static void show(final Mat image, final String title) {
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        canvas.showImage(converter.convert(image));
    }

}
