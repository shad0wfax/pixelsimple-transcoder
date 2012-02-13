/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

import java.util.ArrayList;
import java.util.List;

import com.pixelsimple.appcore.media.Codec;

/**
 *
 * @author Akshay Sharma
 * Feb 3, 2012
 */
public final class Profile {
	public static final String SAME_AS_SOURCE_SETTING = "SAME_AS_SOURCE";
	// Can evolve:
	public static enum ProfileType {VIDEO, AUDIO, CUSTOM};
	
	private String id;
	private String name;
	private ProfileType profileType;
	private String containerFormat;
	private String fileExtension;
	private String videoBitRate;
	private String videoQuality;
	private String audioBitRate; 
	private String aspectRatio; 
	private int maxWidth; 
	private String frameRateFPS; 
	private String additionalParameters;
	private String fileFormat;
	private String audioSampleRate;

	// Will have the order maintained. The position also determines the priority.
	private List<VideoCodec> videoCodecs = new ArrayList<VideoCodec>();  
	private List<AudioCodec> audioCodecs = new ArrayList<AudioCodec>();  
	private List<Codec> videoCodecsComputed = new ArrayList<Codec>();
	private List<Codec> audioCodecsComputed = new ArrayList<Codec>();

	public Profile(ProfileType type) {
		this.profileType = type;
	}
	
	// Todo - improve criteria api
	private List<String> criteria = new ArrayList<String>(); 
	
	/**
	 *
	 * @author Akshay Sharma
	 * Feb 7, 2012
	 */
	private class VideoCodec {
		private Codec videoCodec;
		// Order is important!
		private List<Codec> associatedAudioCodecs = new ArrayList<Codec>();
		
		public VideoCodec(Codec videoCodec) {
			this.videoCodec = videoCodec;
			videoCodecsComputed.add(videoCodec);
		}
		
		public VideoCodec addAudioCodec(Codec audioCodec) {
			this.associatedAudioCodecs.add(audioCodec);
			audioCodecsComputed.add(audioCodec);
			return this;
		}

		/**
		 * @return the videoCodec
		 */
		public Codec getVideoCodec() {
			return videoCodec;
		}

		/**
		 * @return the associatedAudioCodecs
		 */
		public List<Codec> getAssociatedAudioCodecs() {
			return associatedAudioCodecs;
		}
		
		@Override public String toString() {
			return "video codec:" + this.getVideoCodec() + "and associated audio codecs :" + this.getAssociatedAudioCodecs(); 
		}
		
	}
	
	/**
	 *
	 * @author Akshay Sharma
	 * Feb 7, 2012
	 */
	private class AudioCodec {
		private Codec audioCodec;
		
		public AudioCodec(Codec audioCodec) {
			this.audioCodec = audioCodec;
			audioCodecsComputed.add(audioCodec);
		}

		@Override public String toString() {
			return "audio codec:" + this.audioCodec; 
		}
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the profileType
	 */
	public ProfileType getProfileType() {
		return profileType;
	}

	/**
	 * @return the containerFormat
	 */
	public String getContainerFormat() {
		return containerFormat;
	}

