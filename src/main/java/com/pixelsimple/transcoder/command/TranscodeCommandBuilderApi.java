/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.registry.RegistryService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.config.TranscoderConfig;
import com.pixelsimple.transcoder.config.TranscoderRegistryKeys;
import com.pixelsimple.transcoder.profile.Profile;

/**
 * An API for custom transcode command building. Abstract enough but handles some of the transcode chain functionality:
 * 	like delegating to a successor and basic error checking.
 * An implementation would be to extend TranscodeCommandBuilderApi and implement the buildTranscodeCommand() method. 
 * The profile created should use this implementing custom class as the as the <customProfileCommandHandler>. 
 * @author Akshay Sharma
 * May 10, 2012
 */
public abstract class TranscodeCommandBuilderApi implements TranscodeCommandBuilder {
	static final Logger LOG = LoggerFactory.getLogger(TranscodeCommandBuilderApi.class);

	protected TranscodeCommandBuilder successor;
	
	// TODO: As usual, see if this can be injected someday.
	protected ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
	protected TranscoderConfig transcoderConfig = RegistryService.getRegisteredApiConfig()
			.getGenericRegistryEntry().getEntry(TranscoderRegistryKeys.TRANSCODER_CONFIG);
	
	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.TranscodeCommandBuilder#setSuccessor(com.pixelsimple.transcoder.command.TranscodeCommandBuilder)
	 */
	@Override
	public void setSuccessor(TranscodeCommandBuilder successor) {
		this.successor = successor;
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.TranscodeCommandBuilder#buildCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
	 */
	@Override
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		String customProfileCommandHandler = profile.getCustomProfileCommandHandler();
		
		if (!this.getClass().getName().equals(customProfileCommandHandler)) {
			if (this.successor != null) {
				LOG.debug("buildCommand::Cannot handle building the command, delegating to the successor for profile - {}",
					spec.getTargetProfile());
				return this.successor.buildCommand(inputMedia, spec);
			} else {
				LOG.debug("buildCommand::Could not find any successor to handle building the command for profile - {}", 
					spec.getTargetProfile());
				return null;
			}
		}
		
		return this.buildTranscodeCommand(inputMedia, spec);
	}

	public abstract CommandRequest buildTranscodeCommand(Container inputMedia, TranscoderOutputSpec spec);

}
