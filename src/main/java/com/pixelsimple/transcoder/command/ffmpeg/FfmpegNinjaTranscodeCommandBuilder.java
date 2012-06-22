/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderApi;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * May 18, 2012
 */
public class FfmpegNinjaTranscodeCommandBuilder extends TranscodeCommandBuilderApi {
	static final Logger LOG = LoggerFactory.getLogger(FfmpegNinjaTranscodeCommandBuilder.class);

	/* (non-Javadoc)
	 * @see com.pixelsimple.transcoder.command.TranscodeCommandBuilderApi#buildTranscodeCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
	 */
	@Override
	public CommandRequest buildTranscodeCommand(Container inputMedia, TranscoderOutputSpec spec) {
		Profile profile = spec.getTargetProfile();
		String ninjaCommand = profile.getNinjaCommand();
		
		LOG.debug("buildTranscodeCommand::ninjaCommand before replacement = {}", ninjaCommand);
		
		if (StringUtils.isNullOrEmpty(ninjaCommand))
			return null;
		
		// Support replacing the following if present: (update as needed)
		// $ffmpeg -> Will replace with complete path of ffmpeg. Custom handlers can replace any command they want (alternative to ffmpeg?).
		// $if -> Will replace with the input file. (optional)
		// $of - will replace with output file. (optional)
		// $vb - will replace with video bit rate (optional)
		// $ab - will replace with audio bit rate (optional)
		CommandRequest req = new CommandRequest();
		String ffmpegPath = apiConfig.getFfmpegConfig().getExecutablePath();
		String inputFile = quoteIfContainsSpace(inputMedia.getMediaResource().getResourceAsString());
		String outputFile = null;
		if (profile.isHlsProfile()) {
			outputFile = spec.getHlsTranscoderOutputSpec().getComputedPlaylistFileWithPath();
		} else {
			outputFile = quoteIfContainsSpace(spec.getComputedOutputFileWithPath());
		}
		
		 
		// TODO: Handle SAME_AS_SOURCE correctly - ignore?
		String videoBitRate = isValidSetting(profile.getVideoBitRate()) ? profile.getVideoBitRate() : "";
		String audioBitRate = isValidSetting(profile.getAudioBitRate()) ? profile.getAudioBitRate() : "";
		
		ninjaCommand = ninjaCommand
			.replaceAll("\\$ffmpeg", Matcher.quoteReplacement(ffmpegPath))	
			.replaceAll(this.transcoderConfig.getNinjaInputFilePattern(), Matcher.quoteReplacement(inputFile))
			.replaceAll(this.transcoderConfig.getNinjaOutputFilePattern(), Matcher.quoteReplacement(outputFile))
			.replaceAll(this.transcoderConfig.getNinjaVideoBitratePattern(), Matcher.quoteReplacement(videoBitRate))
			.replaceAll(this.transcoderConfig.getNinjaAudioBitratePattern(), Matcher.quoteReplacement(audioBitRate));
		
		LOG.debug("buildTranscodeCommand::ninjaCommand after replacement = {}", ninjaCommand);

		req.addCommand(ninjaCommand, 0);
		
		return req;
	}

	/**
	 * @param filePathWithName
	 * @return
	 */
	private String quoteIfContainsSpace(String filePathWithName) {
		if (filePathWithName.indexOf(" ") == -1)
			return filePathWithName;
		
		// quote the argument if there is a space in it
		return "\"" + filePathWithName + "\"";
	}

	private boolean isValidSetting(String attribute) {
		boolean invalid = StringUtils.isNullOrEmpty(attribute) 
				|| attribute.trim().equalsIgnoreCase(Profile.SAME_AS_SOURCE_SETTING); 
		
		return !invalid;
	}

}
