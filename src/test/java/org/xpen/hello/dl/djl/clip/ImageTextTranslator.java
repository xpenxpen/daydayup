package org.xpen.hello.dl.djl.clip;

import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Pair;

public class ImageTextTranslator implements NoBatchifyTranslator<Pair<Image, String>, float[]> {

    private ImageTranslator imgTranslator;
    private TextTranslator txtTranslator;

    public ImageTextTranslator() {
        this.imgTranslator = new ImageTranslator();
        this.txtTranslator = new TextTranslator();
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) throws Exception {
        NDArray logitsPerImage = list.get(0);
        return logitsPerImage.toFloatArray();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Pair<Image, String> input) throws Exception {
        NDList imageInput = imgTranslator.processInput(ctx, input.getKey());
        NDList textInput = txtTranslator.processInput(ctx, input.getValue());
        return new NDList(textInput.get(0), imageInput.get(0), textInput.get(1));
    }
}
