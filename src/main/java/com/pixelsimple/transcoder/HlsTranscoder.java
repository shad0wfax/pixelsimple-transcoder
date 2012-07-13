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
import com.pixelsimple.commons.util.UrlUtils;
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
				handle, hlsTranscodeCompleteFilePath), true);
		runner.runCommand(commandRequestHls, new CommandResponse());
		LOG.debug("transcodeIt::requested hls transcoding in async mode. Handle returned - {}", handle);
		
		// This will run with the regular, transcoder call back handler
		CommandRunner runner2 = CommandRunnerFactory.newAsyncCommandRunner(new TranscoderCallbackHandler(handle), true);
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
	// 		ffprobe path - to compute the actual segment time.
	// 		base_uri (only optional param)
	private CommandRequest buildM3u8PlaylistCommand(TranscoderOutputSpec spec, String hlsTranscodeCompleteFilePath) {
		String hlsMediaDir = OSUtils.appendFolderSeparator(spec.getOutputFileDir().getResourceAsString()); 
		String playlistFile = spec.getHlsTranscoderOutputSpec().getComputedPlaylistFileWithPath();
		String segmentFileExtension = spec.getTargetProfile().getContainerFormat();
		String playlistCheckTime = "" + spec.getHlsTranscoderOutputSpec().getPlaylistCreationCheckTimeInSec();
		String segmentTime = "" + spec.getHlsTranscoderOutputSpec().getSegmentTime();
		String ffprobePath = this.apiConfig.getFfprobeConfig().getExecutablePath();
		String baseUri = spec.getHlsTranscoderOutputSpec().getPlaylistBaseUri();
		
		if (StringUtils.isNullOrEmpty(playlistCheckTime)) playlistCheckTime = segmentTime;
		if (StringUtils.isNullOrEmpty(baseUri)) baseUri = "";
		if (StringUtils.isNullOrEmpty(segmentFileExtension)) segmentFileExtension = "ts"; //.ts is the std.
		
		// Concatenate the base uri with the media directory encoded for spaces. This will be the playlist entry's base
		// part. The script will append the segment file names and write it out to playlist file.
		String completeBaseUri = baseUri + UrlUtils.encodeSpaces(hlsMediaDir);
		
		// Need to quote since the batch script called with arguments that contain "=" character will be split.
		// Refer the MS article: http://support.microsoft.com/kb/71247
		// Windows batch script will then strip the double quotes.
		completeBaseUri = quoteParamsForWindows(completeBaseUri);
		// There is a crappy bug in commons-exec v1.1 where if a string contains space and ends with '\', on adding it 
		// as argument (addArgument) it adds an additional \ after quoting. This is causing the playlist generator script
		// to break when media directory has two trailing \. This can be fixed if we quote it ourselves. 
		// The script accepts quoted inputs as well.
		hlsMediaDir = quoteParamsForWindows(hlsMediaDir); 
		
		CommandRequest req = new CommandRequest();
		req.addCommand(this.transcoderConfig.getHlsPlaylistGeneratorPath(), 0);
		req.addArgument(hlsMediaDir).addArgument(playlistFile).addArgument(segmentFileExtension)
			.addArgument(playlistCheckTime).addArgument(hlsTranscodeCompleteFilePath).addArgument(segmentTime)
			.addArgument(ffprobePath).addArgument(completeBaseUri);
		
		return req;
	}

	private String quoteParamsForWindows(String param) {
		if (!OSUtils.isWindows())
			return param;
		
		return "\"" + param + "\"";
		
	}

	/**
	 * @param spec
	 * @return
	 */
	private String getHlsTranscodeCompleteFilePath(TranscoderOutputSpec spec) {
		return OSUtils.appendFolderSeparator(spec.getOutputFileDir().getResourceAsString()) + this.transcoderConfig.getHlsTranscodeCompleteFile();
	}


} 
