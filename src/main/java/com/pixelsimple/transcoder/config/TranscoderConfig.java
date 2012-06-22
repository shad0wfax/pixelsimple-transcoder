/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.config;

import java.io.File;

import com.pixelsimple.commons.util.Assert;

/**
 *
 * @author Akshay Sharma
 * May 15, 2012
 */
public class TranscoderConfig {
	private String hlsTranscodeCompleteFile;
	// Going further all paths will be as string instead of their own objects (like ffmpeg/ffprobe)
	private String hlsPlaylistGeneratorPath;
	private String hlsFileSegmentPattern;
	private String ninjaInputFilePattern;
	private String ninjaOutputFilePattern;
	private String ninjaVideoBitratePattern;
	private String ninjaAudioBitratePattern;

	/**
	 * @return the hlsTranscodeCompleteFile
	 */
	public String getHlsTranscodeCompleteFile() {
		return this.hlsTranscodeCompleteFile;
	}
	
	/**
	 * @param hlsTranscodeCompleteFile the hlsTranscodeCompleteFile to set
	 */
	public TranscoderConfig setHlsTranscodeCompleteFile(String hlsTranscodeCompleteFile) {
		this.hlsTranscodeCompleteFile = hlsTranscodeCompleteFile;
		return this;
	}
	public String getHlsPlaylistGeneratorPath() {
		return this.hlsPlaylistGeneratorPath;
	}

	public TranscoderConfig setHlsPlaylistGeneratorPath(String hlsPlaylistGeneratorPath) {
		this.validateFile(hlsPlaylistGeneratorPath);
		this.hlsPlaylistGeneratorPath = hlsPlaylistGeneratorPath;
		return this;
	}
	
	public String getHlsFileSegmentPattern() {
		return hlsFileSegmentPattern;
	}

	public TranscoderConfig setHlsFileSegmentPattern(String hlsFileSegmentPattern) {
		this.hlsFileSegmentPattern = hlsFileSegmentPattern;
		return this;
	}

	private void validateFile(String fullExecutablePath) {
		File file = new File(fullExecutablePath);
		Assert.isTrue(file.isFile(), "Looks like the the file provided in path is not valid::" + fullExecutablePath);

		file = null; // gc it hopefully
	}
	
	/**
	 * @return the ninjaInputFilePattern
	 */
	public String getNinjaInputFilePattern() {
		return ninjaInputFilePattern;
	}

	/**
	 * @param ninjaInputFilePattern the ninjaInputFilePattern to set
	 */
	public void setNinjaInputFilePattern(String ninjaInputFilePattern) {
		this.ninjaInputFilePattern = ninjaInputFilePattern;
	}

	/**
	 * @return the ninjaOutputFilePattern
	 */
	public String getNinjaOutputFilePattern() {
		return ninjaOutputFilePattern;
	}

	/**
	 * @param ninjaOutputFilePattern the ninjaOutputFilePattern to set
	 */
	public void setNinjaOutputFilePattern(String ninjaOutputFilePattern) {
		this.ninjaOutputFilePattern = ninjaOutputFilePattern;
	}

	/**
	 * @return the ninjaVideoBitratePattern
	 */
	public String getNinjaVideoBitratePattern() {
		return ninjaVideoBitratePattern;
	}

	/**
	 * @param ninjaVideoBitratePattern the ninjaVideoBitratePattern to set
	 */
	public void setNinjaVideoBitratePattern(String ninjaVideoBitratePattern) {
		this.ninjaVideoBitratePattern = ninjaVideoBitratePattern;
	}

	/**
	 * @return the ninjaAudioBitratePattern
	 */
	public String getNinjaAudioBitratePattern() {
		return ninjaAudioBitratePattern;
	}

	/**
	 * @param ninjaAudioBitratePattern the ninjaAudioBitratePattern to set
	 */
	public void setNinjaAudioBitratePattern(String ninjaAudioBitratePattern) {
		this.ninjaAudioBitratePattern = ninjaAudioBitratePattern;
	}

	public String toString() {
		return "TranscoderConfig::\n" + this.hlsPlaylistGeneratorPath  + "\n" + this.hlsTranscodeCompleteFile
			+ "\n" + this.hlsFileSegmentPattern + "\n" + this.ninjaAudioBitratePattern + "\t" + this.ninjaVideoBitratePattern
			+ "\t" + this.ninjaInputFilePattern + "\t" + this.ninjaOutputFilePattern;
	}

}
