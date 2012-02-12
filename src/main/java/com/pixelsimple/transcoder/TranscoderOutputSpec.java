/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

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
	
	public TranscoderOutputSpec(Profile targetProfile, String outputFilePath, String outputFileNameWithoutExtension) {
		this.targetProfile = targetProfile;
		this.outputFilePath = outputFilePath;
		this.outputFileNameWithoutExtension = outputFileNameWithoutExtension;
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

}
