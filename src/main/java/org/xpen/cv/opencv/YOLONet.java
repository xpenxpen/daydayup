/*
YOLONet Example Information
---------------------------

This is a basic implementation of a YOLO object detection network inference example.
It works with most variants of YOLO (YOLOv2, YOLOv2-tiny, YOLOv3, YOLOv3-tiny, YOLOv3-tiny-prn, YOLOv4, YOLOv4-tiny).
YOLO9000 is not support by OpenCV DNN.

To run the example download the following files and place them in the root folder of your project:

    YOLOv4 Configuration: https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolov4.cfg
    YOLOv4 Weights: https://github.com/AlexeyAB/darknet/releases/download/darknet_yolo_v3_optimal/yolov4.weights
    COCO Names: https://raw.githubusercontent.com/AlexeyAB/darknet/master/data/coco.names
    Dog Demo Image: https://raw.githubusercontent.com/AlexeyAB/darknet/master/data/dog.jpg

For faster inferencing CUDA is highly recommended.
On CPU it is recommended to decrease the width & height of the network or use the tiny variants.
 */
package org.xpen.cv.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.getCudaEnabledDeviceCount;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.LINE_8;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.WindowConstants;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.StringVector;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.opencv_text.FloatVector;
import org.bytedeco.opencv.opencv_text.IntVector;

/**
 * YOLO v4演示目标检测
 *
 */
public class YOLONet {

    private static final String IN_FOLDER = "src/test/resources/cv/";

    /** 用于过滤boxes的score阈值*/
    private static final float SCORE_THRESHOLD = 0.5f;
    /** NMS阈值*/
    private static final float NMS_THRESHOLD = 0.4f;

	private Path configPath;
    private Path weightsPath;
    private Path namesPath;
    private int width;
    private int height;

    private Net net;
    private StringVector outNames;

    private List<String> names;
    private static YOLONet yolo;

    public static void main(String[] args) throws Exception {

        yolo = new YOLONet(
                "D:/down/yolov4.cfg",
                "D:/down/yolov4.weights",
                "D:/down/coco.names",
                608, 608);
        yolo.setup();
        
        go("dog.jpg");
        go("fruit.jpg");
        go("train.jpg");
        go("cat.jpg");
        go("horse.jpg");
    }

    private static void go(String fileName) {
        //load an image
        System.out.println("Loading image from " + IN_FOLDER + fileName);
        Mat image = imread(IN_FOLDER + fileName);

        List<ObjectDetectionResult> results = yolo.predict(image);

        System.out.printf("Detected %d objects:\n", results.size());
        Scalar fontColor = new Scalar(0, 255, 0, 0);
        for(ObjectDetectionResult result : results) {
            System.out.printf("\t%s - %.2f%%\n", result.className, result.confidence * 100f);

            // annotate on image
            opencv_imgproc.rectangle(image,
                    new Point(result.x, result.y),
                    new Point(result.x + result.width, result.y + result.height),
                    Scalar.MAGENTA, 2, LINE_8, 0);
            
            Point point2 = new Point(result.x, result.y);
            opencv_imgproc.putText(image, result.className,
            		point2, opencv_imgproc.CV_FONT_VECTOR0, 1.1, fontColor, 2, 0, false);
        }

        show(image, "YOLO");
	}

    /**
     * Creates a new YOLO network.
     * @param configPath Path to the configuration file.
     * @param weightsPath Path to the weights file.
     * @param namesPath Path to the names file.
     * @param width Width of the network as defined in the configuration.
     * @param height Height of the network as defined in the configuration.
     */
    public YOLONet(String configPath, String weightsPath, String namesPath, int width, int height) {
        this.configPath = Paths.get(configPath);
        this.weightsPath = Paths.get(weightsPath);
        this.namesPath = Paths.get(namesPath);
        this.width = width;
        this.height = height;
    }

    /**
     * Initialises the network.
     *
     * @return True if the network initialisation was successful.
     */
    public boolean setup() throws Exception {
    	//加载采用Darknet的配置网络和训练的权重参数
        net = opencv_dnn.readNetFromDarknet(
                configPath.toAbsolutePath().toString(),
                weightsPath.toAbsolutePath().toString());
        
        StringVector layerNames = net.getLayerNames();
        System.out.println("Layer count=" + layerNames.size());
//        for (int i = 0; i < layerNames.size(); i++) {
//        	 System.out.println(layerNames.get(i).getString());
//        }
       

        // setup output layers
        outNames = net.getUnconnectedOutLayersNames();
        for (int i = 0; i < outNames.size(); i++) {
       	 	System.out.println(outNames.get(i).getString());
        }

        // enable cuda backend if available
        if (getCudaEnabledDeviceCount() > 0) {
            net.setPreferableBackend(opencv_dnn.DNN_BACKEND_CUDA);
            net.setPreferableTarget(opencv_dnn.DNN_TARGET_CUDA);
        }

        // read names file
        names = Files.readAllLines(namesPath);

        return !net.empty();
    }

