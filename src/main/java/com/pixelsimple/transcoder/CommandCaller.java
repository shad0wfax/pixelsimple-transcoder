/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelsimple.commons.command.CommandRequest;
import com.pixelsimple.commons.command.CommandResponse;
import com.pixelsimple.commons.command.CommandRunner;
import com.pixelsimple.commons.command.CommandRunnerFactory;

/**
 *
 *
 * @author Akshay Sharma
 * @Nov 11, 2011
 */
public class CommandCaller {
	static final Logger LOG = LoggerFactory.getLogger(CommandCaller.class);
	
	public static void main(String [] args) {
		long millis = System.currentTimeMillis();
		String outputFileName1 = "HTTYD_1-021_transcoded_flv_" + millis + ".flv";
		String outputFileName2 = "HTTYD_1-021_transcoded_flv_" + millis + 111 + ".flv"; 

		Thread caller1 = new Thread(new ThreadedCaller(outputFileName1, false));
		Thread caller2 = new Thread(new ThreadedCaller(outputFileName2, true));
		
//		caller1.start();
		caller2.start();
	}
	
	public static class ThreadedCaller implements Runnable {
		private Boolean async;
		private String outputFileName;
		
		public ThreadedCaller(String outputFileName, Boolean async) {
			this.outputFileName = outputFileName;
			this.async = async;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			CommandRunner runner = null;
			
			if (async) {
				runner = CommandRunnerFactory.newAsyncCommandRunner(null);	
			} else {
				runner = CommandRunnerFactory.newBlockingCommandRunner();	
			}
			String command = "Z:/VmShare/Win7x64/Technology/ffmpeg/release_0.8_love/ffmpeg-git-78accb8-win32-static/bin/ffmpeg -y -i C:/Data/video_test/HTTYD_1-021_poor.mov -ar 22050 -ac 2 -vcodec flv C:/Data/video_test/transcoded/" + outputFileName;
			Object [] params = {runner.getClass(), new Date(), outputFileName};
			LOG.debug("CommandCaller::Calling command using {}, \t at time = {} + \t file name = {}", params);
			
			CommandRequest request = new CommandRequest();
			request.addCommand(command, 0);
			CommandResponse response = new CommandResponse();
			runner.runCommand(request, response);

			LOG.debug("CommandCaller::Called command. Response::\n {}", response);
		}
		
	}

}
