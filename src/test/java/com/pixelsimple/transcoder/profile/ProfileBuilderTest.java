/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.transcoder.util.ProfileUtilTest;

/**
 *
 * @author Akshay Sharma
 * Feb 5, 2012
 */
public class ProfileBuilderTest {
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestAppInitializer.bootStrapRegistryForTesting();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#parseDefaultMediaProfiles(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildDefaultProfileData() throws Exception {
		Map<String, Profile> profiles = ProfileBuilder.parseDefaultMediaProfiles();
		Assert.assertNotNull(profiles);
	}
	
	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithValidData() throws Exception {
		Node node = ProfileUtilTest.validVideoXmlNode();
		Profile profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getId(), "Opera_10.5_high_bandwidth");
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertNotNull(profile.getVideoCodecs().get(0).getName());
		Assert.assertEquals(profile.getFileFormat(), "avi");
		Assert.assertEquals(profile.getAudioSampleRate(), "8000");
		Assert.assertEquals(profile.getCustomProfileCommandHandler(), "java.lang.String");
		Assert.assertEquals(profile.isHlsProfile(), true);
		
		node = ProfileUtilTest.validAudioXmlNode();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertEquals(profile.getVideoCodecs().size(), 0);
		Assert.assertEquals(profile.isHlsProfile(), false);
		
		node = ProfileUtilTest.validVideoXmlNodeWithCData();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getId(), "Opera_10.5_high_bandwidth");
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertNotNull(profile.getVideoCodecs().get(0).getName());
		Assert.assertTrue(profile.getName().startsWith("This is a multiline \"name\""));
		Assert.assertTrue(profile.getName().contains("<name> - !!"));
		
		node = ProfileUtilTest.validVideoXmlNodeWithMultipleCodecs();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getId(), "ipod_standalone");
		Assert.assertEquals(profile.getAudioCodecs().size(), 5);
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "aac");
		Assert.assertEquals(profile.getAudioCodecs().get(4).getName(), "libmp3lame");
		Assert.assertEquals(profile.getVideoCodecs().size(), 2);
		Assert.assertEquals(profile.getVideoCodecs().get(0).getName(), "libx264");
		Assert.assertEquals(profile.getVideoCodecs().get(1).getName(), "mpeg4");
		Assert.assertEquals(profile.getAssociatedAudioCodecs(profile.getVideoCodecs().get(0)).size(), 2);
		Assert.assertEquals(profile.getAssociatedAudioCodecs(profile.getVideoCodecs().get(1)).size(), 3);
		
		node = ProfileUtilTest.validVideoXmlNodeWithNinjaCommand();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getNinjaCommand(), "$ffmpeg -i $if -c:a copy -b:a $ab -c:v copy -b:v $vb $of");
		Assert.assertEquals(profile.getCustomProfileCommandHandler(), "com.pixelsimple.transcoder.command.ffmpeg.FfmpegNinjaTranscodeCommandBuilder");

		node = ProfileUtilTest.validVideoXmlNodeWithNinjaCommandWithCustomProfileCommandHandler();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getNinjaCommand(), "$transcode_cmd -i $if c:a copy c:v copy $of");
		Assert.assertEquals(profile.getCustomProfileCommandHandler(), "com.my.command.MyCommandHandler");
				
	}
	
	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithInvalidNode()  {
		try {
			ProfileBuilder.buildProfile(null);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithInvalidId()  {
		try {
			Node node = ProfileUtilTest.idLessXmlNode();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithInvalidCodecs()  {
		try {
			Node node = ProfileUtilTest.videoCodecLessXmlNode();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
		try {
			// This should pass though! - Since for a video codec if audio is missing its OK for now 
			Node node = ProfileUtilTest.audioCodecLessXmlNode();
			ProfileBuilder.buildProfile(node);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
		try {
			Node node = ProfileUtilTest.audioCodecLessXmlNodeForAudioProfile();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithInvalidType()  {
		try {
			Node node = ProfileUtilTest.typeLessXmlNode();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
		try {
			Node node = ProfileUtilTest.invalidTypeXmlNode();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test method for {@link com.pixelsimple.appcore.media.ProfileBuilder#buildProfile(org.w3c.dom.Node)}.
	 * @throws Exception 
	 */
	@Test
	public void buildProfileWithInvalidContainer()  {
		try {
			Node node = ProfileUtilTest.containerLessXmlNode();
			ProfileBuilder.buildProfile(node);
			Assert.fail();
		} catch (ProfileBuilderException e) {
			// Ok
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	
}
