/**
 * � PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.init;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.appcore.Registrable;
import com.pixelsimple.appcore.registry.MapRegistry;
import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.transcoder.Handle;
import com.pixelsimple.transcoder.TranscodeStatus;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 13, 2012
 */
public class TranscoderInitializerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestAppInitializer.bootStrapRegistryForTesting();
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.init.TranscoderInitializer#initialize(com.pixelsimple.appcore.Registry)}.
	 */
	@Test
	public void initialize() {
		TranscoderInitializer initializer = new TranscoderInitializer();
		try {
			initializer.initialize(MapRegistry.INSTANCE);
			Map<String, Profile> profiles = (Map<String, Profile>) MapRegistry.INSTANCE.fetch(Registrable.MEDIA_PROFILES);
			TranscodeCommandBuilderChain chain = (TranscodeCommandBuilderChain) MapRegistry.INSTANCE.fetch(Registrable.TRANSCODE_COMMAND_CHAIN);
			Map<Handle, TranscodeStatus> queue = (Map<Handle, TranscodeStatus>) MapRegistry.INSTANCE.fetch(Registrable.TRANSCODER_QUEUE);

			Assert.assertNotNull(profiles);
			Assert.assertNotNull(chain);
			Assert.assertNotNull(queue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.init.TranscoderInitializer#deinitialize(com.pixelsimple.appcore.Registry)}.
	 */
	@Test
	public void deinitialize() {
		
		TranscoderInitializer initializer = new TranscoderInitializer();
		try {
			initializer.initialize(MapRegistry.INSTANCE);
			Map<String, Profile> profiles = (Map<String, Profile>) MapRegistry.INSTANCE.fetch(Registrable.MEDIA_PROFILES);
			TranscodeCommandBuilderChain chain = (TranscodeCommandBuilderChain) MapRegistry.INSTANCE.fetch(Registrable.TRANSCODE_COMMAND_CHAIN);

			Assert.assertNotNull(profiles);
			Assert.assertNotNull(chain);
			
			initializer.deinitialize(MapRegistry.INSTANCE);
			profiles = (Map<String, Profile>) MapRegistry.INSTANCE.fetch(Registrable.MEDIA_PROFILES);
			chain = (TranscodeCommandBuilderChain) MapRegistry.INSTANCE.fetch(Registrable.TRANSCODE_COMMAND_CHAIN);

			Assert.assertNull(profiles);
			Assert.assertNull(chain);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

}
