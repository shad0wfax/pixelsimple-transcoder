/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command.ffmpeg;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.transcoder.TranscoderOutputSpec;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderApi;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.init.TranscoderInitializer;

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
		// init with transcoder initializer
		TestAppInitializer.bootStrapRegistryForTesting(new TranscoderInitializer());
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
		
		TranscodeCommandBuilder sampleTranscodeCommandBuilderApiBuilder = new SampleTranscodeCommandBuilderApi();
		chain.addNextSuccessor(sampleTranscodeCommandBuilderApiBuilder);
		
		Assert.assertEquals(element2.successor, sampleTranscodeCommandBuilderApiBuilder);
		
	}
	
	private static class SampleTranscodeCommandBuilderApi extends TranscodeCommandBuilderApi {

		/* (non-Javadoc)
		 * @see com.pixelsimple.transcoder.command.TranscodeCommandBuilderApi#buildTranscodeCommand(com.pixelsimple.commons.media.Container, com.pixelsimple.transcoder.TranscoderOutputSpec)
		 */
		@Override
		public CommandRequest buildTranscodeCommand(Container inputMedia, TranscoderOutputSpec spec) {
			return null;
		}
		
	}

}
