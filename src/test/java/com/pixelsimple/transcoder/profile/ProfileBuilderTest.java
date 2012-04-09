/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;

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
		Node node = validVideoXmlNode();
		Profile profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getId(), "Opera_10.5_high_bandwidth");
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertNotNull(profile.getVideoCodecs().get(0).getName());
		Assert.assertEquals(profile.getFileFormat(), "avi");
		Assert.assertEquals(profile.getAudioSampleRate(), "8000");
		Assert.assertEquals(profile.getCustomProfileCommandHandler(), "java.lang.String");
		Assert.assertEquals(profile.isHlsProfile(), true);
		
		node = validAudioXmlNode();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertEquals(profile.getVideoCodecs().size(), 0);
		Assert.assertEquals(profile.isHlsProfile(), false);
		
		node = validVideoXmlNodeWithCData();
		profile = ProfileBuilder.buildProfile(node);
		Assert.assertEquals(profile.getId(), "Opera_10.5_high_bandwidth");
		Assert.assertEquals(profile.getAudioCodecs().get(0).getName(), "libvorbis");
		Assert.assertNotNull(profile.getVideoCodecs().get(0).getName());
		Assert.assertTrue(profile.getName().startsWith("This is a multiline \"name\""));
		Assert.assertTrue(profile.getName().contains("<name> - !!"));
		
		node = validVideoXmlNodeWithMultipleCodecs();
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
			Node node = idLessXmlNode();
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
			Node node = videoCodecLessXmlNode();
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
			Node node = audioCodecLessXmlNode();
			ProfileBuilder.buildProfile(node);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
		try {
			Node node = audioCodecLessXmlNodeForAudioProfile();
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
			Node node = typeLessXmlNode();
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
			Node node = invalidTypeXmlNode();
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
			Node node = containerLessXmlNode();
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
	
	private Node validVideoXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><fileFormat>avi</fileFormat><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><audioSampleRate>8000</audioSampleRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria><customProfileCommandHandler>java.lang.String</customProfileCommandHandler><hls>true</hls></profile>";
		return asNode(xml);
	}

	private Node validAudioXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>audio</type><container>ogg</container><fileExtension>ogg</fileExtension><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node validVideoXmlNodeWithCData() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><criteria>Firefox 10.5 and lower. Supports Ogg.</criteria><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters>" 
				+
				"<name>"
				+ "<![CDATA[This is a multiline \"name\" "
				+ "	if (a < b && a < 0) then do_something()"		
				+ "		this profile is fantastic to have such a cool "
				+ " <name> - !! "
				+ "]]>"				
				+ "</name></profile>";
		return asNode(xml);
	}

	private Node validVideoXmlNodeWithMultipleCodecs() {
		String xml = "<profile><id>ipod_standalone</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>mp4</container><fileExtension>m4v</fileExtension><videoCodec>libx264|mpeg4</videoCodec><audioCodec>aac,ac3|aac,ac3,libmp3lame</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node idLessXmlNode() {
		String xml = "<profile><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node typeLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node invalidTypeXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>blah</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node videoCodecLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec></videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node audioCodecLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec></audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node audioCodecLessXmlNodeForAudioProfile() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>audio</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec></audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node containerLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container></container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	private Node asNode(String xml) {
		Node node = null;
		try {
			node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		return node;

	}
	
}
