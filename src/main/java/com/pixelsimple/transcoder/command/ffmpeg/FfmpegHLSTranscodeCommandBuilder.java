/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.media.AudioCodec;
import com.pixelsimple.appcore.media.VideoCodec;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
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

		// TODO: Hardcoded for now, change it based on what is being transcoded.
		request.addArgument("-map").addArgument("0");
		
		if (isValidSetting(profile.getFileFormat())) {
			// This will be a temp file, the actual playlist file is created by the scripts.
			String file = spec.getHlsTranscoderOutputSpec().getComputedPlaylistFileWithPath();
			int xtnInd = file.lastIndexOf(".");
			String playlistFileNameForFfmpeg = file.substring(0, xtnInd) + "_temp" + file.substring(xtnInd);
			String hlsFilePattern =  apiConfig.getHlsFileSegmentPattern();
			
			request.addArgument("-f").addArgument(profile.getFileFormat())
				.addArgument("-segment_time").addArgument("" + spec.getHlsTranscoderOutputSpec().getSegmentTime())
				.addArgument("-segment_list").addArgument(playlistFileNameForFfmpeg)
				.addArgument("-segment_format").addArgument(spec.getHlsTranscoderOutputSpec().getSegmentFormat())
				.addArgument(spec.getOutputFileDir() + OSUtils.folderSeparator() 
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
