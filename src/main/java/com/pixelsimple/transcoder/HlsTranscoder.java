/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;
import com.pixelsimple.commons.media.Container;
import com.pixelsimple.commons.util.OSUtils;
import com.pixelsimple.commons.util.StringUtils;
import com.pixelsimple.transcoder.exception.TranscoderException;

/**
 *
 * @author Akshay Sharma
 * Apr 6, 2012
 */
public class HlsTranscoder extends AbstractTranscoder {
	private static final Logger LOG = LoggerFactory.getLogger(HlsTranscoder.class);
	
	public Handle transcodeIt(Container inputMedia, TranscoderOutputSpec spec) throws TranscoderException {
		Handle handle = this.buildHandle(spec);
		CommandRequest commandRequestHls = this.buildCommand(inputMedia, spec);

		String hlsTranscodeCompleteFilePath = this.getHlsTranscodeCompleteFilePath(spec);
		LOG.debug("transcodeIt::hlsTranscodeCompleteFilePath - {}", hlsTranscodeCompleteFilePath);
		
		// NOTE: CRITICAL! - This is a potential race condition. If the command to create the complete file doesn't run,
		// like for example if there are folder permission problems, then the script to create the playlist could hang!
		// TODO: A another algo, could be the playlist creation command is terminated after the completion of hls transcode 
		// command. Or maybe the playlist creation script can be smart enought to terminate itself if there are perm problems :).
		CommandRequest commandRequestM3u8Playlist = this.buildM3u8PlaylistCommand(spec, hlsTranscodeCompleteFilePath);

		CommandRunner runner = CommandRunnerFactory.newAsyncCommandRunner(new HlsTranscoderCallbackHandler(
				handle, hlsTranscodeCompleteFilePath));
		runner.runCommand(commandRequestHls, new CommandResponse());
		LOG.debug("transcodeIt::requested hls transcoding in async mode. Handle returned - {}", handle);
		
		// This will run with the regular, transcoder call back handler
		CommandRunner runner2 = CommandRunnerFactory.newAsyncCommandRunner(new TranscoderCallbackHandler(handle));
		runner2.runCommand(commandRequestM3u8Playlist, new CommandResponse());
		
		return handle;
	}

	// This will be a call to the bat/sh file that will create the playlist.
	// Building a command that would execute the playlist generator file with the following arguments (in order):
	// 		hls_media_dir
	// 		playlist_file
	// 		hls_segment_extension_name
	// 		check_interval_in_sec
	// 		hls_transcode_complete_file
	// 		segment_time
	// 		base_uri (only optional param)
	private CommandRequest buildM3u8PlaylistCommand(TranscoderOutputSpec spec, String hlsTranscodeCompleteFilePath) {
		String hldMediaDir = OSUtils.appendFolderSeparator(spec.getOutputFileDir()); 
		String playlistFile = spec.getHlsTranscoderOutputSpec().getComputedPlaylistFileWithPath();
		String segmentFileExtension = spec.getTargetProfile().getContainerFormat();
		String playlistCheckTime = "" + spec.getHlsTranscoderOutputSpec().getPlaylistCreationCheckTimeInSec();
		String segmentTime = "" + spec.getHlsTranscoderOutputSpec().getSegmentTime();
		String baseUri = spec.getHlsTranscoderOutputSpec().getPlaylistBaseUri();
		
		if (StringUtils.isNullOrEmpty(playlistCheckTime)) playlistCheckTime = segmentTime;
		if (StringUtils.isNullOrEmpty(baseUri)) baseUri = "";
		
		// Need to do it due to the batch script behavior: Refer the MS article: http://support.microsoft.com/kb/71247
		baseUri = quoteBaseUriForWindows(baseUri);
		
		CommandRequest req = new CommandRequest();
		req.addCommand(this.transcoderConfig.getHlsPlaylistGeneratorPath(), 0);
		req.addArgument(hldMediaDir).addArgument(playlistFile).addArgument(segmentFileExtension)
			.addArgument(playlistCheckTime).addArgument(hlsTranscodeCompleteFilePath).addArgument(segmentTime)
			.addArgument(baseUri);
		
		return req;
	}

	// NEed to quote since the batch script called with arguments that contain "=" character will be split.
	// Refer the MS article: http://support.microsoft.com/kb/71247
	// Our batch script will then strip the double quotes.
	private String quoteBaseUriForWindows(String baseUri) {
		if (!OSUtils.isWindows())
			return baseUri;
		
		return "\"" + baseUri + "\"";
	}

	/**
	 * @param spec
	 * @return
	 */
	private String getHlsTranscodeCompleteFilePath(TranscoderOutputSpec spec) {
		return spec.getOutputFileDir() + OSUtils.folderSeparator() + this.transcoderConfig.getHlsTranscodeCompleteFile();
	}


} 
