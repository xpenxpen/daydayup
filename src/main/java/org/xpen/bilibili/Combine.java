package org.xpen.bilibili;

import org.bytedeco.javacpp.Loader;

public class Combine {

	public static void main(String[] args) throws Exception {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        System.out.println(ffmpeg);
	    ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-f", "concat", "-i", "D:\\tmp\\qiaohu1\\420892692\\done\\list2.txt",
	    		"-c", "copy", "rz-aa.mp4");
	    pb.inheritIO().start().waitFor();

	}

}
