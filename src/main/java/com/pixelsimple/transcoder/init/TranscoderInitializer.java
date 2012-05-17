/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.config.Environment;
import com.pixelsimple.appcore.init.Initializable;
import com.pixelsimple.appcore.registry.GenericRegistryEntry;
import com.pixelsimple.appcore.registry.Registry;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.Handle;
import com.pixelsimple.transcoder.TranscodeStatus;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegAudioTranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegVideoTranscodeCommandBuilder;
import com.pixelsimple.transcoder.config.TranscoderConfig;
import com.pixelsimple.transcoder.config.TranscoderRegistryKeys;
import com.pixelsimple.transcoder.exception.TranscoderException;
import com.pixelsimple.transcoder.profile.Profile;
import com.pixelsimple.transcoder.profile.ProfileBuilder;

/**
 *
 * @author Akshay Sharma
 * Feb 12, 2012
 */
public class TranscoderInitializer implements Initializable {
	private static final Logger LOG = LoggerFactory.getLogger(TranscoderInitializer.class);
	private static final String APP_CONFIG_HLS_COMPLETE_FILE = "hlsTranscodeCompleteFile";
	private static final String APP_CONFIG_HLS_PLAYLIST_GENERATOR_PATH = "hlsPlaylistGeneratorPath";
	private static final String APP_CONFIG_HLS_FILE_SEGMENT_PATTERN = "hlsFileSegmentPattern";
	

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.init.Initializable#initialize(com.pixelsimple.appcore.Registry)
	 */
	@Override
	public void initialize(Registry registry, ApiConfig apiConfig) throws Exception {
		this.initTranscoderConfig(registry, apiConfig);
		
		Map<String, Profile> profiles = this.initMediaProfiles(registry, apiConfig);
		this.initTranscodeCommandChain(registry, apiConfig, profiles);
		
		this.initTranscoderQueue(registry, apiConfig);
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.init.Initializable#deinitialize(com.pixelsimple.appcore.Registry)
	 */
	@Override
	public void deinitialize(Registry registry, ApiConfig apiConfig) throws Exception {
		GenericRegistryEntry genericRegistryEntry = apiConfig.getGenericRegistryEntry();
		genericRegistryEntry.removeEntry(TranscoderRegistryKeys.MEDIA_PROFILES);
		genericRegistryEntry.removeEntry(TranscoderRegistryKeys.TRANSCODE_COMMAND_CHAIN);
		genericRegistryEntry.removeEntry(TranscoderRegistryKeys.TRANSCODER_QUEUE);
	}

	private void initTranscoderConfig(Registry registry, ApiConfig apiConfig) {
		Environment env = apiConfig.getEnvironment();
		Map<String, String> configMap = env.getApplicationConfiguratations();
		TranscoderConfig tConfig = new TranscoderConfig();
		tConfig.setHlsFileSegmentPattern(configMap.get(APP_CONFIG_HLS_FILE_SEGMENT_PATTERN));
		tConfig.setHlsPlaylistGeneratorPath(env.getAppBasePath() + configMap.get(APP_CONFIG_HLS_PLAYLIST_GENERATOR_PATH));
		tConfig.setHlsTranscodeCompleteFile(configMap.get(APP_CONFIG_HLS_COMPLETE_FILE));
		
		GenericRegistryEntry genericRegistryEntry = apiConfig.getGenericRegistryEntry();
		genericRegistryEntry.addEntry(TranscoderRegistryKeys.TRANSCODER_CONFIG, tConfig);
	}

	private Map<String, Profile> initMediaProfiles(Registry registry, ApiConfig apiConfig) throws Exception {
		Map<String, Profile> profiles = ProfileBuilder.parseDefaultMediaProfiles();
		
		// Load these objects up in registry
		GenericRegistryEntry genericRegistryEntry = apiConfig.getGenericRegistryEntry();
		genericRegistryEntry.addEntry(TranscoderRegistryKeys.MEDIA_PROFILES, profiles);
		
		LOG.debug("initMediaProfiles registered profile");
		return profiles;
	}

	private void initTranscodeCommandChain(Registry registry, ApiConfig apiConfig, Map<String, Profile> profilesMap) 
			throws TranscoderException {
		// Algo: Create the set of objects that are part of transcoder chain. 
		// TODO: Future - iterate the profiles and find all the custom Profile.ProfileType handlers and add them to 
		// to this as chain as successors. 
		TranscodeCommandBuilder chainStart = new FfmpegVideoTranscodeCommandBuilder();
		TranscodeCommandBuilderChain chain = new TranscodeCommandBuilderChain(chainStart);
		Collection<Profile> profiles = profilesMap.values();
		
		// Keep adding to the chain 
		chain.addNextSuccessor(new FfmpegAudioTranscodeCommandBuilder());

		// Load other custom profile, based on where they are stored - DB
		for (Profile profile : profiles) {
			String customCommandHandler = profile.getCustomProfileCommandHandler();
			
			if (!StringUtils.isNullOrEmpty(customCommandHandler)) {
				try {
					Class clazz = Class.forName(customCommandHandler.trim());
					TranscodeCommandBuilder successor = (TranscodeCommandBuilder) clazz.newInstance(); 
					chain.addNextSuccessor(successor);
				} catch (Exception e) {
					throw new TranscoderException("Cannot instantiate the class - " + customCommandHandler, e);					
				}
			}
		}
		LOG.debug("initTranscodeCommandChain with chain - {}", chain);
		
		GenericRegistryEntry genericRegistryEntry = apiConfig.getGenericRegistryEntry();
		genericRegistryEntry.addEntry(TranscoderRegistryKeys.TRANSCODE_COMMAND_CHAIN, chain);		
	}

	/**
	 * @param registry
	 */
	private void initTranscoderQueue(Registry registry, ApiConfig apiConfig) {
		Map<Handle, TranscodeStatus> transcoderQueue = new HashMap<Handle, TranscodeStatus>(8);
		GenericRegistryEntry genericRegistryEntry = apiConfig.getGenericRegistryEntry();
		genericRegistryEntry.addEntry(TranscoderRegistryKeys.TRANSCODER_QUEUE, transcoderQueue);		
		LOG.debug("initTranscoderQueue initing the Queue}");
	}

}
