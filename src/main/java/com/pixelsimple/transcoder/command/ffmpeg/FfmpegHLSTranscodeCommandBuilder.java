/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.MediaType;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.config.TranscoderConfig;
import com.pixelsimple.transcoder.config.TranscoderRegistryKeys;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Mar 22, 2012
 */
public class FfmpegHLSTranscodeCommandBuilder extends AbstractFfmpegTranscodeCommandBuilder {
	static final Logger LOG = LoggerFactory.getLogger(FfmpegHLSTranscodeCommandBuilder.class);

	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.ffmpeg.AbstractFfmpegTranscodeCommandBuilder#buildCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
	 */
	@Override
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		String customProfileCommandHandler = profile.getCustomProfileCommandHandler();

		if (!StringUtils.isNullOrEmpty(customProfileCommandHandler) && !this.getClass().getName().equals(customProfileCommandHandler)) {
			return this.delegateCommandBuilding(inputMedia, spec);
		}
		
		CommandRequest request = new CommandRequest().addCommand(getFfmpegPath(), 0);
		
		if (inputMedia.getMediaType() == MediaType.VIDEO) {
			this.buildVideoTranscodeCommand(inputMedia, profile, request, true);
		} else if (inputMedia.getMediaType() == MediaType.AUDIO) {
			this.buildAudioTranscodeCommand(inputMedia, profile, request);
		}

		// TODO: Hardcoded for now, change it based on what is being transcoded. -map 0 means copy all streams.
		request.addArgument("-map").addArgument("0");
		
		if (isValidSetting(profile.getFileFormat())) {
			TranscoderConfig transcoderConfig = apiConfig.getGenericRegistryEntry().getEntry(
					TranscoderRegistryKeys.TRANSCODER_CONFIG);
			
			// This will be a temp file, the actual playlist file is created by the scripts.
			String file = spec.getHlsTranscoderOutputSpec().getComputedPlaylistFileWithPath();
			int xtnInd = file.lastIndexOf(".");
			String playlistFileNameForFfmpeg = file.substring(0, xtnInd) + "_temp" + file.substring(xtnInd);
			String hlsFilePattern =  transcoderConfig.getHlsFileSegmentPattern();
			
			request.addArgument("-f").addArgument(profile.getFileFormat())
				.addArgument("-segment_time").addArgument("" + spec.getHlsTranscoderOutputSpec().getSegmentTime())
				.addArgument("-segment_list").addArgument(playlistFileNameForFfmpeg)
				.addArgument("-segment_format").addArgument(spec.getHlsTranscoderOutputSpec().getSegmentFormat())
				.addArgument(OSUtils.appendFolderSeparator(spec.getOutputFileDir().getResourceAsString()) 
					+ spec.getHlsTranscoderOutputSpec().getSegmentFileWithoutExtension() + hlsFilePattern 
					+ "." + profile.getContainerFormat());
			LOG.debug("buildCommand::built command::{}", request.getCommandAsString());
		} else {
			LOG.debug("buildCommand::Missing file format, so returning null request.");
			request = null;
		}
		
		return request;
	}

}
