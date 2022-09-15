package org.xpen.bilibili;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bytedeco.javacpp.Loader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * 将bilibili缓存下来的视频转换为mp4
 * 将video.m4s和audio.m4s合并转换为mp4格式
 *
 */
public class M4s2Mp4 {

	public static void main(String[] args) throws Exception {
		
		Path rootFolder = Paths.get("D:/tmp/qiaohu1/933573488");
		Stream<Path> list = Files.list(rootFolder);
		List<Path> folders = list.filter(p->p.toFile().isDirectory()).collect(Collectors.toList());
		
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

		for (Path path : folders) {
			Path entryJson = Path.of(path.toString(), "entry.json");
			Entry entry = mapper.readValue(entryJson.toFile(), Entry.class);
			System.out.println(entry.getPageData().getPart());
			
			Path m4sFolder = Path.of(path.toString(), "64");
			String video = Path.of(m4sFolder.toString(), "video.m4s").toString();
			String audio = Path.of(m4sFolder.toString(), "audio.m4s").toString();
			String out = Path.of(rootFolder.toString(), entry.getPageData().getPart() + ".mp4").toString();
		    ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", video, "-i", audio, out);
		    pb.inheritIO().start().waitFor();
		}
	}

}
