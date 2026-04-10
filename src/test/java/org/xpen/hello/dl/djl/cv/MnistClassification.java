package org.xpen.hello.dl.djl.cv;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

/**
 * MNIST手写数字分类
 *
 */
public final class MnistClassification {

	private static final String OUTPUT_DIR = "D:/ml/djl/hello/build/mlp";
	
    private static final Logger LOGGER = LoggerFactory.getLogger(MnistClassification.class);

    public static void main(String[] args) throws IOException, ModelException, TranslateException {
        Classifications classifications = predict();
        LOGGER.info("{}", classifications);
    }

    public static Classifications predict() throws IOException, ModelException, TranslateException {
        Path imageFile = Paths.get("src/test/resources/dl/0.png");
        Image img = ImageFactory.getInstance().fromFile(imageFile);

        String modelName = "mlp";
        try (Model model = Model.newInstance(modelName, "PyTorch")) {
            model.setBlock(new Mlp(28 * 28, 10, new int[] {128, 64}));

            // Assume you have run TrainMnist.java example, and saved model in build/model folder.
            Path modelDir = Paths.get(OUTPUT_DIR);
            model.load(modelDir);

            List<String> classes =
                    IntStream.range(0, 10).mapToObj(String::valueOf).collect(Collectors.toList());
            Translator<Image, Classifications> translator =
                    ImageClassificationTranslator.builder()
                            .addTransform(new ToTensor())
                            .optSynset(classes)
                            .optApplySoftmax(true)
                            .build();

            try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                return predictor.predict(img);
            }
        }
    }
}
