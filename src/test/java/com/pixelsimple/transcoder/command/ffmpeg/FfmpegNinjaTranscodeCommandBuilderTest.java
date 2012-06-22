/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.Resource;
import com.pixelsimple.appcore.Resource.RESOURCE_TYPE;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.media.MediaInspector;
import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.commons.test.util.TestUtil;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.init.TranscoderInitializer;
import com.pixelsimple.transcoder.profile.Profile;
import com.pixelsimple.transcoder.profile.ProfileBuilder;
import com.pixelsimple.transcoder.util.ProfileUtilTest;

/**
 *
 * @author Akshay Sharma
 * May 18, 2012
 */
public class FfmpegNinjaTranscodeCommandBuilderTest {
	private static final Logger LOG = LoggerFactory.getLogger(FfmpegNinjaTranscodeCommandBuilderTest.class);
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// init with transcoder initializer
		TestAppInitializer.bootStrapRegistryForTesting(new TranscoderInitializer());
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.command.ffmpeg.FfmpegNinjaTranscodeCommandBuilder#buildTranscodeCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)}.
	 */
	@Test
	public void buildTranscodeCommandValidCommand() {
		String mediaPath = TestAppInitializer.TEST_ARTIFACT_DIR + "video1.mov";
		Resource res = new Resource(mediaPath, RESOURCE_TYPE.FILE); 
		Resource outDir = new Resource(TestAppInitializer.TEST_ARTIFACT_DIR, RESOURCE_TYPE.DIRECTORY);
		
		if (TestUtil.fileExists(mediaPath)) {
			MediaInspector inspector = new MediaInspector();
			try {
				Container container = inspector.createMediaContainer(res);
				Profile targetProfile = ProfileBuilder.buildProfile(ProfileUtilTest.validVideoXmlNodeWithNinjaCommand());
				TranscoderOutputSpec spec = new TranscoderOutputSpec(targetProfile, outDir, "transcoded_video1");
				
				FfmpegNinjaTranscodeCommandBuilder builder = new FfmpegNinjaTranscodeCommandBuilder();
				CommandRequest req = builder.buildTranscodeCommand(container, spec);
				LOG.debug("buildTranscodeCommandValidCommandLessReplacement::req.getCommandAsString() = {}", req.getCommandAsString());
				
				Assert.assertEquals(req.getCommandAsString(), TestUtil.getTestConfig().get(TestUtil.FFMPEG_EXECUTABLE_CONFIG) 
						+ " -i " + TestAppInitializer.TEST_ARTIFACT_DIR + "video1.mov -c:a copy -b:a 160k -c:v copy -b:v 100k " 
						+ TestAppInitializer.TEST_ARTIFACT_DIR + "transcoded_video1.ogg");
				
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			
		} else {
			LOG.error("buildTranscodeCommand::Did not find the media to test with, logging and passing the test.");
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.command.ffmpeg.FfmpegNinjaTranscodeCommandBuilder#buildTranscodeCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)}.
	 */
	@Test
	public void buildTranscodeCommandValidCommandLessReplacement() {
		String mediaPath = TestAppInitializer.TEST_ARTIFACT_DIR + "video1.mov";
		Resource res = new Resource(mediaPath, RESOURCE_TYPE.FILE); 
		Resource outDir = new Resource(TestAppInitializer.TEST_ARTIFACT_DIR, RESOURCE_TYPE.DIRECTORY);
		
		if (TestUtil.fileExists(mediaPath)) {
			MediaInspector inspector = new MediaInspector();
			try {
				Container container = inspector.createMediaContainer(res);
				Profile targetProfile = ProfileBuilder.buildProfile(ProfileUtilTest.validVideoXmlNodeWithNinjaCommand2());
				TranscoderOutputSpec spec = new TranscoderOutputSpec(targetProfile, outDir, "transcoded_video1");
				
				FfmpegNinjaTranscodeCommandBuilder builder = new FfmpegNinjaTranscodeCommandBuilder();
				CommandRequest req = builder.buildTranscodeCommand(container, spec);
				LOG.debug("buildTranscodeCommandValidCommandLessReplacement::req.getCommandAsString() = {}", req.getCommandAsString());
				
				if (OSUtils.isWindows()) {
					Assert.assertEquals(req.getCommandAsString(), TestUtil.getTestConfig().get(TestUtil.FFMPEG_EXECUTABLE_CONFIG)
							+ " -i " + TestAppInitializer.TEST_ARTIFACT_DIR + "video1.mov -c:a copy -b:a 160k -c:v copy -b:v 100k \\video\\transcode\\out.mp4");
				} else if (OSUtils.isMac()) {
					Assert.assertEquals(req.getCommandAsString(), TestUtil.getTestConfig().get(TestUtil.FFMPEG_EXECUTABLE_CONFIG)
							+ " -i " + TestAppInitializer.TEST_ARTIFACT_DIR + "video1.mov -c:a copy -b:a 160k -c:v copy -b:v 100k /video/transcode/out.mp4");
				} else {
					Assert.fail("Linux not supported yet!");
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			
		} else {
			LOG.error("buildTranscodeCommand::Did not find the media to test with, logging and passing the test.");
			Assert.assertTrue(true);
		}
	}

}
