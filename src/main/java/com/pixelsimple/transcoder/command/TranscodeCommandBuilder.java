/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.transcoder.TranscoderOutputSpec;

/**
 * All implementations have to be STATELESS - Will be cached. This is mandatory and an asbsolute requirement.
 * Even the custom implementations have to be. 
 * This means multiple threads could be calling different methods and nothing should be synchronized. 
 * Methods should be sel-contained and not change the implementation's state/behavior.
 *
 * @author Akshay Sharma
 * Feb 11, 2012
 */
public interface TranscodeCommandBuilder {

	public void setSuccessor(TranscodeCommandBuilder successor);
	
	public CommandRequest buildCommand(Container inputMedia, TranscoderOutputSpec spec);
	
}
