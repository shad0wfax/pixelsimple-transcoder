/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.init;

import java.util.Collection;
import java.util.Map;

import com.pixelsimple.appcore.Registrable;
import com.pixelsimple.appcore.Registry;
import com.pixelsimple.appcore.init.Initializable;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.TranscodeCommandBuilderChain;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegAudioTranscodeCommandBuilder;
import com.pixelsimple.transcoder.command.ffmpeg.FfmpegVideoTranscodeCommandBuilder;
import com.pixelsimple.transcoder.exception.TranscoderException;
import com.pixelsimple.transcoder.profile.Profile;
import com.pixelsimple.transcoder.profile.ProfileBuilder;

/**
 *
 * @author Akshay Sharma
 * Feb 12, 2012
 */
public class TranscoderInitializer implements Initializable {

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.init.Initializable#initialize(com.pixelsimple.appcore.Registry)
	 */
	@Override
	public void initialize(Registry registry) throws Exception {
		Map<String, Profile> profiles = this.initMediaProfiles(registry);

		this.initTranscodeCommandChain(registry, profiles);
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.init.Initializable#deinitialize(com.pixelsimple.appcore.Registry)
	 */
	@Override
	public void deinitialize(Registry registry) throws Exception {
		registry.remove(Registrable.MEDIA_PROFILES);
		registry.remove(Registrable.TRANSCODE_COMMAND_CHAIN);
	}

	private Map<String, Profile> initMediaProfiles(Registry registry) throws Exception {
		Map<String, Profile> profiles = ProfileBuilder.parseDefaultMediaProfiles();
		
		// Load these objects up in registry
		registry.register(Registrable.MEDIA_PROFILES, profiles);
		
		return profiles;
	}

	private void initTranscodeCommandChain(Registry registry, Map<String, Profile> profilesMap) throws TranscoderException {
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
		registry.register(Registrable.TRANSCODE_COMMAND_CHAIN, chain);		
	}

}
