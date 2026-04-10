package org.xpen.hello.dl.djl.cv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicdataset.cv.classification.Cifar10;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

/**
 * CIFAR10图像分类
 *
 */
public class Cifar10Classification {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(Cifar10Classification.class);

    public static void main(String[] args) throws IOException, ModelException, TranslateException {
        predict();
}

    public static void predict() throws IOException, ModelException, TranslateException {
    	String[] images = {"src/test/resources/dl/airplane1.png"};

        Criteria<Image, Classifications> criteria = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optModelUrls("djl://ai.djl.zoo/resnet/0.0.2/resnetv1")
                .optEngine("PyTorch")
                .optProgress(new ProgressBar())
                .build();

        Model model = criteria.loadModel();
        
        String userHome = System.getProperty("user.home");
        String synsetPath = userHome + "/.djl.ai/cache/repo/model/cv/image_classification/ai/djl/zoo/resnet/0.0.2/resnetv1/synset.txt";
        List<String> synset = Files.readAllLines(Paths.get(synsetPath));

        ImageClassificationTranslator translator =
                ImageClassificationTranslator.builder()
                        .addTransform(new ToTensor())
                        .addTransform(new Normalize(Cifar10.NORMALIZE_MEAN, Cifar10.NORMALIZE_STD))
                        .optSynset(synset)
                        .optApplySoftmax(true)
                        .build();

        try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
        	for (String image : images) {
				Path imageFile = Paths.get(image);
				Image img = ImageFactory.getInstance().fromFile(imageFile);
				Classifications classifications = predictor.predict(img);
				LOGGER.info("Image: {}, Classifications: {}", image, classifications);
        		
        	}
        	
        }
    }

}
