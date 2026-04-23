package org.xpen.hello.dl.djl.cv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.translator.StyleTransferTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

/**
 * 神经风格迁移
 * cyclegan
 * 
 */
public class StyleTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTransfer.class);

    public enum Artist {
        CEZANNE,   // 塞尚(Paul Cézanne),后印象派画家
        MONET,     // 莫奈(Claude Monet),印象派画家
        UKIYOE,    // 浮世绘,日本传统版画风格
        VANGOGH    // 梵高(Vincent van Gogh),后印象派画家
    }

    public static void main(String[] args) throws Exception {
        String imagePath = "src/test/resources/dl/mountains.png";
        Image input = ImageFactory.getInstance().fromFile(Paths.get(imagePath));
        
        for (Artist a : Artist.values()) {
            Image output = transfer(input, a);
            LOGGER.info("Using PyTorch Engine. {} painting generated", a);
            save(output, a.toString(), "target/cyclegan/");
        }
    }

    public static Image transfer(Image image, Artist artist) throws Exception {
        // Use DJL PyTorch model zoo model, model can be found:
        // https://mlrepo.djl.ai/model/cv/image_generation/ai/djl/pytorch/cyclegan/0.0.1/style_xxxx.zip

        String modelName = "style_" + artist.toString().toLowerCase(Locale.ROOT);
        String modelUrl = "djl://ai.djl.pytorch/cyclegan/0.0.1/" + modelName;
        Criteria<Image, Image> criteria =
                Criteria.builder()
                        .optApplication(Application.CV.IMAGE_GENERATION)
                        .setTypes(Image.class, Image.class)
                        .optModelUrls(modelUrl)
                        .optProgress(new ProgressBar())
                        .optTranslatorFactory(new StyleTransferTranslatorFactory())
                        .optEngine("PyTorch")
                        .build();

        try (ZooModel<Image, Image> model = criteria.loadModel();
                Predictor<Image, Image> styler = model.newPredictor()) {
            return styler.predict(image);
        }
    }

    public static void save(Image image, String name, String path) throws Exception {
        Path outputPath = Paths.get(path);
        Files.createDirectories(outputPath);
        Path imagePath = outputPath.resolve(name + ".png");
        image.save(Files.newOutputStream(imagePath), "png");
        
        CvUtil.showImage(image, name);
    }
}