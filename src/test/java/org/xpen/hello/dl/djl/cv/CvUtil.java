package org.xpen.hello.dl.djl.cv;

import java.awt.image.BufferedImage;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameConverter;

import ai.djl.modality.cv.Image;

public class CvUtil {

    //弹窗显示图片（修正色彩通道）
    public static void showImage(Image img, String title) {
        BufferedImage bufferedImage = (BufferedImage) img.getWrappedImage();
        // 修正RGB->BGR，避免红蓝通道颠倒
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int bgr = (b << 16) | (g << 8) | r;
                bufferedImage.setRGB(x, y, (rgb & 0xff000000) | bgr);
            }
        }
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        canvas.showImage(converter.convert(bufferedImage));
    }

}
