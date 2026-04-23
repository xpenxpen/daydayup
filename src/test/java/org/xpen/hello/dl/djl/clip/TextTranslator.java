package org.xpen.hello.dl.djl.clip;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;

public class TextTranslator implements NoBatchifyTranslator<String, float[]> {

    HuggingFaceTokenizer tokenizer;

    public TextTranslator() {
        tokenizer = HuggingFaceTokenizer.newInstance("openai/clip-vit-base-patch32");
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        return list.singletonOrThrow().toFloatArray();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        Encoding encoding = tokenizer.encode(input);
        NDArray attention = ctx.getNDManager().create(encoding.getAttentionMask());
        NDArray inputIds = ctx.getNDManager().create(encoding.getIds());
        NDArray placeholder = ctx.getNDManager().create("");
        placeholder.setName("module_method:get_text_features");
        return new NDList(inputIds.expandDims(0), attention.expandDims(0), placeholder);
    }
}
