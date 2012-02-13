/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.transcoder.command.ffmpeg.FfmpegAudioTranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegVideoTranscodeCommandBuilder;

/**
 *
 * @author Akshay Sharma
 * Feb 13, 2012
 */
public class TranscodeCommandBuilderChainTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain#addNextSuccessor(com.pixelsimple.transcoder.command.TranscodeCommandBuilder)}.
	 */
	@Test
	public void addNextSuccessor() {
		TranscodeCommandBuilder chainStart = new FfmpegVideoTranscodeCommandBuilder();
		TranscodeCommandBuilderChain chain = new TranscodeCommandBuilderChain(chainStart);
		TranscodeCommandBuilder start = chain.getChainStart();
		
		Assert.assertSame(chainStart, start);
		
		TranscodeCommandBuilder element = new FfmpegAudioTranscodeCommandBuilder();
		chain.addNextSuccessor(element);
	}

}
