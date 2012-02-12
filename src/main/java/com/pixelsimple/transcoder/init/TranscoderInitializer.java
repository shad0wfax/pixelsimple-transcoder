/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.init;

import java.util.Map;

import com.pixelsimple.appcore.Registrable;
import com.pixelsimple.appcore.Registry;
import com.pixelsimple.appcore.init.Initializable;
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
		this.initMediaProfiles(registry);
	}

	/* (non-Javadoc)
	 * @see com.pixelsimple.appcore.init.Initializable#deinitialize(com.pixelsimple.appcore.Registry)
	 */
	@Override
	public void deinitialize(Registry registry) throws Exception {
		registry.remove(Registrable.MEDIA_PROFILES);
	}

	private void initMediaProfiles(Registry registry) throws Exception {
		Map<String, Profile> profiles = ProfileBuilder.parseDefaultMediaProfiles();
		
		// Load these objects up in registry
		registry.register(Registrable.MEDIA_PROFILES, profiles);
	}
}
