/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegVideoTranscodeCommandBuilder;

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
		LOG.debug("transcode::VideoTranscoder with inputMedia::{} \nand spec::{}", inputMedia, 
				spec.getOutputFilePath() + spec.getOutputFileNameWithoutExtension());
		
		// TODO: validate spec?

//		TranscodeCommandBuilder builder = TranscodeCommandFactory.createTranscodeCommandBuilder(spec);
//		CommandRequest commandRequest = builder.buildCommand(inputMedia, spec);
		
		CommandRequest commandRequest = new FfmpegVideoTranscodeCommandBuilder().buildCommand(inputMedia, spec);
		
		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(null);
		runner.runCommand(commandRequest, new CommandResponse());
		
		LOG.debug("transcode::requested transcoding in async mode");
	}
}
