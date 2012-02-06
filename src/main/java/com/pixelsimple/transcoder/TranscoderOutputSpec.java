/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import com.pixelsimple.appcore.media.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 5, 2012
 */
public class TranscoderOutputSpec {
	private Profile targetProfile;
	private String outputFileNameWithPath;
	
	public TranscoderOutputSpec(Profile targetProfile, String outputFileNameWithPath) {
		this.targetProfile = targetProfile;
		this.outputFileNameWithPath = outputFileNameWithPath;
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
	public String getOutputFileNameWithPath() {
		return outputFileNameWithPath;
	}

}
