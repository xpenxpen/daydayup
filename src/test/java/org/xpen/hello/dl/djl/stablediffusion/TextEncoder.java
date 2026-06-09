package org.xpen.hello.dl.djl.stablediffusion;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.NoBatchifyTranslator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;

/**
 * 把文本转成 token / embedding 输入
 * 
 */
public class TextEncoder implements NoBatchifyTranslator<String, NDList> {

    private static final int MAX_LENGTH = 77;

    HuggingFaceTokenizer tokenizer;

    /** {@inheritDoc} */
    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        tokenizer =
                HuggingFaceTokenizer.builder()
                        .optPadding(true)
                        .optPadToMaxLength()
                        .optMaxLength(MAX_LENGTH)
                        .optTruncation(true)
                        .optTokenizerName("openai/clip-vit-base-patch32")
                        .build();
    }

    /** {@inheritDoc} */
    @Override
    public NDList processOutput(TranslatorContext ctx, NDList list) {
        list.detach();
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        Encoding encoding = tokenizer.encode(input);
        Shape shape = new Shape(1, encoding.getIds().length);
        return new NDList(ctx.getNDManager().create(encoding.getIds(), shape));
    }
}
