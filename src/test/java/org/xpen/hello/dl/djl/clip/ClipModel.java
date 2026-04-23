package org.xpen.hello.dl.djl.clip;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.util.Pair;

import java.io.IOException;

public class ClipModel implements AutoCloseable {

    private ZooModel<NDList, NDList> clip;
    private Predictor<Image, float[]> imageFeatureExtractor;
    private Predictor<String, float[]> textFeatureExtractor;
    private Predictor<Pair<Image, String>, float[]> imgTextComparator;

    public ClipModel() throws ModelException, IOException {
        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelUrls("https://resources.djl.ai/demo/pytorch/clip.zip")
                        .optTranslator(new NoopTranslator())
                        .optEngine("PyTorch")
                        .optDevice(Device.cpu()) // torchscript model only support CPU
                        .optProgress(new ProgressBar())
                        .build();
        clip = criteria.loadModel();
        imageFeatureExtractor = clip.newPredictor(new ImageTranslator());
        textFeatureExtractor = clip.newPredictor(new TextTranslator());
        imgTextComparator = clip.newPredictor(new ImageTextTranslator());
    }

    public float[] extractTextFeatures(String inputs) throws TranslateException {
        return textFeatureExtractor.predict(inputs);
    }

    public float[] extractImageFeatures(Image inputs) throws TranslateException {
        return imageFeatureExtractor.predict(inputs);
    }

    public float[] compareTextAndImage(Image image, String text) throws TranslateException {
        return imgTextComparator.predict(new Pair<>(image, text));
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        imageFeatureExtractor.close();
        textFeatureExtractor.close();
        imgTextComparator.close();
        clip.close();
    }
}
