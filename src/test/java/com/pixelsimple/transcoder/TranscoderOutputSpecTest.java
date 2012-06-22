/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.pixelsimple.appcore.Resource;
import com.pixelsimple.appcore.Resource.RESOURCE_TYPE;
import com.pixelsimple.transcoder.profile.Profile;
import com.pixelsimple.transcoder.profile.Profile.ProfileType;

/**
 * @author Akshay Sharma
 *
 * Jun 16, 2012
 */
public class TranscoderOutputSpecTest {

	@Test
	public void invalidSpec() {
		try {
			Resource res = new Resource("somedir/file", RESOURCE_TYPE.FILE); 
			new TranscoderOutputSpec(new Profile(ProfileType.AUDIO), res, "sample"); 
			fail();
		} catch (IllegalArgumentException e) {}
		
	}

	@Test
	public void validSpec() {
		try {
			Resource res = new Resource("somedir/file", RESOURCE_TYPE.DIRECTORY); 
			new TranscoderOutputSpec(new Profile(ProfileType.AUDIO), res, "sample"); 
		} catch (IllegalArgumentException e) {
			fail();
		}
		
	}
}
