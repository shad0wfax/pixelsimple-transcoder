/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.appcore.media.Codec;
import com.pixelsimple.appcore.media.StreamType;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.media.Stream;
import com.pixelsimple.commons.util.OSUtils;
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
		ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
		return apiConfig.getFfmpegConfig().getExecutablePath(); 
	}

	protected String getOutputFileWithPath(TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		String outputFileNameWithoutExtension = spec.getOutputFileNameWithoutExtension();
		outputFileNameWithoutExtension = outputFileNameWithoutExtension + "." + profile.getFileExtension();
		String outputPath = spec.getOutputFilePath() + OSUtils.folderSeparator() + outputFileNameWithoutExtension;
		return outputPath;
	}

	protected Codec pickBestMatchVideoCodec(Container inputMedia, Profile profile) {
		// Algo: Profile has the list of video codecs that are to be used and is in preferred order (list).
		// We will check to see if the source video codec matches any of the video codec, if so, we can just reuse (copy).
		// If there is no match, then we pick the first video codec in the list as it was the preferred one.
		Codec videoCodec = profile.getVideoCodecs().get(0);
		
		Stream sourceVideoStream = inputMedia.getStreams().get(StreamType.VIDEO);
		String sourceCodecName = sourceVideoStream.getStreamAttribute(Stream.VIDEO_STREAM_ATTRIBUTES.codec_name);
		
		if (!StringUtils.isNullOrEmpty(sourceCodecName)) {
			List<Codec> codecs =  profile.getVideoCodecs();
			Codec sourceCodec = new Codec(Codec.CODEC_TYPE.VIDEO, sourceCodecName);
			
			Codec matchedCodec = findMatch(codecs, sourceCodec);
			videoCodec = matchedCodec != null ? matchedCodec : videoCodec;
		}
		LOG.debug("pickBestMatchVideoCodec::picked the codec::{}", videoCodec);
		
		return videoCodec;
	}
	
	//TODO: defensive ! - what if no audio codecs for a video codec? Right now it will be NPE/AIOBE all the way :-)
	protected Codec pickBestMatchAudioCodecForVideoCodec(Codec videoCodec, Container inputMedia, Profile profile) {
		// Algo: Profile has the list of audio codecs associated for a video codec, again as a list (in order of preference).
		// We will check to see if there is a match already with the source and if so use it (copy). If not, pick the 
		// first for the video codec as that was the preffered order. 
		Codec audioCodec = profile.getAssociatedAudioCodecs(videoCodec).get(0);		
		
		List<Codec> codecs =  profile.getAssociatedAudioCodecs(videoCodec);
		Codec matchedCodec = matchAudioCodec(inputMedia, codecs);
		audioCodec = matchedCodec != null ? matchedCodec : audioCodec;

		LOG.debug("pickBestMatchAudioCodecForVideoCodec::picked the codec::{}", audioCodec);
		return audioCodec;
		
	}

	protected Codec pickBestMatchAudioCodecForAudioOnlyTranscode(Container inputMedia, Profile profile) {
		// Algo: Check if there is a match with the list of audio codecs listed for the profile, else pick first in list.
		Codec audioCodec = profile.getAudioCodecs().get(0);
		
		List<Codec> codecs =  profile.getAudioCodecs();
		Codec matchedCodec = matchAudioCodec(inputMedia, codecs);
		audioCodec = matchedCodec != null ? matchedCodec : audioCodec;
		
		LOG.debug("pickBestMatchAudioCodecForAudioOnlyTranscode::picked the codec::{}", audioCodec);
		return audioCodec;
	}
	

	protected Codec matchAudioCodec(Container inputMedia, List<Codec> codecs) {
		Codec audioCodec = null;
		Stream sourceAudioStream = inputMedia.getStreams().get(StreamType.AUDIO);
		String sourceCodecName = sourceAudioStream.getStreamAttribute(Stream.AUDIO_STREAM_ATTRIBUTES.codec_name);
		
		if (!StringUtils.isNullOrEmpty(sourceCodecName)) {
			
			Codec sourceCodec = new Codec(Codec.CODEC_TYPE.AUDIO, sourceCodecName);
			
			audioCodec = findMatch(codecs, sourceCodec);
		}
		return audioCodec;
	}
	
	protected Codec findMatch(List<Codec> codecs, Codec sourceCodec) {
		Codec matchedCodec = null;
		for (int i = 0, size = codecs.size(); i < size; i++) {
			Codec preferredVideoCodec = codecs.get(i);
			
			if (preferredVideoCodec.equals(sourceCodec)) {
				matchedCodec = preferredVideoCodec;
				break;
			}
		}
		return matchedCodec;
	}

	protected void buildAdditionalParamters(Profile profile, CommandRequest request) {
		
		if (isValidSetting(profile.getAdditionalParameters())) {
			// Additional parameters might have space, so get it as string array to set
			String additionalParam = profile.getAdditionalParameters().trim();
			String [] parameters = additionalParam.split("\\s");
			
			for (String param : parameters) {
				request.addArguments(param);	
			}
		}
	}
	
}
