package org.xpen.hello.dl.djl.cv;

import java.awt.image.BufferedImage;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;

import ai.djl.modality.cv.Image;

public class CvUtil {

    /** 弹窗显示图片（无需修正色彩通道）*/
    public static void showImage(Image img, String title) {
        BufferedImage bufferedImage = (BufferedImage) img.getWrappedImage();
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.showImage(bufferedImage);
    }

}
