/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.queue;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.registry.GenericRegistryEntry;
import com.pixelsimple.appcore.registry.RegistryService;
import com.pixelsimple.transcoder.Handle;
import com.pixelsimple.transcoder.TranscodeStatus;
import com.pixelsimple.transcoder.config.TranscoderRegistryKeys;

/**
 *
 * @author Akshay Sharma
 * Feb 20, 2012
 */
public class TranscoderQueue {
	private static final Logger LOG = LoggerFactory.getLogger(TranscoderQueue.class);
	
	private TranscoderQueue() {}
	
	// TODO: Should be synchronized?? Repeated enqueue should be prevented. 
	public static void enqueue(Handle handle, TranscodeStatus status) {
		Map<Handle, TranscodeStatus> transcoderQueue = getTranscoderQueue();
		transcoderQueue.put(handle, status);
		
		LOG.debug("enqueue::Added to the transcoder queue - handle {} status {}", handle, status);
	}
	
	public static TranscodeStatus dequeue(Handle handle) {
		Map<Handle, TranscodeStatus> transcoderQueue = getTranscoderQueue();

		LOG.debug("dequeue::Removed from transcoder queue - handle {}", handle);
		
		return transcoderQueue.remove(handle);
	}
	
	public static TranscodeStatus peek(Handle handle) {
		Map<Handle, TranscodeStatus> transcoderQueue = getTranscoderQueue();
		TranscodeStatus status = transcoderQueue.get(handle); 

		LOG.debug("peek::Peeking from transcoder queue - handle {} with status {}", handle, status);
		
		return status;
	}
	
	private static Map<Handle, TranscodeStatus> getTranscoderQueue() {
		GenericRegistryEntry entry = RegistryService.getGenericRegistryEntry();
		@SuppressWarnings("unchecked")
		Map<Handle, TranscodeStatus> transcoderQueue = (Map<Handle, TranscodeStatus>) entry.getEntry(
				TranscoderRegistryKeys.TRANSCODER_QUEUE);
		
		return transcoderQueue;

	}
	
}
