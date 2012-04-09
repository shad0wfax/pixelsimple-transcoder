/**
 * � PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.AudioCodec;
import com.pixelsimple.appcore.media.MediaType;
import com.pixelsimple.appcore.media.VideoCodec;
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
			
			if (this.successor != null) {
				LOG.debug("buildCommand::Cannot handle building the command, delegating to the successor for profile - {}",
					profile);
				return this.successor.buildCommand(inputMedia, spec);
			} else {
				LOG.debug("buildCommand::Could not find any successor to handle building the command for profile - {}", 
					profile);
				return null;
			}
		}
			
		CommandRequest request = new CommandRequest().addCommand(getFfmpegPath(), 0);
		
		// Keep it simple - gunning for this :-)
		// ffmpeg -y -i input_file [output_file_options: like bitrate,codecs,format etc] output_file
		String videoInputPath = inputMedia.getFilePathWithName(); 
		request.addArgument("-y").addArgument("-i").addArgument(videoInputPath);

		// Note: Having problems with ffmpeg sometimes when -f is container format name. Ex: wmv. Let it auto-detect/use fileFormat
//		command.append(" -f " + profile.getContainerFormat());
		if (isValidSetting(profile.getFileFormat())) {
			request.addArgument("-f").addArgument(profile.getFileFormat());
		}
		
		VideoCodec vcodec = this.buildVideoCodecsSetting(inputMedia, profile, request);
		AudioCodec acodec = this.buildAudioCodecSetting(inputMedia, profile, request, vcodec);
		
		String dimension = this.computeVideoDimensions(inputMedia, profile);
		
		if (!StringUtils.isNullOrEmpty(dimension)) {
			request.addArgument("-s").addArgument(dimension);
		} else {
			// If dimension has been set, ignore aspect ratio - else, set the aspect ratio
			if (isValidSetting(profile.getAspectRatio())) {
				request.addArgument("-aspect").addArgument(profile.getAspectRatio());					
			}
		}

		this.buildAdditionalParamters(profile, request);
		this.addChannelRestriction(inputMedia, acodec, request);

		request.addArgument(spec.getComputedOutputFileWithPath());
		
		LOG.debug("buildCommand::built command::{}", request.getCommandAsString());
		return request;
	}

}
