package org.xpen.hello.svg;

// CropCircles.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Translate crop circle notation into a SVG file.
 The language is based on "Crop Art, Part 2", 
 Andrew Glassner's Notebook, Nov/Dec 2004, IEEE Computer Graphics and Applications,
 available at http://www.glassner.com/andrew/cg/research/crop/crop.htm

 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class CropCircles {
    private static final String EXAMPS_DIR = "src/test/resources/svg/";
    private static final String OUT_DIR = "target/svg/";

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Supply a filename in " + EXAMPS_DIR);
            System.exit(0);
        }

        Parser p = new Parser(EXAMPS_DIR + args[0]);
        ProgramNode pNode = p.parse();

        Evaluator eval = new Evaluator();
        SVGGraphics2D svgGenerator = eval.genImage(pNode);

        String imFnm = outSVG(args[0]);
        File outDir = new File(OUT_DIR);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        saveImage(svgGenerator, OUT_DIR + imFnm);
        svg2png(imFnm);
    }

    private static void svg2png(String svgFileName) {
        try {
            String uri = Paths.get(OUT_DIR + svgFileName).toUri().toURL().toString();
            TranscoderInput input = new TranscoderInput(uri);
            OutputStream os = new FileOutputStream(OUT_DIR + svgFileName + ".png");
            TranscoderOutput output = new TranscoderOutput(os);              
            PNGTranscoder transcoder = new PNGTranscoder();        
            transcoder.transcode(input, output);
            os.flush();
            os.close();
        } catch (TranscoderException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void saveImage(SVGGraphics2D svgGenerator, String fnm) {
    // write to svg file
        try {
            OutputStream os = new FileOutputStream(new File(fnm));
            Writer out = new OutputStreamWriter(os, "UTF-8");
            svgGenerator.stream(out, true); // use css
            os.flush();
            os.close();
            System.out.println("Saved crop circles to " + fnm);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static String outSVG(String fnm) {
    // add "Out" to end of filename, adding a SVG extension
        int dotPosn = fnm.lastIndexOf('.');
        if (dotPosn == -1)
            return (fnm + "Out.svg");

        String name = fnm.substring(0, dotPosn);
        return (name + "Out.svg");
    }

}
