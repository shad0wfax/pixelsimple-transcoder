/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.appcore.media.Codec;

/**
 *
 * @author Akshay Sharma
 * Feb 7, 2012
 */
public class ProfileTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void verifyProfile() {
		Profile profile = createValidVideoProfile();
		Assert.assertEquals(profile.getAspectRatio(), "16:9");
		Assert.assertEquals(profile.getVideoQuality(), "SAME_AS_SOURCE");
		Assert.assertEquals(profile.getAudioCodecs().size(), 1);
		Assert.assertEquals(profile.getVideoCodecs().size(), 1);
		Assert.assertEquals(profile.getAdditionalParameters(), " -moov_size 10 ");
	}
	
	@Test
	public void invalidProfileOperations() {
		Profile profile = createValidVideoProfile();
		
		try {
			profile.addAudioOnlyCodec(new Codec(Codec.CODEC_TYPE.VIDEO, "aac"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

		try {
			profile.addAssociatedAudioCodec(new Codec(Codec.CODEC_TYPE.VIDEO, "doesntexist"), new Codec(Codec.CODEC_TYPE.AUDIO, "flv"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

		try {
			profile.getAssociatedAudioCodecs(new Codec(Codec.CODEC_TYPE.AUDIO, "aac"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

		try {
			Profile newProfile = new Profile(Profile.ProfileType.AUDIO);
			newProfile.addVideoCodec(new Codec(Codec.CODEC_TYPE.AUDIO, "flv"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

	}
	
	private Profile createValidVideoProfile() {
		Profile profile = new Profile(Profile.ProfileType.VIDEO);

		profile.setId("test_profile");
	    profile.setName("testing profile");
	    profile.setContainerFormat("mkv");
	    profile.setFileExtension("mkv");
	    profile.setVideoBitRate("SAME_AS_SOURCE");
	    profile.setVideoQuality("SAME_AS_SOURCE");
	    profile.setAudioBitRate("");
	    profile.setAspectRatio("16:9");
    	profile.setMaxWidth(720);	
	    profile.setFrameRateFPS("SAME_AS_SOURCE");
	    profile.setAdditionalParameters(" -moov_size 10 ");
   		profile.addCriteria("testing");
	    
   		Codec vCodec = new Codec(Codec.CODEC_TYPE.VIDEO, "libx264");
   		Codec aCodec = new Codec(Codec.CODEC_TYPE.AUDIO, "libmp3lame");
   		profile.addVideoCodec(vCodec);
   		profile.addAssociatedAudioCodec(vCodec, aCodec);

	    return profile;
		
	}

}
