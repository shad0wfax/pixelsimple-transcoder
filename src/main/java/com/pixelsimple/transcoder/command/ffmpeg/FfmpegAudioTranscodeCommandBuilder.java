/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.Codec;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 12, 2012
 */
public class FfmpegAudioTranscodeCommandBuilder extends AbstractFfmpegTranscodeCommandBuilder {
	static final Logger LOG = LoggerFactory.getLogger(FfmpegAudioTranscodeCommandBuilder.class);


	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.AbstractTranscodeCommandBuilder#buildCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
	 */
	@Override
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		
		if (profile.getProfileType() != Profile.ProfileType.AUDIO) {
			
			if (this.successor != null) {
				return this.successor.buildCommand(inputMedia, spec);
			} else {
				LOG.debug("Could not find any successor to handle building the command for profile - {}", profile);
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
		
		this.buildCodecsSetting(inputMedia, profile, request);
		this.buildAdditionalParamters(profile, request);		
		request.addArgument(spec.getComputedOutputFileWithPath());
		
		LOG.debug("buildCommand::built command::{}", request.getCommandAsString());
		return request;
	}

	private void buildCodecsSetting(Container inputMedia, Profile profile, CommandRequest request) {
		Codec acodec = this.pickBestMatchAudioCodecForAudioOnlyTranscode(inputMedia, profile);
		
		//TODO: Throw Exception?
		if (acodec != null) {
			if (isValidSetting(acodec.getStrict())) {
				request.addArgument("-strict").addArgument(acodec.getStrict());
			}
			request.addArgument("-acodec").addArgument(acodec.getName());
			
			if (isValidSetting(profile.getAudioBitRate())) {
				request.addArgument("-ab").addArgument(profile.getAudioBitRate());
			}
			
			if (isValidSetting(profile.getAudioSampleRate())) {
				request.addArgument("-ar").addArgument(profile.getAudioSampleRate());
			}
		}
	}

}
