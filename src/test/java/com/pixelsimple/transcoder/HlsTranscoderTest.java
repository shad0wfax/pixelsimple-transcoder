/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;

/**
 *
 * @author Akshay Sharma
 * May 27, 2012
 */
public class HlsTranscoderTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

//	/**
//	 * Test method for {@link com.pixelsimple.transcoder.HlsTranscoder#transcodeIt(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)}.
//	 */
//	@Test
//	public void callPlaylistGenerator() {
//
//		CommandRequest req = new CommandRequest();
//		req.addCommand("C:\\Users\\Shad0wfax\\Desktop\\package-app-1.0-SNAPSHOT\\bin\\hls_playlist_generator.bat", 0);
//		req.addArgument("\"C:\\Data\\video_test\\trans coded\\hls\\\"").addArgument("C:\\Data\\video_test\\trans coded\\hls\\web_transcoding.m3u8").addArgument("ts")
//			.addArgument("8").addArgument("C:\\Data\\video_test\\trans coded\\hls\\pixelsimple_hls_transcode.complete").addArgument("10")
//			.addArgument("C:\\Users\\Shad0wfax\\Desktop\\package-app-1.0-SNAPSHOT\\bin\\ffprobe.exe").addArgument("\"staticmedia?inputPath=\"");
//	
//		Handle handle = new Handle("C:\\Data\\video_test\\trans coded\\hls\\web_transcoding.m3u8");
//		handle.setOutputFileCreated("C:\\Data\\video_test\\trans coded\\hls\\web_transcoding.m3u8");
//		
//		CommandResponse res = new CommandResponse();
//		CommandRunner runner2 = CommandRunnerFactory.newBlockingCommandRunner();
//		runner2.runCommand(req, res);
//		
//		System.out.println(res.hasCommandFailed());
//	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.HlsTranscoder#transcodeIt(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)}.
	 */
	@Test
	public void callPlaylistGenerator() {
		Assert.assertTrue(true);
	}
	
}
