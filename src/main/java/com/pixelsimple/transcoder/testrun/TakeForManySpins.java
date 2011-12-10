/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.testrun;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.transcoder.VideoTranscoder;

/**
 *
 * @author Akshay Sharma
 * Nov 28, 2011
 */
public class TakeForManySpins implements Runnable {
	static final Logger LOG = LoggerFactory.getLogger(TakeForManySpins.class);
	static Map<String, String> overrideParams = null;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		
		try {
			List<Thread> threads = new ArrayList<Thread>();
			
			for (int i  = 0; i < 8; i++) {
				Thread t = new Thread(new TakeForManySpins());
				threads.add(t);
			}
			
			for (Thread t : threads) {
				t.start();
			}
		} catch (Throwable t) {
			LOG.error("{}", t);
		}

	}
	
	private void transcode(Map<String, String> overrideParams) {
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
		String outputFileName = params.get("output_file_name");
		outputFileName = "Thread_" + Thread.currentThread().getId() + "_" + outputFileName;
		
		params.put("output_file_name", outputFileName);
		
		LOG.debug("transcode::final params::{}", params);
		
		VideoTranscoder videoTranscoder = new VideoTranscoder();
		videoTranscoder.transcode(params);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		LOG.debug("run::Starting to transcode - will not block");
		this.transcode(overrideParams);
		LOG.debug("run::Finished to calling transcode");
	}
}
