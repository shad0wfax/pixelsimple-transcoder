/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;

/**
 *
 * @author Akshay Sharma
 * Feb 20, 2012
 */
public class TranscodeStatus {
	// TODO: Justify why we need these??
	private CommandRequest commandRequest;
	private CommandResponse commandResponse;
	private STATUS status = STATUS.notstarted;
	
	public static enum STATUS {notstarted, running, completed, failed};
	
	public TranscodeStatus(CommandRequest commandRequest, CommandResponse commandResponse) {
		this.commandRequest = commandRequest;
		this.commandResponse = commandResponse;
	}
	
	public TranscodeStatus start() {
		this.status = STATUS.running;
		return this;
	}

	public TranscodeStatus failed() {
		this.status = STATUS.failed;
		return this;
	}

	public TranscodeStatus completed() {
		this.status = STATUS.completed;
		return this;
	}
	
	public STATUS getStatus() {
		return this.status;
	}
}
