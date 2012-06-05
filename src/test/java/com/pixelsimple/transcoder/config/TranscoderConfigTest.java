/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.config;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.appcore.init.BootstrapInitializer;
import com.pixelsimple.appcore.registry.RegistryService;
import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.transcoder.init.TranscoderInitializer;

/**
 *
 * @author Akshay Sharma
 * May 16, 2012
 */
public class TranscoderConfigTest {

	@Before
	public void setUp() throws Exception {
		// init with transcoder initializer
		TestAppInitializer.bootStrapRegistryForTesting(new TranscoderInitializer());
	}
	
	@Test
	public void initWithValidValues() {
		TranscoderConfig transcoderConfig = RegistryService.getGenericRegistryEntry().getEntry(
					TranscoderRegistryKeys.TRANSCODER_CONFIG);
		Map<String, String> configs =  RegistryService.getRegisteredApiConfig().getEnvironment().getImmutableApplicationConfiguratations();
		
		Assert.assertEquals(transcoderConfig.getHlsTranscodeCompleteFile(), "pixelsimple_hls_transcode.complete");
		Assert.assertEquals(transcoderConfig.getHlsPlaylistGeneratorPath(),  
				configs.get(BootstrapInitializer.JAVA_SYS_ARG_APP_HOME_DIR) + OSUtils.folderSeparator() + configs.get("ffmpegPath"));
		Assert.assertEquals(transcoderConfig.getHlsFileSegmentPattern(), "%06d");
		Assert.assertEquals(transcoderConfig.getNinjaAudioBitratePattern(), "\\$ab");
		Assert.assertEquals(transcoderConfig.getNinjaVideoBitratePattern(), "\\$vb");
		Assert.assertEquals(transcoderConfig.getNinjaInputFilePattern(), "\\$if");
		Assert.assertEquals(transcoderConfig.getNinjaOutputFilePattern(), "\\$of");
	}

}
