package org.xpen.hello.dl.djl.stablediffusion;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;

/**
 * 如果是图生图(mage-to-image),先把输入图片编码成 latent
 *
 */
public class ImageEncoder implements NoBatchifyTranslator<Image, NDArray> {

    private int height;
    private int width;

    public ImageEncoder(int height, int width) {
        this.height = height;
        this.width = width;
    }

    /** {@inheritDoc} */
    @Override
    public NDArray processOutput(TranslatorContext ctx, NDList list) throws Exception {
        NDArray result = list.singletonOrThrow();
        //Stable Diffusion latent 空间中常见的 scale，
        //目的是让 latent 的数值范围符合后续 UNet 和 VAE decoder 的预期
        result = result.mul(0.18215f);
        result.detach();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public NDList processInput(TranslatorContext ctx, Image input) throws Exception {
    	//1. 把图片转成 NDArray
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
        //2. 调整尺寸到 32 的倍数
        int[] size = resize32(height, width);
        //3. 从 HWC 转成 CHW
        array = NDImageUtils.resize(array, size[1], size[0]);
        array = array.transpose(2, 0, 1).div(255f); // HWC -> CHW RGB
        //4. 把像素范围从[0, 1]映射到[-1, 1]
        array = array.mul(2).sub(1);
        //5. 增加 batch 维度
        array = array.expandDims(0);
        return new NDList(array);
    }

    private int[] resize32(double h, double w) {
        double min = Math.min(h, w);
        if (min < 32) {
            h = 32.0 / min * h;
            w = 32.0 / min * w;
        }
        int h32 = (int) h / 32;
        int w32 = (int) w / 32;
        return new int[] {h32 * 32, w32 * 32};
    }
}
