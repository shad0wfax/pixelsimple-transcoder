/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.transcoder.command.TranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;

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
		AbstractFfmpegTranscodeCommandBuilder start = (AbstractFfmpegTranscodeCommandBuilder) chain.getChainStart();
		
		Assert.assertSame(chainStart, start);
		
		AbstractFfmpegTranscodeCommandBuilder element = new FfmpegAudioTranscodeCommandBuilder();
		chain.addNextSuccessor(element);

		Assert.assertSame(element, start.successor);
		
		AbstractFfmpegTranscodeCommandBuilder element2 = new FfmpegHLSTranscodeCommandBuilder();
		chain.addNextSuccessor(element2);

		Assert.assertSame(element2, element.successor);

		// This shouldn't be added since the same class already exists once.
		chain.addNextSuccessor(new FfmpegHLSTranscodeCommandBuilder());
		
		Assert.assertEquals(element2.successor, null);
		
		
	}

}
