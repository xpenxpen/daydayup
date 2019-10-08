package org.xpen.cv.opencv;

//import static org.bytedeco.flandmark.global.flandmark.flandmark_detect;
//import static org.bytedeco.flandmark.global.flandmark.flandmark_free;
//import static org.bytedeco.flandmark.global.flandmark.flandmark_init;
//import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;
//import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
//import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
//import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
//import static org.bytedeco.opencv.global.opencv_core.cvGetSeqElem;
//import static org.bytedeco.opencv.global.opencv_core.cvGetTickCount;
//import static org.bytedeco.opencv.global.opencv_core.cvGetTickFrequency;
//import static org.bytedeco.opencv.global.opencv_core.cvPoint;
//import static org.bytedeco.opencv.global.opencv_core.cvReleaseMemStorage;
//import static org.bytedeco.opencv.global.opencv_core.cvScalar;
//import static org.bytedeco.opencv.global.opencv_core.cvSize;
//import static org.bytedeco.opencv.global.opencv_highgui.cvDestroyWindow;
//import static org.bytedeco.opencv.global.opencv_highgui.cvNamedWindow;
//import static org.bytedeco.opencv.global.opencv_highgui.cvShowImage;
//import static org.bytedeco.opencv.global.opencv_highgui.cvWaitKey;
//import static org.bytedeco.opencv.global.opencv_imgcodecs.cvConvertImage;
//import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
//import static org.bytedeco.opencv.global.opencv_imgproc.CV_FILLED;
//import static org.bytedeco.opencv.global.opencv_imgproc.CV_FONT_HERSHEY_PLAIN;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvCircle;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvInitFont;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvPutText;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
//import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FOURCC;
//import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FPS;
//import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_COUNT;
//import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_HEIGHT;
//import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_WIDTH;
//import static org.bytedeco.opencv.global.opencv_videoio.cvCaptureFromAVI;
//import static org.bytedeco.opencv.global.opencv_videoio.cvCreateAVIWriter;
//import static org.bytedeco.opencv.global.opencv_videoio.cvGetCaptureProperty;
//import static org.bytedeco.opencv.global.opencv_videoio.cvQueryFrame;
//import static org.bytedeco.opencv.global.opencv_videoio.cvReleaseCapture;
//import static org.bytedeco.opencv.global.opencv_videoio.cvReleaseVideoWriter;
//import static org.bytedeco.opencv.global.opencv_videoio.cvWriteFrame;

import org.apache.commons.lang3.tuple.Pair;
import org.bytedeco.flandmark.FLANDMARK_Model;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.CvSize;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_imgproc.CvFont;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.CvCapture;
import org.bytedeco.opencv.opencv_videoio.CvVideoWriter;

/**
 * 视频人脸识别
 * 以及使用flandmark作进一步的眼睛，鼻子，嘴检测
 * 
 * 需要下载flandmark data https://github.com/uricamic/flandmark/tree/master/data
 * 然后设置CASCADE_FILE和FLANDMARK_MODEL
 * 需要下载xvid解码器并安装 https://www.xvid.com/
 * 
 * 
 * javacv1.4后eclipse里已经可以直接运行，或者可以到命令行下运行
 * mvn package exec:java -Dexec.mainClass=org.xpen.cv.opencv.VideoFaceDetection -Dexec.args="D:/git/opensource/flandmark/data/seq_bruges04_300frames.avi D:/git/opensource/flandmark/data/seq_bruges04_300frames-marked.avi" -Dmaven.test.skip=true
 * 
 *
 */
public class VideoFaceDetection {
    // cascade definition for face detection
    private static final String CASCADE_FILE = "D:/git/opensource/flandmark/data/haarcascade_frontalface_alt.xml";
    private static final String FLANDMARK_MODEL = "D:/git/opensource/flandmark/data/flandmark_model.dat";
    
    private static FLANDMARK_Model model;

