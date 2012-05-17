/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.config;

import com.pixelsimple.appcore.registry.GenericRegistryEntryKey;

/**
 *
 * @author Akshay Sharma
 * May 16, 2012
 */
public enum TranscoderRegistryKeys implements GenericRegistryEntryKey {

	TRANSCODER_CONFIG,
	MEDIA_PROFILES,
	TRANSCODE_COMMAND_CHAIN,
	TRANSCODER_QUEUE,;

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.registry.GenericRegistryEntryKey#getUniqueModuleName()
	 */
	@Override
	public String getUniqueModuleName() {
		return "transcoder";
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.registry.GenericRegistryEntryKey#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return name();
	}
}
