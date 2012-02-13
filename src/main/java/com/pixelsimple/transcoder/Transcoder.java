/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.Registrable;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.exception.TranscoderException;

/**
 *
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class Transcoder {
	static final Logger LOG = LoggerFactory.getLogger(Transcoder.class);
	
	private TranscodeCommandBuilderChain chain;
	
	public Transcoder() {
		chain = (TranscodeCommandBuilderChain) RegistryService.getRegisteredEntry(Registrable.TRANSCODE_COMMAND_CHAIN);
	}
	
	public void transcode(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException {
		// TODO: validate spec?
		if (spec == null) {
			throw new TranscoderException("Pass a valid TranscoderOutputSpec in order to start a transcoding");
		}
		LOG.debug("transcode::Transcoder with inputMedia::{} \nand spec::{}", inputMedia, 
				spec.getOutputFilePath() + spec.getOutputFileNameWithoutExtension());
		
		// TODO: validate spec?
		CommandRequest commandRequest = this.buildCommand(inputMedia, spec);
		
		if (commandRequest == null) {
			throw new TranscoderException("None of the builder in transcoder builder chain could create a builder. Verify profile");
		}
		
		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(null);
		runner.runCommand(commandRequest, new CommandResponse());
		
		LOG.debug("transcode::requested transcoding in async mode");
	}

	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		return this.chain.getChainStart().buildCommand(inputMedia, spec);
	}
}
