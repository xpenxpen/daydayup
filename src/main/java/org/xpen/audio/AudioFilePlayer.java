package org.xpen.audio;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
 
/**
 * 演示播放各种声音格式
 * ogg,mp3,ape,flac
 *
 */
public class AudioFilePlayer {
 
    public static void main(String[] args) {
        final AudioFilePlayer player = new AudioFilePlayer ();
        //player.play("d:/xiaomi.ogg");
        //player.play("D:/01 - 謎解きはディナーのあとで.mp3");
        player.play("D:/周杰伦-青花瓷.APE");
        //player.play("D:/周杰伦 - 听妈妈的话.flac");
    }
 
    public void play(String filePath) {
        final File file = new File(filePath);
 
        try {
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);
             
            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
 
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
 
            if (line != null) {
                line.open(outFormat);
                line.start();
                stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                line.drain();
                line.stop();
            }
 
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
 
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
 
    private void stream(AudioInputStream in, SourceDataLine line)
        throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}