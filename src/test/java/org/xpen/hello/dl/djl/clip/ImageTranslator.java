package org.xpen.hello.dl.djl.clip;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;

public class ImageTranslator implements NoBatchifyTranslator<Image, float[]> {

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        NDArray array = list.singletonOrThrow();
        return array.toFloatArray();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);

        float percent = 224f / Math.min(input.getWidth(), input.getHeight());
        int resizedWidth = Math.round(input.getWidth() * percent);
        int resizedHeight = Math.round(input.getHeight() * percent);

        array = NDImageUtils.resize(array, resizedWidth, resizedHeight, Image.Interpolation.BICUBIC);
        array = NDImageUtils.centerCrop(array, 224, 224);
        array = NDImageUtils.toTensor(array);
        NDArray placeholder = ctx.getNDManager().create("");
        placeholder.setName("module_method:get_image_features");
        return new NDList(array.expandDims(0), placeholder);
    }
}
