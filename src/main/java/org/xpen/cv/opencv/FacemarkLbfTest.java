package org.xpen.cv.opencv;

import static org.bytedeco.opencv.global.opencv_face.drawFacemarks;
import static org.bytedeco.opencv.global.opencv_highgui.imshow;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;
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
import org.bytedeco.opencv.opencv_core.UMat;
import org.bytedeco.opencv.opencv_face.FacemarkLBF;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * LBFFacemark 摄像头/视频演示
 * 出处: javacv官方例子
 *
 * 下载LBF model : 
 * https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml
 */
public class FacemarkLbfTest {
	
    // cascade definition for face detection
    private static final String CASCADE_FILE = "D:/git/opensource/flandmark/data/haarcascade_frontalface_alt.xml";
    private static final String LANDMARK_MODEL = "D:/git/opensource/flandmark/lbfmodel.yaml";

    private static final String VIDEO_FILE = "D:/git/opensource/flandmark/data/seq_bruges04_300frames.avi";
    //private static final String VIDEO_FILE = "C:/ftptemp/B站下载/erge/宝宝巴士儿歌 3D版 第1集 拔萝卜.mp4";
    
    private static final int MODE_CAMERA = 1;
    private static final int MODE_VIDEO = 2;
    //可切换捕获摄像头或文件
    private static final int MODE = MODE_VIDEO;
    
    private static CascadeClassifier faceDetector;
    private static FacemarkLBF facemark;

	public static void main(String[] args) throws Exception {
		
        faceDetector = new CascadeClassifier(CASCADE_FILE);
        facemark = FacemarkLBF.create();
        facemark.loadModel(LANDMARK_MODEL);
        
        //Set up webcam for video capture
        VideoCapture video = null;
        try {
	        if (MODE == MODE_CAMERA) {
	        	video = new VideoCapture(0);
	        } else {
	        	video = new VideoCapture(VIDEO_FILE, Videoio.CAP_ANY);
	        }
	        //Variable to store a video frame and its grayscale 
	        Mat frame = new Mat();
	        
	        while(video.read(frame)) {
	        	go(frame);
	            // Exit loop if ESC is pressed
	            if (waitKey(1) == 27) {
	            	break;
	            }
	        }
        } finally {
	        video.release();
        }

	}

	private static void go(Mat frame) throws Exception {
        //convert to gray scale and equalize histogram for better detection
        UMat gray = new UMat();
        frame.copyTo(gray);
        cvtColor(gray, gray, COLOR_BGR2GRAY);
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
        boolean success = facemark.fit(frame, faces, landmarks);
        
        if (success) {
            for (long i = 0; i < landmarks.size(); i++) {
                Point2fVector v = landmarks.get(i);
                drawFacemarks(frame, v, Scalar.YELLOW);
            }
        }
        
        for (int i = 0; i < faces.size(); i++) {
            Rect r = faces.get(i);
            int[] bbox = new int[4];
            bbox[0] = r.x();
            bbox[1] = r.y();
            bbox[2] = r.x() + r.width();
            bbox[3] = r.y() + r.height();
            rectangle(frame, new Point(bbox[0], bbox[1]), new Point(bbox[2], bbox[3]), RGB(255, 0, 0));
        }

        //Display results 
        //imshow("Kazemi Facial Landmark", img);
        //cvWaitKey(0);
        imshow("Facial Landmark", frame);
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
