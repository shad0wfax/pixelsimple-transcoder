/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.Codec;
import com.pixelsimple.appcore.media.MediaType;
import com.pixelsimple.appcore.media.StreamType;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.media.Stream;
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
		
		if ((profile.getProfileType() != Profile.ProfileType.VIDEO) || (inputMedia.getMediaType() != MediaType.VIDEO)) {
			
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
		
		this.buildCodecsSetting(inputMedia, profile, request);
		
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
		request.addArgument(spec.getComputedOutputFileWithPath());
		
		LOG.debug("buildCommand::built command::{}", request.getCommandAsString());
		return request;
	}

	private void buildCodecsSetting(Container inputMedia, Profile profile, CommandRequest request) {
		Codec vcodec = this.pickBestMatchVideoCodec(inputMedia, profile);
		if (isValidSetting(vcodec.getStrict())) {
			request.addArgument("-strict").addArgument(vcodec.getStrict());
		}
		request.addArgument("-vcodec").addArgument(vcodec.getName());
		
		if(isValidSetting(profile.getVideoBitRate())) {
			request.addArgument("-b").addArgument(profile.getVideoBitRate());
		}

		if (isValidSetting(profile.getFrameRateFPS())) {
			request.addArgument("-r").addArgument(profile.getFrameRateFPS());
		}
		
		Codec acodec = this.pickBestMatchAudioCodecForVideoCodec(vcodec, inputMedia, profile);
		
		//TODO: Can acodec be null?
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
	
	// Returns a WxH dimension [ex: 1024x720]
	private String computeVideoDimensions(Container inputMedia, Profile profile) {
		// Algo: If there is a maxwidth supplied, first we check the source to see what its width is. 
		// If the source width is smaller than supplied maxwidth, we return null - this means the maxwidth will 
		// be that of the source. 
		// Suppose the source's width is greater than the maxwidth supplied, we will have to scale it down as 
		// follows: If there is an aspect ratio supplied on the profile, we compute the height using that, 
		// else we look at the source and see if we can get the aspect ratio and use it.
		Stream sourceVideoStream = inputMedia.getStreams().get(StreamType.VIDEO);
		String sourceWidthString = sourceVideoStream.getStreamAttribute(Stream.VIDEO_STREAM_ATTRIBUTES.width);
		int sourceWidth = -1;

		if (StringUtils.isNullOrEmpty(sourceWidthString)) {
			// If we can't figure source width, don't constrain to maxwidth? - TODO: Verify what to do - Make it configuration?
			return null;
		}

		// Should be handle NAN?
		sourceWidth = Integer.parseInt(sourceWidthString.trim());
		
		if (profile.getMaxWidth() > 0 && profile.getMaxWidth() < sourceWidth && sourceWidth != -1) {
			String aspectRatioString = isValidSetting(profile.getAspectRatio()) ? profile.getAspectRatio() : null;
			
			// Pick from source
			if (aspectRatioString == null) {
				aspectRatioString = sourceVideoStream.getStreamAttribute(Stream.VIDEO_STREAM_ATTRIBUTES.display_aspect_ratio);
				
				if (StringUtils.isNullOrEmpty(aspectRatioString) || !aspectRatioString.matches("[0-9]+:[0-9]+")) {
					aspectRatioString = null;
					// Do a hard compute - can lead to errors! 
					String sourceHeightString = sourceVideoStream.getStreamAttribute(Stream.VIDEO_STREAM_ATTRIBUTES.height);
					int sourceHeight = -1;

					if (!StringUtils.isNullOrEmpty(sourceHeightString)) {
						// Should be handle NAN?
						sourceHeight = Integer.parseInt(sourceWidthString.trim());
						aspectRatioString = "" + sourceWidth + ":" + sourceHeight;
					}
				}
			}
			
			if (StringUtils.isNullOrEmpty(aspectRatioString)) {
				return null;
			}
			int num = Integer.valueOf(aspectRatioString.substring(aspectRatioString.indexOf(":") + 1, aspectRatioString.length()));
			int den = Integer.valueOf(aspectRatioString.substring(0, aspectRatioString.indexOf(":")));
			int maxHeight = (profile.getMaxWidth() * num ) / den;  
			LOG.debug("computeVideoDimensions::computed maxHeight (note: even/odd)::{}",  maxHeight);
			
			// Even out the maxheight as ffmpeg will fail otherwise (odd width and height transcode fail). Reduce!
			maxHeight = (maxHeight % 2) == 0 ? maxHeight : (maxHeight -1); 
			
			String dimension =  profile.getMaxWidth() + "x" + maxHeight;

			LOG.debug("computeVideoDimensions::identified a new dimension to use::{}",  dimension);
			return dimension;
		}
		return null;
	}
	
}
