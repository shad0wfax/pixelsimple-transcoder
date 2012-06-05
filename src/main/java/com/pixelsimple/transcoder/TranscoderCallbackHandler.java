/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.queue.QueueService;
import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.callback.AsyncCallbackHandler;

/**
 *
 * @author Akshay Sharma
 * Feb 20, 2012
 */
public class TranscoderCallbackHandler implements AsyncCallbackHandler {
	private static final Logger LOG = LoggerFactory.getLogger(TranscoderCallbackHandler.class);
	
	protected Handle handle;
	
	public TranscoderCallbackHandler(Handle handle) {
		this.handle = handle;
	}
			
	/* (non-Javadoc)
	 * @see com.pixelsimple.commons.command.callback.AsyncCallbackHandler#onCommandStart(com.pixelsimple.commons.command.CommandRequest, com.pixelsimple.commons.command.CommandResponse)
	 */
	@Override
	public void onCommandStart(CommandRequest commandRequest, CommandResponse commandResponse) {
		LOG.debug("Starting the command, enqueing - {} the handle :{}", commandRequest.getCommandAsString(), this.handle);
		TranscodeStatus status = new TranscodeStatus(commandRequest, commandResponse);
		status.start();
		QueueService.getQueue().enqueue(this.handle, status);
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.commons.command.callback.AsyncCallbackHandler#onCommandComplete(com.pixelsimple.commons.command.CommandRequest, com.pixelsimple.commons.command.CommandResponse)
	 */
	@Override
	public void onCommandComplete(CommandRequest commandRequest, CommandResponse commandResponse) {
		LOG.debug("Completed running the command, dequeing - {} \n the output of the command \n:{}", 
			commandRequest.getCommandAsString(), commandResponse);
		TranscodeStatus status = QueueService.getQueue().peek(this.handle);
		
		if (status != null) {
			status.completed();
			QueueService.getQueue().dequeue(this.handle);
		}
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.commons.command.callback.AsyncCallbackHandler#onCommandFailed(com.pixelsimple.commons.command.CommandRequest, com.pixelsimple.commons.command.CommandResponse)
	 */
	@Override
	public void onCommandFailed(CommandRequest commandRequest, CommandResponse commandResponse) {
		LOG.debug("Failed running the command, dequeing - {} \n the output of the command \n:{}", commandRequest.getCommandAsString(), 
				commandResponse);
		LOG.debug("Failure cause:\n {}", commandResponse.getFailureCause());

		TranscodeStatus status = QueueService.getQueue().peek(this.handle);
		
		if (status != null) {
			status.failed();
			QueueService.getQueue().dequeue(this.handle);
		}
	}

}