    /**
     * Runs the object detection on the frame.
     *
     * @param frame Input frame.
     * @return List of objects that have been detected.
     */
    public List<ObjectDetectionResult> predict(Mat frame) {
    	//将数据加入到模型之前，需要对数据进行tranform
    	Mat inputBlob = opencv_dnn.blobFromImage(frame,
                1 / 255.0,
                new Size(width, height),
                new Scalar(0.0),
                true, false, CV_32F);

        net.setInput(inputBlob);

        // run detection
        MatVector outs = new MatVector(outNames.size());
        //前向传递
        net.forward(outs, outNames);

        // evaluate result
        List<ObjectDetectionResult> result = postprocess(frame, outs);

        // cleanup
        outs.releaseReference();
        inputBlob.release();

        return result;
    }

    /**
     * Remove the bounding boxes with low confidence using non-maxima suppression
     *
     * @param frame Input frame
     * @param outs  Network outputs
     * @return List of objects
     */
    private List<ObjectDetectionResult> postprocess(Mat frame, MatVector outs) {
        for (int i = 0; i < outs.size(); ++i) {
        	Mat result = outs.get(i);
        	System.out.println(result);
        }
        
        //检测到的对象的类标签
    	final IntVector classIds = new IntVector();
        //置信度值，较低的置信度值表示该对象可能不是网络认为的对象。将过滤掉小于 0.5 阈值的对象
        final FloatVector confidences = new FloatVector();
        //对象的边界框
        final RectVector boxes = new RectVector();

        for (int i = 0; i < outs.size(); ++i) {
            // extract the bounding boxes that have a high enough score
            // and assign their highest confidence class prediction.
            Mat result = outs.get(i);
            FloatIndexer data = result.createIndexer();

            for (int j = 0; j < result.rows(); j++) {
                // minMaxLoc implemented in java because it is 1D
                int maxIndex = -1;
                float maxScore = Float.MIN_VALUE;
                for (int k = 5; k < result.cols(); k++) {
                    float score = data.get(j, k);
                    if (score > maxScore) {
                        maxScore = score;
                        maxIndex = k - 5;
                    }
                }

                if (maxScore > SCORE_THRESHOLD) {
                    int centerX = (int) (data.get(j, 0) * frame.cols());
                    int centerY = (int) (data.get(j, 1) * frame.rows());
                    int width = (int) (data.get(j, 2) * frame.cols());
                    int height = (int) (data.get(j, 3) * frame.rows());
                    int left = centerX - width / 2;
                    int top = centerY - height / 2;

                    classIds.push_back(maxIndex);
                    confidences.push_back(maxScore);

                    boxes.push_back(new Rect(left, top, width, height));
                    
                    System.out.println("增加候选:maxIndex=" + maxIndex
                    		+ "(" + names.get(maxIndex) + ")"
                    		+ ",maxScore=" + maxScore
                    		+ ",left=" + left + ",top=" + top
            				+ ",width=" + width + ",height=" + height);
                }
            }

            data.release();
            result.release();
        }

        // remove overlapping bounding boxes with NMS
        IntPointer indices = new IntPointer(confidences.size());
        FloatPointer confidencesPointer = new FloatPointer(confidences.size());
        confidencesPointer.put(confidences.get());

        //根据给定的检测boxes和对应的scores进行NMS(Non-Maximum Suppression,非极大值抑制)处理
        opencv_dnn.NMSBoxes(
        		boxes, confidencesPointer,
        		SCORE_THRESHOLD, NMS_THRESHOLD,
        		indices, 1.f, 0);

        // create result list
        List<ObjectDetectionResult> detections = new ArrayList<>();
        for (int i = 0; i < indices.limit(); ++i) {
            final int idx = indices.get(i);
            final Rect box = boxes.get(idx);

            final int clsId = classIds.get(idx);

            detections.add(new ObjectDetectionResult() {{
                classId = clsId;
                className = names.get(clsId);
                confidence = confidences.get(idx);
                x = box.x();
                y = box.y();
                width = box.width();
                height = box.height();
            }});

            box.releaseReference();
        }

        // cleanup
        indices.releaseReference();
        confidencesPointer.releaseReference();
        classIds.releaseReference();
        confidences.releaseReference();
        boxes.releaseReference();

        return detections;
    }

    private static void show(final Mat image, final String title) {
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        canvas.showImage(converter.convert(image));
    }

    /**
     * Dataclass for object detection result.
     */
    public static class ObjectDetectionResult {
        public int classId;
        public String className;

        public float confidence;

        public int x;
        public int y;
        public int width;
        public int height;
    }
}
