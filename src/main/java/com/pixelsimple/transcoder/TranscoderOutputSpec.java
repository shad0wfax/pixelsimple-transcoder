/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import com.pixelsimple.appcore.Resource;
import com.pixelsimple.appcore.Resource.RESOURCE_TYPE;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.commons.util.UrlUtils;
import com.pixelsimple.transcoder.exception.TranscoderException;
import com.pixelsimple.transcoder.profile.Profile;

/**
 *
 * @author Akshay Sharma
 * Feb 5, 2012
 */
public class TranscoderOutputSpec {
	private Profile targetProfile;
	
	// The spec says that we have to write the output file to a file system (type DIRECTORY). 
	// Writing to FTP/WebDAV etc should be supported as a support job using com.pixelsimple.commons.command.callback.AsyncCallbackHandler
	private Resource outputFileDir;
	// Extension automatically gets decided.
	private String outputFileNameWithoutExtension;
	private String computedOutputFile;
	// TODO: do we need eagar load when most transcodes are non-hls? Waste of space.
	private HlsTranscoderOutputSpec hlsTranscoderOutputSpec = new HlsTranscoderOutputSpec();
	
	public TranscoderOutputSpec(Profile targetProfile, Resource outputFileDir, String outputFileNameWithoutExtension) {
		if (outputFileDir.getResourceType() != RESOURCE_TYPE.DIRECTORY)
			throw new IllegalArgumentException("The transcoding output can be placed only in a file directory");
		
		this.targetProfile = targetProfile;
		this.outputFileDir = outputFileDir;
		this.outputFileNameWithoutExtension = outputFileNameWithoutExtension;
	}
	
	public void validate() throws TranscoderException {
		if (targetProfile == null)
				throw new TranscoderException("The target profile cannot be null");

		if (outputFileDir == null)
			throw new TranscoderException("The outputFileDir cannot be null");
	
		if (outputFileNameWithoutExtension == null)
			throw new TranscoderException("The outputFileNameWithoutExtension cannot be null");
	}
	
	public TranscoderOutputSpec addHlsSegmentTime(int segmentTime) {
		hlsTranscoderOutputSpec.segmentTime = segmentTime;
		return this;
	}
	
	/**
	 * Override from the default mpegts to whatever needed. Note: mpegts is what is supported by ffmpeg/hsl spec. 
	 * @param segmentFormat
	 * @return
	 */
	public TranscoderOutputSpec overrideHlsSegmentFormat(String segmentFormat) {
		hlsTranscoderOutputSpec.segmentFormat = segmentFormat;
		return this;
	}
	
	/**
	 * Override from the default profile's fileformat (usually ts) to whatever needed. Note: ts is what is supported by ffmpeg/hsl spec. 
	 * @param segmentFileWithoutExtension
	 * @return
	 */
	public TranscoderOutputSpec overrideHlsSegmentFileWithoutExtension(String segmentFileWithoutExtension) {
		hlsTranscoderOutputSpec.segmentFileWithoutExtension = segmentFileWithoutExtension;
		return this;
	}
	
	/**
	 * Will encode any spaces to ensure it is a valid uri
	 * @param playlistBaseUri
	 * @return
	 */
	public TranscoderOutputSpec addHlsPlaylistBaseUri(String playlistBaseUri) {
		hlsTranscoderOutputSpec.playlistBaseUri = UrlUtils.encodeSpaces(playlistBaseUri);
		return this;
	}
	
	public TranscoderOutputSpec addHlsPlaylistCreationCheckTimeInSec(int playlistCreationCheckTimeInSec) {
		hlsTranscoderOutputSpec.playlistCreationCheckTimeInSec = playlistCreationCheckTimeInSec;
		return this;
	}
	
	/**
	 * @return the targetProfile
	 */
	public Profile getTargetProfile() {
		return this.targetProfile;
	}
	
	/**
	 * @return the outputFileNameWithPath
	 */
	public Resource getOutputFileDir() {
		return this.outputFileDir;
	}

	/**
	 * @return the outputFileNameWithoutExtension
	 */
	public String getOutputFileNameWithoutExtension() {
		return outputFileNameWithoutExtension;
	}

	public String getComputedOutputFileWithPath() {
		
		if (StringUtils.isNullOrEmpty(computedOutputFile)) {
			String outputFileNameWithExtension = this.getOutputFileNameWithoutExtension() + "." + this.targetProfile.getFileExtension();
			// Append to the directory (after ensuring it is correctly ended with / or \)
			this.computedOutputFile = OSUtils.appendFolderSeparator(this.getOutputFileDir().getResourceAsString()) + outputFileNameWithExtension;
		}
		return this.computedOutputFile;
	}

	public class HlsTranscoderOutputSpec {
		// TODO: Leaving it on now so that script can be extended in the future such that it need not detect the time.
		private int segmentTime;
		// Default is mpegts
		private String segmentFormat = "mpegts";
		// Default will be the profile's file format (ts). Can be override if needed
		// Ex: mymovie (will be later converted to mymovie001.ts, ...)
		private String segmentFileWithoutExtension; 
		// What needs to be prefixed to the playlist
		private String playlistBaseUri;
		private int playlistCreationCheckTimeInSec;

		public int getSegmentTime() {
			return segmentTime;
		}
		public String getComputedPlaylistFileWithPath() {
			return getComputedOutputFileWithPath();
		}
		public String getSegmentFormat() {
			return segmentFormat;
		}
		public String getSegmentFileWithoutExtension() {
			if (this.segmentFileWithoutExtension == null) {
				this.segmentFileWithoutExtension = targetProfile.getFileFormat();
			}
			return segmentFileWithoutExtension;
		}
		
		/**
		 * Will return a uri that is space encoded. This is to ensure it is valid uri when copied over to the m3u8 file.
		 * @return
		 */
		public String getPlaylistBaseUri() {
			return playlistBaseUri;
		}
		public int getPlaylistCreationCheckTimeInSec() {
			return playlistCreationCheckTimeInSec;
		}
	}

	/**
	 * @return the hlsTranscoderOutputSpec
	 */
	public HlsTranscoderOutputSpec getHlsTranscoderOutputSpec() {
		return this.hlsTranscoderOutputSpec;
	}

}
