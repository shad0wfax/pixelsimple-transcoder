/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final String VIDEO_INPUT_PATH = "video_input_path"; 
	public static final String VIDEO_OUTPUT_PATH = "video_output_path"; 
	public static final String INPUT_FILE_NAME = "input_file_name"; 
	public static final String OUTPUT_FILE_NAME = "output_file_name";
	
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
		String ffmpegPath = params.get(FFMPEG_PATH); 
		String videoInputPath = params.get(VIDEO_INPUT_PATH); 
		String videoOutputPath = params.get(VIDEO_OUTPUT_PATH);
		String inputFileName = params.get(INPUT_FILE_NAME);
		String outputFileName = params.get(OUTPUT_FILE_NAME);
		
//		String command = "Z:/VmShare/Win7x64/Technology/ffmpeg/release_0.8_love/ffmpeg-git-78accb8-win32-static/bin/ffmpeg -y -i C:/Data/video_test/HTTYD_1-021_poor.mov -ar 22050 -ac 2 -vcodec flv C:/Data/video_test/transcoded/" + outputFileName;
		String command = ffmpegPath + "ffmpeg -y -i " + videoInputPath + inputFileName + " -ar 22050 -ac 2 -vcodec flv " + videoOutputPath + outputFileName;
		
		LOG.debug("buildCommand::built command::{}", command);
		
		return new CommandRequest().addCommand(command, 1);
	}

}
