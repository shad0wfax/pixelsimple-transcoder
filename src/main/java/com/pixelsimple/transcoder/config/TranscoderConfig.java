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

	/**
	 * @return the hlsTranscodeCompleteFile
	 */
	public String getHlsTranscodeCompleteFile() {
		return hlsTranscodeCompleteFile;
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
		Assert.isTrue(file.isFile(), "Looks like the ffprobe path is not valid::" + fullExecutablePath);

		file = null; // gc it hopefully
	}
	
	public String toString() {
		return "TranscoderConfig::\n" + this.hlsPlaylistGeneratorPath  + "\n" + this.hlsTranscodeCompleteFile
			+ "\n" + this.hlsFileSegmentPattern;
	}
}
