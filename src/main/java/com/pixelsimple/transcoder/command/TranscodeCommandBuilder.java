/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.TranscoderOutputSpec;

/**
 *
 * @author Akshay Sharma
 * Feb 11, 2012
 */
public interface TranscodeCommandBuilder {

	public void setSuccessor(TranscodeCommandBuilder successor);
	
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec);
	
}
