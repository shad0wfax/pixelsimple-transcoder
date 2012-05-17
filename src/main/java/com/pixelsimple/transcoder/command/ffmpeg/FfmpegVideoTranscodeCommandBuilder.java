/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.MediaType;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 12, 2012
 */
public class FfmpegVideoTranscodeCommandBuilder extends AbstractFfmpegTranscodeCommandBuilder {
	static final Logger LOG = LoggerFactory.getLogger(FfmpegVideoTranscodeCommandBuilder.class);

	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.AbstractTranscodeCommandBuilder#buildCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
	 */
	@Override
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		String customProfileCommandHandler = profile.getCustomProfileCommandHandler();
		
		if ((!StringUtils.isNullOrEmpty(customProfileCommandHandler) && !this.getClass().getName().equals(customProfileCommandHandler))
				|| (profile.getProfileType() != Profile.ProfileType.VIDEO) || (inputMedia.getMediaType() != MediaType.VIDEO)) {
			return this.delegateCommandBuilding(inputMedia, spec);
		}
			
		CommandRequest request = new CommandRequest().addCommand(getFfmpegPath(), 0);
		
		this.buildVideoTranscodeCommand(inputMedia, profile, request, false);

		request.addArgument(spec.getComputedOutputFileWithPath());
		
		LOG.debug("buildCommand::built command::{}", request.getCommandAsString());
		return request;
	}

}
