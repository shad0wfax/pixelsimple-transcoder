/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.appcore.media.AudioCodec;
import com.pixelsimple.appcore.media.VideoCodec;
import com.pixelsimple.transcoder.profile.Profile.ProfileType;

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
	public void validProfileOperations() {
		Profile profile = createValidVideoProfile();
		
		profile.addVideoCodec(new VideoCodec("msmpeg"));
		profile.addAssociatedAudioCodec(new VideoCodec("msmpeg"), new AudioCodec("vorbis"));
		
		Assert.assertEquals(profile.getAudioCodecs().size(), 2);
		Assert.assertEquals(profile.getVideoCodecs().size(), 2);
	}
	
	@Test
	public void invalidProfileOperations() {
		
		try {
			Profile profile = createValidVideoProfile();
			profile.addAssociatedAudioCodec(new VideoCodec("doesntexist"), new AudioCodec("flv"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}
		
		try {
			Profile profile = new Profile(ProfileType.AUDIO);
			profile.addAssociatedAudioCodec(new VideoCodec("wmv2"), new AudioCodec("flv"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

		try {
			Profile profile = new Profile(ProfileType.AUDIO);
			profile.addVideoCodec(new VideoCodec("libx264"));
			Assert.fail();
		} catch (IllegalStateException e) {
			// Ok
		}

		try {
			Profile profile = new Profile(ProfileType.VIDEO);
			profile.addAudioOnlyCodec(new AudioCodec("libmp3lame"));
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
	    
   		VideoCodec vCodec =  new VideoCodec("libx264"); // Codec.create(Codec.CODEC_TYPE.VIDEO , "libx264");
   		AudioCodec aCodec = new AudioCodec("libmp3lame"); // Codec.create(Codec.CODEC_TYPE.AUDIO , "libmp3lame");
   		profile.addVideoCodec(vCodec);
   		profile.addAssociatedAudioCodec(vCodec, aCodec);

	    return profile;
		
	}

}