    public static void main(String[] args) {
        
//        String flandmark_window = "flandmark_example2";
//        double t;
//        int ms;
//
//        String infname = null;
//        String outfname = null;
//        boolean video = false, savevideo = false;
//
//        CvVideoWriter writer = null;
//        int frameW = 0;
//        int frameH = 0;
//        int fourcc = 0;
//        int nframes = 0;
//        double vidfps = 0;
//        //int fourcc = CV_FOURCC('D', 'I', 'V', 'X');
//
//        CvCapture camera = null;  // The camera device.
//        IplImage frame = null;
//
//        if (args.length == 0) {
//            System.out.println("Missing arg: video file name");
//            System.exit(1);
//        }
//        
//        model = flandmark_init(FLANDMARK_MODEL);
//        if (model == null) {
//            System.out.println("Structure model wasn't created. Corrupted file flandmark_model.dat?");
//            System.exit(1);
//        }
//
//        if (args.length >= 1) {
//            infname = args[0];
//            video = true;
//            
//            Pair<IplImage, CvCapture> pair = getCameraFrame(camera, infname);
//
//            frame = pair.getLeft();
//            camera = pair.getRight();
//
//            frameH = (int)cvGetCaptureProperty(camera, CAP_PROP_FRAME_HEIGHT);
//            frameW = (int)cvGetCaptureProperty(camera, CAP_PROP_FRAME_WIDTH);
//            fourcc = (int)cvGetCaptureProperty(camera, CAP_PROP_FOURCC);
//            nframes = (int)cvGetCaptureProperty(camera, CAP_PROP_FRAME_COUNT);
//            vidfps = cvGetCaptureProperty(camera, CAP_PROP_FPS);
//            System.out.println("frameH=" + frameH + ", frameW=" + frameW + ", fourcc=" + fourcc
//                    + ", nframes=" + nframes + ", vidfps=" + vidfps);
//        }
//            
//        
//        if (args.length >= 2) {
//            outfname = args[1];
//            savevideo = true;
//            writer = cvCreateAVIWriter(outfname, fourcc, vidfps, new CvSize(frameW, frameH), 1);
//            //writer = cvCreateVideoWriter(outfname, fourcc, vidfps, cvSize(frameW, frameH));
//        }
//
//        cvNamedWindow(flandmark_window, 0);
//
//        // Load the HaarCascade classifier for face detection.
//        CascadeClassifier faceCascade = new CascadeClassifier(CASCADE_FILE);
//
//
//        int[] bbox = new int[4];
//        double[] landmarks = new double[2 * model.data().options().M()];
//        IplImage frame_bw = cvCreateImage(cvSize(frame.width(), frame.height()), IPL_DEPTH_8U, 1);
//
//        CvFont font = new CvFont();
//        cvInitFont(font, CV_FONT_HERSHEY_PLAIN, 1.0, 1.0, 0, 1, CV_AA);
//
//        int frameid = 0;
//        boolean flag = true;
//
//        while (flag) {
//            if (++frameid >= nframes-2) {
//                flag = false;
//                break;
//            }
//
//            t = (double)cvGetTickCount();
//            Pair<IplImage, CvCapture> pair = getCameraFrame(camera, infname);
//
//            frame = pair.getLeft();
//            camera = pair.getRight();
//
//            if (frame == null) {
//                flag = false;
//                break;
//            }
//
//            cvConvertImage(frame, frame_bw);
//            detectFaceInImage(frame, frame_bw, faceCascade, model, bbox, landmarks);
//
//            t = (double)cvGetTickCount() - t;
//            String fps = "" + 1000.0/( t/((double)cvGetTickFrequency() * 1000.0));
//            cvPutText(frame, fps, cvPoint(10, 40), font, cvScalar(255, 0, 0, 0));
//            cvShowImage(flandmark_window, frame );
//
//            cvWaitKey(10);
//
//            if (savevideo) {
//                cvWriteFrame(writer, frame);
//            }
//        }
//
//        cvReleaseCapture(camera);
//        cvReleaseHaarClassifierCascade(cascade);
//        cvReleaseVideoWriter(writer);
//        cvDestroyWindow(flandmark_window);
//        flandmark_free(model);

    }
    
//    private static Pair<IplImage, CvCapture> getCameraFrame(CvCapture camera, String filename) {
//        IplImage frame;
//        int w, h;
//    
//        // If the camera hasn't been initialized, then open it.
//        if (camera == null) {
//            
//            System.out.println("Acessing the video sequence ...");
//            camera = cvCaptureFromAVI(filename);
//    
//            if (camera == null) {
//                System.out.println("Couldn't access the camera.");
//                System.exit(1);
//            }
//    
//            // Get the first frame, to make sure the camera is initialized.
//            frame = cvQueryFrame(camera);
//            //frame = cvRetrieveFrame(camera);
//    
//            if (frame != null) {
//                w = frame.width();
//                h = frame.height();
//                System.out.printf("Got the camera at %dx%d resolution.\n", w, h);
//            }
//            //sleep(10);
//        }
//    
//        // Wait until the next camera frame is ready, then grab it.
//        frame = cvQueryFrame(camera);
//        //frame = cvRetrieveFrame(camera);
//        if (frame == null) {
//            System.err.println("Couldn't grab a camera frame.");
//            return Pair.of(frame, camera);
//        }
//        return Pair.of(frame, camera);
//    }
//
//    private static void detectFaceInImage(IplImage orig, IplImage input, CascadeClassifier faceCascade, FLANDMARK_Model model, int[] bbox, double[] landmarks) {
//        // Smallest face size.
//        CvSize minFeatureSize = cvSize(40, 40);
//        // How detailed should the search be.
//        float search_scale_factor = 1.1f;
//        CvMemStorage storage = CvMemStorage.create();
//    
//        // Detect all the faces in the greyscale image.
//        RectVector faces = new RectVector();
//        faceCascade.detectMultiScale(input, faces);
//        cvClearMemStorage(storage);
//        
//        long nFaces = faces.size();
//    
//        for (int iface = 0; iface < nFaces; iface++) {
//            Rect r = faces.get(i);
//    
//            bbox[0] = r.x();
//            bbox[1] = r.y();
//            bbox[2] = r.x() + r.width();
//            bbox[3] = r.y() + r.height();
//    
//            flandmark_detect(input, bbox, model, landmarks);
//    
//            // display landmarks
//            cvRectangle(orig, cvPoint(bbox[0], bbox[1]), cvPoint(bbox[2], bbox[3]), CV_RGB(255,0,0) );
//            cvRectangle(orig, cvPoint((int)bbox[0], (int)bbox[1]), cvPoint((int)bbox[2], (int)bbox[3]), CV_RGB(0, 0, 255));
//            cvCircle(orig, cvPoint((int)landmarks[0], (int)landmarks[1]), 3, CV_RGB(0, 0,255), CV_FILLED, 8, 0);
//            for (int i = 2; i < landmarks.length; i += 2) {
//                cvCircle(orig, cvPoint((int)landmarks[i], (int)landmarks[i+1]), 3, CV_RGB(255, 0, 0), CV_FILLED, 8, 0);
//            }
//        }
//    
//        cvReleaseMemStorage(storage);
//    }


}
