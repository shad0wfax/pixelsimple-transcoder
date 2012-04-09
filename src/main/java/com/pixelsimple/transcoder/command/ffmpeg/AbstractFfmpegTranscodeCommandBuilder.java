/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.appcore.media.AudioCodec;
import com.pixelsimple.appcore.media.StreamType;
import com.pixelsimple.appcore.media.VideoCodec;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.media.Stream;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilder;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 11, 2012
 */
public abstract class AbstractFfmpegTranscodeCommandBuilder implements TranscodeCommandBuilder {
	static final Logger LOG = LoggerFactory.getLogger(AbstractFfmpegTranscodeCommandBuilder.class);

	protected TranscodeCommandBuilder successor;

	// TODO: As usual, see if this can be injected someday.
	protected ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();


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
	public abstract CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec);
	
	protected boolean isValidSetting(String attribute) {
		boolean invalid = StringUtils.isNullOrEmpty(attribute) 
				|| attribute.trim().equalsIgnoreCase(Profile.SAME_AS_SOURCE_SETTING); 
		
		return !invalid;
	}
	
	protected String getFfmpegPath() {
		return apiConfig.getFfmpegConfig().getExecutablePath(); 
	}

	protected VideoCodec pickBestMatchVideoCodec(Container inputMedia, Profile profile) {
		// Algo: Profile has the list of video codecs that are to be used and is in preferred order (list).
		// We will check to see if the source video codec matches any of the video codec, if so, we can just reuse (copy).
		// If there is no match, then we pick the first video codec in the list as it was the preferred one.
		VideoCodec videoCodec = profile.getVideoCodecs().get(0);
		
		Stream sourceVideoStream = inputMedia.getStreams().get(StreamType.VIDEO);
		String sourceCodecName = sourceVideoStream.getStreamAttribute(Stream.VIDEO_STREAM_ATTRIBUTES.codec_name);
		
		if (!StringUtils.isNullOrEmpty(sourceCodecName)) {
			List<VideoCodec> codecs =  profile.getVideoCodecs();
			VideoCodec sourceCodec = new VideoCodec(sourceCodecName);
			
			VideoCodec matchedCodec = this.findMatch(codecs, sourceCodec);
			videoCodec = matchedCodec != null ? matchedCodec : videoCodec;
		}
		LOG.debug("pickBestMatchVideoCodec::picked the codec::{}", videoCodec);
		
		return videoCodec;
	}
	
	//TODO: defensive ! - what if no audio codecs for a video codec? Right now it will be NPE/AIOBE all the way :-)
	protected AudioCodec pickBestMatchAudioCodecForVideoCodec(VideoCodec videoCodec, Container inputMedia, Profile profile) {
		// Algo: Profile has the list of audio codecs associated for a video codec, again as a list (in order of preference).
		// We will check to see if there is a match already with the source and if so use it (copy). If not, pick the 
		// first for the video codec as that was the preffered order. 
		AudioCodec audioCodec = profile.getAssociatedAudioCodecs(videoCodec).get(0);		
		
		List<AudioCodec> codecs =  profile.getAssociatedAudioCodecs(videoCodec);
		AudioCodec matchedCodec = matchAudioCodec(inputMedia, codecs);
		audioCodec = matchedCodec != null ? matchedCodec : audioCodec;

		LOG.debug("pickBestMatchAudioCodecForVideoCodec::picked the codec::{}", audioCodec);
		return audioCodec;
		
	}

	protected AudioCodec pickBestMatchAudioCodecForAudioOnlyTranscode(Container inputMedia, Profile profile) {
		// Algo: Check if there is a match with the list of audio codecs listed for the profile, else pick first in list.
		AudioCodec audioCodec = profile.getAudioCodecs().get(0);
		
		List<AudioCodec> codecs =  profile.getAudioCodecs();
		AudioCodec matchedCodec = matchAudioCodec(inputMedia, codecs);
		audioCodec = matchedCodec != null ? matchedCodec : audioCodec;
		
		LOG.debug("pickBestMatchAudioCodecForAudioOnlyTranscode::picked the codec::{}", audioCodec);
		return audioCodec;
	}
	

	protected AudioCodec matchAudioCodec(Container inputMedia, List<AudioCodec> codecs) {
		AudioCodec audioCodec = null;
		Stream sourceAudioStream = inputMedia.getStreams().get(StreamType.AUDIO);
		String sourceCodecName = sourceAudioStream.getStreamAttribute(Stream.AUDIO_STREAM_ATTRIBUTES.codec_name);
		
		if (!StringUtils.isNullOrEmpty(sourceCodecName)) {
			AudioCodec sourceCodec = new AudioCodec(sourceCodecName);
			
			audioCodec = findMatch(codecs, sourceCodec);
		}
		return audioCodec;
	}
	
	protected VideoCodec findMatch(List<VideoCodec> codecs, VideoCodec sourceCodec) {
		VideoCodec matchedCodec = null;
		for (int i = 0, size = codecs.size(); i < size; i++) {
			VideoCodec preferredVideoCodec = codecs.get(i);
			
			if (preferredVideoCodec.equals(sourceCodec)) {
				matchedCodec = preferredVideoCodec;
				break;
			}
		}
		return matchedCodec;
	}

	protected AudioCodec findMatch(List<AudioCodec> codecs, AudioCodec sourceCodec) {
		AudioCodec matchedCodec = null;
		for (int i = 0, size = codecs.size(); i < size; i++) {
			AudioCodec preferredVideoCodec = codecs.get(i);
			
			if (preferredVideoCodec.equals(sourceCodec)) {
				matchedCodec = preferredVideoCodec;
				break;
			}
		}
		return matchedCodec;
	}

	protected void buildAdditionalParamters(Profile profile, CommandRequest request) {
		
		// Additional parameters might have space, so get it as string array to set
		// This is to ensure that if, a parameter is to be overriden it can be done individually here. 
		if (isValidSetting(profile.getAdditionalParameters())) {
			String additionalParam = profile.getAdditionalParameters().trim();
			String [] parameters = additionalParam.split("\\s");
			
			for (String param : parameters) {
				request.addArgument(param);	
			}
		}
	}
	
	protected void addChannelRestriction(Container inputMedia, AudioCodec acodec, CommandRequest request) {
		int limit = acodec.getMaxChannels();
		
		// first check if there are any codec based channel restriction:
		if (limit == AudioCodec.NO_SPECIFIED_CHANNEL_LIMIT)
			return;
		
		// Check if "-ac" (audio channel count param of ffmpeg) is already added (could be form additional param override)
		if (request.doesArgumentExist("-ac", false))
			return;
		
		Stream audio = inputMedia.getStreams().get(StreamType.AUDIO);
		
		if (audio == null)
			return;
		
		String channelStr = audio.getStreamAttribute(Stream.AUDIO_STREAM_ATTRIBUTES.channels);
		
		if (StringUtils.isNullOrEmpty(channelStr))
			return;
		
		try {
			float channels = Float.parseFloat(channelStr);
			
			if (limit < channels) {
				request.addArgument("-ac").addArgument("" + limit);
			}
			LOG.debug("addChannelRestriction:: limiting the number of channels to {}", limit);
		} catch (Exception e) {
			// NAN - ignore move on.
		}
	}

	protected VideoCodec buildVideoCodecsSetting(Container inputMedia, Profile profile, CommandRequest request) {
		VideoCodec vcodec = this.pickBestMatchVideoCodec(inputMedia, profile);
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
		return vcodec;
	}

	protected AudioCodec buildAudioCodecSetting(Container inputMedia, Profile profile, CommandRequest request, 
			VideoCodec vcodec) {
		AudioCodec acodec = this.pickBestMatchAudioCodecForVideoCodec(vcodec, inputMedia, profile);
		
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
		return acodec;
	}
	
	// Returns a WxH dimension [ex: 1024x720]
	protected String computeVideoDimensions(Container inputMedia, Profile profile) {
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
