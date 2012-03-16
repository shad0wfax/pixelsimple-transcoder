/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pixelsimple.appcore.RegistryService;
import com.pixelsimple.appcore.media.AudioCodec;
import com.pixelsimple.appcore.media.Codec;
import com.pixelsimple.appcore.media.Codecs;
import com.pixelsimple.appcore.media.ContainerFormats;
import com.pixelsimple.appcore.media.MediaInfoParserFactory;
import com.pixelsimple.appcore.media.VideoCodec;
import com.pixelsimple.commons.util.StringUtils;

/**
 * Refer the <profile>...</profile> tag in the mediaProfiles.xml file. This profile builder class builds one profile 
 * object per <profile> tag passed. Ideally this will the single place for building the profiles. So the system could
 * load profiles from the xml as well as user defined profile, but they will have to adhere to the semantics here. 
 * @author Akshay Sharma
 * Feb 4, 2012
 */
public class ProfileBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileBuilder.class);
	
	private ProfileBuilder() {}
	
	public static Map<String, Profile> parseDefaultMediaProfiles() throws Exception {
		InputStream is = MediaInfoParserFactory.class.getResourceAsStream("/com/pixelsimple/transcoder/profile/defaultMediaProfiles.xml");
		
		// TODO: A multi-key map with criteria as keys? - Uniqueness is a problem and lookups might not be best.
		Map<String, Profile> profiles = new HashMap<String, Profile>();
		
		try {
			
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(is);
		    
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    NodeList nodes = (NodeList) xpath.evaluate("//profiles/profile", doc, XPathConstants.NODESET);
		    
		    for (int i = 0; i < nodes.getLength(); i++) {
		    	Node node = nodes.item(i);
		    	Profile profile = ProfileBuilder.buildProfile(node); 
		    	profiles.put(profile.getId(), profile);
		    }		    
		} finally {
			if (is != null) {
				is.close();
			}
		}
		LOG.debug("profiles build :: {} ", profiles);
		return profiles;
	}
	

	public static Profile buildProfile(Node xmlProfileNode) throws Exception {
		if (xmlProfileNode == null)
			throw new ProfileBuilderException("The xmlNode cannot be null");
		
		LOG.debug("buildProfile::building profile using :: {} ", xmlProfileNode.getTextContent());
	    
		XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();

	    String type = (String) xpath.evaluate("type", xmlProfileNode, XPathConstants.STRING);
		if (StringUtils.isNullOrEmpty(type))
			throw new ProfileBuilderException("Profile should have a type associated");
	    
	    Profile.ProfileType profileType = null; 
	    for (Profile.ProfileType pt : Profile.ProfileType.values()) {
	    	if (type.equalsIgnoreCase(pt.name())) {
	    		profileType = pt;
	    		break;
	    	}
	    }
		if (profileType == null)
			throw new ProfileBuilderException("The profileType cannot be null");	

		Profile profile = new Profile(profileType);

		profile.setId((String) xpath.evaluate("id", xmlProfileNode, XPathConstants.STRING));
	    profile.setName((String) xpath.evaluate("name", xmlProfileNode, XPathConstants.STRING));
	    profile.setContainerFormat((String) xpath.evaluate("container", xmlProfileNode, XPathConstants.STRING));
	    profile.setFileExtension((String) xpath.evaluate("fileExtension", xmlProfileNode, XPathConstants.STRING));
	    profile.setVideoBitRate((String) xpath.evaluate("videoBitRate", xmlProfileNode, XPathConstants.STRING));
	    profile.setVideoQuality((String) xpath.evaluate("vidoeQuality", xmlProfileNode, XPathConstants.STRING));
	    profile.setAudioBitRate((String) xpath.evaluate("audioBitRate", xmlProfileNode, XPathConstants.STRING));
	    profile.setAspectRatio((String) xpath.evaluate("aspectRatio", xmlProfileNode, XPathConstants.STRING));
	    profile.setFileFormat((String) xpath.evaluate("fileFormat", xmlProfileNode, XPathConstants.STRING));
	    profile.setAudioSampleRate((String) xpath.evaluate("audioSampleRate", xmlProfileNode, XPathConstants.STRING));
	    profile.setCustomProfileCommandHandler((String) xpath.evaluate("customProfileCommandHandler", xmlProfileNode, 
	    		XPathConstants.STRING));
	    
	    Number maxWidth = (Number) xpath.evaluate("maxWidth", xmlProfileNode, XPathConstants.NUMBER);
	    if (maxWidth != null) {
	    	profile.setMaxWidth(maxWidth.intValue());	
	    }	    
	    profile.setFrameRateFPS((String) xpath.evaluate("frameRateFPS", xmlProfileNode, XPathConstants.STRING));
	    profile.setAdditionalParameters((String) xpath.evaluate("additionalParameters", xmlProfileNode, XPathConstants.STRING));
	    
	    String criteria = (String) xpath.evaluate("criteria", xmlProfileNode, XPathConstants.STRING);
	    if (criteria != null) {
	    	String [] arr = StringUtils.delimitedListToStringArray(criteria, ",");
	    	for (String a : arr) {
	    		profile.addCriteria(a);
	    	}
	    }
	    
	    addCodecs(xmlProfileNode, xpath, profileType, profile);
	    
	    // Validate if the profile is fine -
	   validate(profile);
	    
		return profile;
	}

	/**
	 * @param xmlProfileNode
	 * @param xpath
	 * @param mediaType
	 * @param profile
	 * @throws XPathExpressionException
	 */
	private static void addCodecs(Node xmlProfileNode, XPath xpath, Profile.ProfileType profileType, Profile profile) throws Exception {
		Codecs supportedCodecs = RegistryService.getSupportedCodecs();

		String audioCodec = (String) xpath.evaluate("audioCodec", xmlProfileNode, XPathConstants.STRING);
	    String videoCodec = (String) xpath.evaluate("videoCodec", xmlProfileNode, XPathConstants.STRING);
 
	    if (profileType == Profile.ProfileType.AUDIO && !StringUtils.isNullOrEmpty(audioCodec)) {
		    // Format will be: <audioCodec>mp3,libvorbis,aac,ac3</audioCodec>
	    	// Ignore the video codecs. So we will not have any pipe separated entries. Only a comma separated list.
	    	// Note: Order is important - it indicates priority/preference
	    	String [] codecsString = StringUtils.commaDelimitedListToStringArray(audioCodec);
	    	for (String acodec : codecsString) {
	    		AudioCodec audioCodecSupported = (AudioCodec) supportedCodecs.findCodec(Codec.CODEC_TYPE.AUDIO, acodec);
	    		
	    		if (audioCodecSupported == null)
					throw new ProfileBuilderException("Looks like the audio codec - " + acodec + " is not supported");			
	    		
	    		profile.addAudioOnlyCodec(audioCodecSupported);
	    	}	    	
	    } else if (profileType == Profile.ProfileType.VIDEO && !StringUtils.isNullOrEmpty(videoCodec)) {
		    // Format will be: <videoCodec>webm|libx264</videoCodec>
		    // Format will be: <audioCodec>mp3,libvorbis|mp3,aac,ac3</audioCodec>
	    	// Note: audioCodec can also be missing - TODO: What is the solution in such cases?

	    	String [] vcodecs = StringUtils.delimitedListToStringArray(videoCodec, "|");
	    	String [] acodecs = null;
	    	
	    	if (!StringUtils.isNullOrEmpty(audioCodec)) {
	    		acodecs = StringUtils.delimitedListToStringArray(audioCodec, "|");	
	    	}
	    	for (int i = 0; i < vcodecs.length; i++) {
	    		String vcodec = vcodecs[i];
	    		VideoCodec videoCodecSupported = (VideoCodec) supportedCodecs.findCodec(Codec.CODEC_TYPE.VIDEO, vcodec);

	    		if (videoCodecSupported == null)
					throw new ProfileBuilderException("Looks like the video codec - " + vcodec + " is not supported");			
	    		
	    		profile.addVideoCodec(videoCodecSupported);
	    		
	    		if (acodecs != null && acodecs.length > i) {
	    			String [] acodecForVid = StringUtils.commaDelimitedListToStringArray(acodecs[i]);
	    	    	for (String acodec : acodecForVid) {
	    	    		AudioCodec audioCodecSupported = (AudioCodec) supportedCodecs.findCodec(Codec.CODEC_TYPE.AUDIO, acodec);
	    	    		
	    	    		if (audioCodecSupported == null)
	    					throw new ProfileBuilderException("Looks like the audio codec - " + acodec + " is not supported");			
	    	    		
	    	    		profile.addAssociatedAudioCodec(videoCodecSupported, audioCodecSupported);
	    	    	}	    	
	    		} 
	    	}
	    }
	    // TODO: Add builder logic for other Profile.ProfileType - like Custom etc - Extension point.
	}

	/**
	 * Throws ProfileBuilderException if there are basic validation errors 
	 */
	private static void validate(Profile profile) {
		if (StringUtils.isNullOrEmpty(profile.getId()))
			throw new ProfileBuilderException("Looks like the profile Id is missing - " + profile.toString());
		
		if (StringUtils.isNullOrEmpty(profile.getContainerFormat()))
			throw new ProfileBuilderException("Looks like the container format is missing - " + profile.toString());

		if (profile.getProfileType() == Profile.ProfileType.AUDIO) {
			// At least one audio codec should be supplied:
			if (profile.getAudioCodecs().size() <= 0)
				throw new ProfileBuilderException("Looks like the audio codec for profile type audio is missing - " 
					+ profile.toString());
		} else if (profile.getProfileType() == Profile.ProfileType.VIDEO) {
			// TODO: handle the case of a profile that can say only extract audio - when supported.
			// At least one video codec should be supplied:
			if (profile.getVideoCodecs().size() == 0)
				throw new ProfileBuilderException("Looks like the video codec for profile type video is missing - " 
					+ profile.toString());
		}
	    // TODO: Add validation logic for other Profile.ProfileType - like Custom etc - Extension point.
		
		ContainerFormats formats = RegistryService.getSupportedContainerFormats();
		String format = profile.getContainerFormat();
		
		if (!StringUtils.isNullOrEmpty(format) && !formats.isSupported(format))
			throw new ProfileBuilderException("Looks like the container format - " + format + " is not supported");			
	}


}
