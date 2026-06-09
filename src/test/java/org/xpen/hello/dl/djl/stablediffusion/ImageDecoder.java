package org.xpen.hello.dl.djl.stablediffusion;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;

/**
 * 最后把生成好的 latent 解码回可保存的图片
 *
 */
public class ImageDecoder implements NoBatchifyTranslator<NDArray, Image> {

    /** {@inheritDoc} */
    @Override
    public NDList processInput(TranslatorContext ctx, NDArray input) throws Exception {
        input = input.div(0.18215);
        return new NDList(input);
    }

    /** {@inheritDoc} */
    @Override
    public Image processOutput(TranslatorContext ctx, NDList output) throws Exception {
    	//1. 把模型输出从[-1, 1]转成[0, 1]
        NDArray scaled = output.get(0).div(2).add(0.5).clip(0, 1);
        //2. 从 NCHW 转成 NHWC
        scaled = scaled.transpose(0, 2, 3, 1);
        //3. 映射到 0~255 并转成整型
        scaled = scaled.mul(255).round().toType(DataType.INT8, true).get(0);
        //4. 把神经网络输出的张量,整理成真正能保存/显示的图片对象
        return ImageFactory.getInstance().fromNDArray(scaled);
    }
}
