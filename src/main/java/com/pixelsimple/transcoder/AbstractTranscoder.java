/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.Registrable;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.exception.TranscoderException;

/**
 *
 * @author Akshay Sharma
 * Apr 6, 2012
 */
public abstract class AbstractTranscoder {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractTranscoder.class);
	// TODO: As usual, see if this can be injected someday.
	protected ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
	
	private TranscodeCommandBuilderChain chain;
	
	public AbstractTranscoder() {
		chain = (TranscodeCommandBuilderChain) RegistryService.getRegisteredEntry(Registrable.TRANSCODE_COMMAND_CHAIN);
		LOG.debug("transcode()::Registered chain : {} ", chain);
	}
	
	public Handle transcode(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException {
		// TODO: validate spec?
		if (spec == null) {
			throw new TranscoderException("Pass a valid TranscoderOutputSpec in order to start a transcoding");
		}
		LOG.debug("transcode::Transcoder with inputMedia::{} \nand spec::{}", inputMedia,
				spec.getOutputFileDir() + spec.getOutputFileNameWithoutExtension());
		
		// TODO: validate spec?

		return this.transcodeIt(inputMedia, spec);
	}
	
	protected CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException {
		spec.validate();
		
		LOG.debug("transbuildCommandcode()::Going to use chain start : {} ", chain.getChainStart());
		
		CommandRequest commandRequest = this.chain.getChainStart().buildCommand(inputMedia, spec);
		
		if (commandRequest == null) {
			throw new TranscoderException("None of the builder in transcoder builder chain could create a builder. Verify profile");
		}
		return commandRequest;
	}
	
	protected Handle buildHandle(TranscoderOutputSpec spec) {
		// TODO: Evaluate what can be a better handle id? The output file of transcoding should surely be unique right?
		Handle handle = new Handle(spec.getComputedOutputFileWithPath());
		handle.setOutputFileCreated(spec.getComputedOutputFileWithPath());
		
		return handle;
	}

	protected abstract Handle transcodeIt(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException;


}
