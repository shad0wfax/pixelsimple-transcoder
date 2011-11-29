/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.testrun;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.transcoder.Transcoder;

/**
 *
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class TakeForASpin {
	static final Logger LOG = LoggerFactory.getLogger(TakeForASpin.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			TakeForASpin forASpin = new TakeForASpin();
			forASpin.takeForASpin(args);
		} catch (Throwable t) {
			LOG.error("{}", t);
		}

	}
	
	public void takeForASpin(String[] args) {
		Map<String, String> overrideParams = null;
		
		if (args != null && args.length > 0) {
			overrideParams = new HashMap<String, String>(8);
			
			for (String a : args) {
				int indexEquals = a.indexOf("=");
				
				if (indexEquals != -1) {
					overrideParams.put(a.substring(0, indexEquals), a.substring(indexEquals + 1));
				}
			}
		}
		LOG.debug("takeForASping:Override params::{}", overrideParams);
		
		this.transcode(overrideParams);
	}
	
	public void transcode(Map<String, String> overrideParams) {
		Map<String, String> params = new HashMap<String, String>(8);
		
		ResourceBundle bundle = ResourceBundle.getBundle("default_transcoder_settings");
		Enumeration<String> keys = bundle.getKeys();
		
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			params.put(key, value);
		}
		
		if (overrideParams != null) {
			params.putAll(overrideParams);
		}
		LOG.debug("transcode::final params::{}", params);
		
		Transcoder transcoder = new Transcoder();
		transcoder.transcode(params);
	}

}
