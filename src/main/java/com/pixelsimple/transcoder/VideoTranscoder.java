/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;
import com.pixelsimple.commons.media.Container;

/**
 *
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class VideoTranscoder {
	static final Logger LOG = LoggerFactory.getLogger(VideoTranscoder.class);
	
	public void transcode(Container inputMedia, TranscoderOutputSpec spec) {
		// TODO: validate spec?
		if (spec == null) {
			throw new IllegalStateException("Pass a valid TranscoderOutputSpec in order to start a transcoding");
		}
		LOG.debug("transcode::VideoTranscoder with inputMedia::{} \nand spec::{}", inputMedia, spec.getOutputFileNameWithPath());
		
		// TODO: validate spec?

		CommandRequest commandRequest = this.buildCommand(inputMedia, spec);
		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(null);
		runner.runCommand(commandRequest, new CommandResponse());
		
		LOG.debug("transcode::requested transcoding in async mode");
	}
		
	/**
	 * @param params
	 * @return
	 */
	private CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
		
		String ffmpegPath = apiConfig.getFfmpegConfig().getExecutablePath(); 

		String videoInputPath = inputMedia.getFilePathWithName(); 
		String videoOutputPath = spec.getOutputFileNameWithPath();
		
//		String command = "C:\dev\pixelsimple\ffmpeg\32_bit\0.8\ffmpeg -y -i C:/Data/video_test/HTTYD_1-021_poor.mov -ar 22050 -ac 2 -vcodec flv C:/Data/video_test/transcoded/" + outputFileName;
		String command = ffmpegPath + " -y -i " + videoInputPath + " -ar 22050 -ac 2 -vcodec flv " + videoOutputPath;
		
		LOG.debug("buildCommand::built command::{}", command);
		
		return new CommandRequest().addCommand(command, 0);
	}

}
