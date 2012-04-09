/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;

/**
 *
 * @author Akshay Sharma
 * Apr 7, 2012
 */
public class HlsTranscoderCallbackHandler extends TranscoderCallbackHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HlsTranscoderCallbackHandler.class);
	String hlsTranscodeCompleteFilePath;

	public HlsTranscoderCallbackHandler(Handle handle, String hlsTranscodeCompleteFilePath) {
		super(handle);
		this.hlsTranscodeCompleteFilePath = hlsTranscodeCompleteFilePath;
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.commons.command.callback.TranscoderCallbackHandler#onCommandComplete(com.pixelsimple.commons.command.CommandRequest, com.pixelsimple.commons.command.CommandResponse)
	 */
	@Override
	public void onCommandComplete(CommandRequest commandRequest, CommandResponse commandResponse) {
		this.createHlsTranscodeCompleteFilePath();
		super.onCommandComplete(commandRequest, commandResponse);
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.commons.command.callback.TranscoderCallbackHandler#onCommandFailed(com.pixelsimple.commons.command.CommandRequest, com.pixelsimple.commons.command.CommandResponse)
	 */
	@Override
	public void onCommandFailed(CommandRequest commandRequest, CommandResponse commandResponse) {
		this.createHlsTranscodeCompleteFilePath();
		super.onCommandFailed(commandRequest, commandResponse);
	}
	
	private void createHlsTranscodeCompleteFilePath() {
		LOG.debug("createHlsTranscodeCompleteFilePath::Creating the file - {}", this.hlsTranscodeCompleteFilePath);
		File f = new File(this.hlsTranscodeCompleteFilePath);
		
		try {
			f.createNewFile();
		} catch (IOException e) {
			LOG.error("createHlsTranscodeCompleteFilePath::Could not create the hls transcode complete file: {}", 
					this.hlsTranscodeCompleteFilePath, e);
		}
		
	}
}
