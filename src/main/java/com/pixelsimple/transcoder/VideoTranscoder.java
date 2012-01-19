/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;

/**
 *
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class VideoTranscoder {
	public static final String FFMPEG_PATH = "ffmpeg_path"; 
	public static final String INPUT_FILE_PATH_WITH_NAME = "input_file_name_with_path"; 
	public static final String OUTPUT_FILE_PATH_WITH_NAME = "output_file_name_with_path";
	
	static final Logger LOG = LoggerFactory.getLogger(VideoTranscoder.class);
	
	public void transcode(Map<String, String> params) {
		LOG.debug("transcode::VideoTranscoder params::{}", params);
		
		if (params == null || params.size() == 0) {
			throw new IllegalStateException("Pass a valid params in order to start a transcoding");
		}
		CommandRequest commandRequest = this.buildCommand(params);
		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(null);
		runner.runCommand(commandRequest, new CommandResponse());
		
		LOG.debug("transcode::requested transcoding in async mode");
	}

	/**
	 * @param params
	 * @return
	 */
	private CommandRequest buildCommand(Map<String, String> params) {
		ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
		
		String ffmpegPath = apiConfig.getFfmpegConfig().getExecutablePath(); 

		String videoInputPath = params.get(INPUT_FILE_PATH_WITH_NAME); 
		String videoOutputPath = params.get(OUTPUT_FILE_PATH_WITH_NAME);
		
//		String command = "C:\dev\pixelsimple\ffmpeg\32_bit\0.8\ffmpeg -y -i C:/Data/video_test/HTTYD_1-021_poor.mov -ar 22050 -ac 2 -vcodec flv C:/Data/video_test/transcoded/" + outputFileName;
		String command = ffmpegPath + " -y -i " + videoInputPath + " -ar 22050 -ac 2 -vcodec flv " + videoOutputPath;
		
		LOG.debug("buildCommand::built command::{}", command);
		
		return new CommandRequest().addCommand(command, 0);
	}

}
