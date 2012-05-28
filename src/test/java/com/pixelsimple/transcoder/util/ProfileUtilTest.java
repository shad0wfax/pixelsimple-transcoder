/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.util;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Node;

/**
 *
 * @author Akshay Sharma
 * May 18, 2012
 */
public class ProfileUtilTest {
	public static Node validVideoXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><fileFormat>avi</fileFormat><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><audioSampleRate>8000</audioSampleRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria><customProfileCommandHandler>java.lang.String</customProfileCommandHandler><hls>true</hls></profile>";
		return asNode(xml);
	}

	public static Node validAudioXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>audio</type><container>ogg</container><fileExtension>ogg</fileExtension><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node validVideoXmlNodeWithCData() {
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

	public static Node validVideoXmlNodeWithMultipleCodecs() {
		String xml = "<profile><id>ipod_standalone</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>mp4</container><fileExtension>m4v</fileExtension><videoCodec>libx264|mpeg4</videoCodec><audioCodec>aac,ac3|aac,ac3,libmp3lame</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node idLessXmlNode() {
		String xml = "<profile><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node typeLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node invalidTypeXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>blah</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node videoCodecLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec></videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node audioCodecLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec></audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node audioCodecLessXmlNodeForAudioProfile() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>audio</type><container>ogg</container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec></audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node containerLessXmlNode() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container></container><fileExtension>ogg</fileExtension><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria></profile>";
		return asNode(xml);
	}

	public static Node validVideoXmlNodeWithNinjaCommand() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><fileFormat>avi</fileFormat><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate>100k</videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><audioSampleRate>8000</audioSampleRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria><hls>true</hls><ninjaCommand>$ffmpeg -i $if -c:a copy -b:a $ab -c:v copy -b:v $vb $of</ninjaCommand></profile>";
		return asNode(xml);
	}

	public static Node validVideoXmlNodeWithNinjaCommand2() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><fileFormat>avi</fileFormat><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate>100k</videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><audioSampleRate>8000</audioSampleRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria><hls>false</hls><ninjaCommand>$ffmpeg -i $if -c:a copy -b:a $ab -c:v copy -b:v 100k /video/transcode/out.mp4</ninjaCommand></profile>";
		return asNode(xml);
	}

	public static Node validVideoXmlNodeWithNinjaCommandWithCustomProfileCommandHandler() {
		String xml = "<profile><id>Opera_10.5_high_bandwidth</id><name>Firefox 10.5 and lower. Supports Ogg.</name><type>video</type><container>ogg</container><fileExtension>ogg</fileExtension><fileFormat>avi</fileFormat><videoCodec>libtheora</videoCodec><audioCodec>libvorbis</audioCodec><videoBitRate></videoBitRate><vidoeQuality>3</vidoeQuality><audioBitRate>160k</audioBitRate><audioSampleRate>8000</audioSampleRate><aspectRatio>SAME_AS_SOURCE</aspectRatio><maxWidth>SAME_AS_SOURCE</maxWidth><frameRateFPS>SAME_AS_SOURCE</frameRateFPS><optionalAdditionalParameters></optionalAdditionalParameters><criteria>Opera10.5,Desktop,PC,Windows,Mac</criteria><hls>true</hls><ninjaCommand>$transcode_cmd -i $if c:a copy c:v copy $of</ninjaCommand><customProfileCommandHandler>com.my.command.MyCommandHandler</customProfileCommandHandler></profile>";
		return asNode(xml);
		
	}

	private static Node asNode(String xml) {
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
	

	/**
	 * This test case is needed to ensure junit passes this class. 
	 */
	@Test
	public void alwaysTrue() {
		Assert.assertTrue(true);
	}

}
