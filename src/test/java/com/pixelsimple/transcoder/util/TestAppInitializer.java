/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.pixelsimple.appcore.init.AppInitializer;
import com.pixelsimple.appcore.init.BootstrapInitializer;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.OSUtils.OS;

/**
 *
 * @author Akshay Sharma
 * Jan 18, 2012
 */
public class TestAppInitializer {
	
	public static void bootStrapRegistryForTesting() {
		
		Map<String, String> configs = new HashMap<String, String>();
		
		if (OSUtils.CURRENT_OS == OS.WINDOWS) {
			// Keep this path up to date with ffmpeg updates
			configs.put(BootstrapInitializer.JAVA_SYS_ARG_APP_HOME_DIR, "c:/dev/pixelsimple");
			configs.put("ffprobePath", "c:/dev/pixelsimple/ffprobe/32_bit/0.8/ffprobe.exe"); 
			configs.put("ffmpegPath", "c:/dev/pixelsimple/ffmpeg/32_bit/0.8/ffmpeg.exe"); 
		} else if (OSUtils.CURRENT_OS == OS.MAC) {
			// Keep this path up to date with ffmpeg updates
			configs.put(BootstrapInitializer.JAVA_SYS_ARG_APP_HOME_DIR,  OSUtils.USER_SYSTEM_HOME_DIR + "/dev/pixelsimple");
			configs.put("ffprobePath",  OSUtils.USER_SYSTEM_HOME_DIR + "/dev/pixelsimple/ffprobe/32_bit/0.7_beta2/ffprobe"); 
			configs.put("ffmpegPath",  OSUtils.USER_SYSTEM_HOME_DIR + "/dev/pixelsimple/ffmpeg/32_bit/0.8.7/ffmpeg"); 
		}  else {
			// add linux based tests when ready :-)
		}
		
		AppInitializer initializer = new AppInitializer();
		try {
			initializer.init(configs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * This test case is needed to ensure junit passes this class. 
	 */
	@Test
	public void alwaysTrue() {
		Assert.assertTrue(true);
	}
}