	/**
	 * @param containerFormat the containerFormat to set
	 */
	protected void setContainerFormat(String containerFormat) {
		this.containerFormat = containerFormat;
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	protected void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	protected Profile addAudioOnlyCodec(Codec codec) {
		if (this.profileType != ProfileType.AUDIO)
			throw new IllegalStateException("An audio only codec can be added only if the media is of profileType Audio. " +
				"Use adding Audio through an associated video codec if the media is of profileType video");
		
		AudioCodec audioCodec = new AudioCodec(codec);
		this.audioCodecs.add(audioCodec);
		return this;
	}
	
	protected Profile addVideoCodec(Codec codec) {
		if (this.profileType != ProfileType.VIDEO)
			throw new IllegalStateException("A video only codec can be added only if the media is of profileType Video. " +
				"Use adding Audio with addAudioOnlyCodec() for media profileType Audio.");

		VideoCodec videoCodec = new VideoCodec(codec);		
		this.videoCodecs.add(videoCodec);
		return this;
	}
	
	protected Profile addAssociatedAudioCodec(Codec videoCodec, Codec audioCodec) {
		VideoCodec vidCodec = null;
		for (VideoCodec vcodec : this.videoCodecs) {
			
			if (vcodec.getVideoCodec().equals(videoCodec)) {
				vidCodec = vcodec;
				break;
			}
		}
		
		if (vidCodec != null) {
			vidCodec.addAudioCodec(audioCodec);
		} else {
			throw new IllegalStateException("Looks like the videoCodec has not been added to the profile yet.");
			// TODO: consider adding it videoCodec chain??
		}

		return this;
	}
	
	public List<Codec> getVideoCodecs() {
		return this.videoCodecsComputed;
	}
	
	public List<Codec> getAudioCodecs() {
		return this.audioCodecsComputed;
	}
	
	public List<Codec> getAssociatedAudioCodecs(Codec videoCodec) {
		
		if (videoCodec.getCodecType() != Codec.CODEC_TYPE.VIDEO)
			throw new IllegalStateException("Only a video codec can have an associated list of Audio codecs for a profile");
		
		List<Codec> associatedCodecs = null;
		for (VideoCodec vcodec : this.videoCodecs) {
			
			if (vcodec.getVideoCodec().equals(videoCodec)) {
				associatedCodecs = vcodec.getAssociatedAudioCodecs();
				break;
			}
		}
		
		return associatedCodecs;
	}
	
	/**
	 * @return the videoBitRate
	 */
	public String getVideoBitRate() {
		return videoBitRate;
	}

	/**
	 * @param videoBitRate the videoBitRate to set
	 */
	protected void setVideoBitRate(String videoBitRate) {
		this.videoBitRate = videoBitRate;
	}

	/**
	 * @return the videoQuality
	 */
	public String getVideoQuality() {
		return videoQuality;
	}

	/**
	 * @param videoQuality the videoQuality to set
	 */
	protected void setVideoQuality(String vidoeQuality) {
		this.videoQuality = vidoeQuality;
	}

	/**
	 * @return the audioBitRate
	 */
	public String getAudioBitRate() {
		return audioBitRate;
	}

	/**
	 * @param audioBitRate the audioBitRate to set
	 */
	protected void setAudioBitRate(String audioBitRate) {
		this.audioBitRate = audioBitRate;
	}

	/**
	 * @return the aspectRatio
	 */
	public String getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * @param aspectRatio the aspectRatio to set
	 */
	protected void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	/**
	 * @return the maxWidth
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * @param maxWidth the maxWidth to set
	 */
	protected void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * @return the frameRateFPS
	 */
	public String getFrameRateFPS() {
		return frameRateFPS;
	}

	/**
	 * @param frameRateFPS the frameRateFPS to set
	 */
	protected void setFrameRateFPS(String frameRateFPS) {
		this.frameRateFPS = frameRateFPS;
	}

	/**
	 * @return the additionalParameters
	 */
	public String getAdditionalParameters() {
		return additionalParameters;
	}

	/**
	 * @param additionalParameters the additionalParameters to set
	 */
	protected void setAdditionalParameters(String optionalAdditionalParameters) {
		this.additionalParameters = optionalAdditionalParameters;
	}

	/**
	 * @return the criteria
	 */
	public List<String> getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	protected Profile addCriteria(String criterion) {
		this.criteria.add(criterion);
		return this;
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Profile))
			return false;
		
		Profile profile = (Profile) obj;
		
		if (profile == this)
			return true;

		if (profile.getId().equalsIgnoreCase(this.getId()))
			return true;
		
		return false;
	}
	
	@Override public int hashCode() {
		return this.getId().hashCode();
	}

	@Override public String toString() {
		return "\nid:" + this.getId() + "::Container:" + this.getContainerFormat() + "::ProfileType:" + this.getProfileType() 
			+ "::Video Codecs supported:" + this.videoCodecs + "\t:: audio codecs supported:" + this.audioCodecs;  
	}

	/**
	 * @return the fileFormat
	 */
	public String getFileFormat() {
		return fileFormat;
	}

	/**
	 * @param fileFormat the fileFormat to set
	 */
	protected void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	/**
	 * @return the audioSampleRate
	 */
	public String getAudioSampleRate() {
		return audioSampleRate;
	}

	/**
	 * @param audioSampleRate the audioSampleRate to set
	 */
	protected void setAudioSampleRate(String audioSampleRate) {
		this.audioSampleRate = audioSampleRate;
	}
	
}
