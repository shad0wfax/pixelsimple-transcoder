/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.exception.TranscoderException;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 5, 2012
 */
public class TranscoderOutputSpec {
	private Profile targetProfile;
	private String outputFilePath;
	// Extension automatically gets decided.
	private String outputFileNameWithoutExtension;
	private String computedOutputFile;
	
	public TranscoderOutputSpec(Profile targetProfile, String outputFilePath, String outputFileNameWithoutExtension) {
		this.targetProfile = targetProfile;
		this.outputFilePath = outputFilePath;
		this.outputFileNameWithoutExtension = outputFileNameWithoutExtension;
	}
	
	public void validate() throws TranscoderException {
		if (targetProfile == null)
				throw new TranscoderException("The target profile cannot be null");

		if (outputFilePath == null)
			throw new TranscoderException("The outputFilePath cannot be null");
	
		if (outputFileNameWithoutExtension == null)
			throw new TranscoderException("The outputFileNameWithoutExtension cannot be null");
	}
	
	/**
	 * @return the targetProfile
	 */
	public Profile getTargetProfile() {
		return targetProfile;
	}
	
	/**
	 * @return the outputFileNameWithPath
	 */
	public String getOutputFilePath() {
		return outputFilePath;
	}

	/**
	 * @return the outputFileNameWithoutExtension
	 */
	public String getOutputFileNameWithoutExtension() {
		return outputFileNameWithoutExtension;
	}

	public String getComputedOutputFileWithPath() {
		
		if (StringUtils.isNullOrEmpty(computedOutputFile)) {
			String outputFileNameWithoutExtension = this.getOutputFileNameWithoutExtension();
			outputFileNameWithoutExtension = outputFileNameWithoutExtension + "." + this.targetProfile.getFileExtension();
			this.computedOutputFile = this.getOutputFilePath() + OSUtils.folderSeparator() + outputFileNameWithoutExtension;
		}
		return this.computedOutputFile;
	}


}
