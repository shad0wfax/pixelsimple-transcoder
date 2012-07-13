/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.exception.TranscoderException;

/**
 * Only for non-HLS transcoding. For HLS transcoding use 
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class Transcoder extends AbstractTranscoder {
	private static final Logger LOG = LoggerFactory.getLogger(Transcoder.class);
	
	public Handle transcodeIt(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException {
		Handle handle = this.buildHandle(spec);
		CommandRequest commandRequest = this.buildCommand(inputMedia, spec);
		
		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(new TranscoderCallbackHandler(handle), true);
		runner.runCommand(commandRequest, new CommandResponse());
		
		LOG.debug("transcode::requested transcoding in async mode. Handle returned - {}", handle);
		return handle;
	}

}
