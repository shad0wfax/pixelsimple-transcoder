/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.queue;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pixelsimple.appcore.ApiConfig;
import com.pixelsimple.appcore.queue.QueueService;
import com.pixelsimple.appcore.registry.RegistryService;
import com.pixelsimple.commons.test.appcore.init.TestAppInitializer;
import com.pixelsimple.transcoder.Handle;
import com.pixelsimple.transcoder.TranscodeStatus;
import com.pixelsimple.transcoder.init.TranscoderInitializer;

/**
 *
 * @author Akshay Sharma
 * Feb 20, 2012
 */
public class TranscoderQueueTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestAppInitializer.bootStrapRegistryForTesting();
		ApiConfig apiConfig = RegistryService.getRegisteredApiConfig();
		TranscoderInitializer initializer = new TranscoderInitializer();
		try {
			initializer.initialize(apiConfig);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.pixelsimple.transcoder.queue.TranscoderQueue#enqueue(com.pixelsimple.transcoder.Handle, com.pixelsimple.transcoder.TranscodeStatus)}.
	 */
	@Test
	public void enqueueDequeueAndPeek() {
		Handle handle1 = new Handle("test1");
		Handle handle2 = new Handle("test2");
		TranscodeStatus status1 = new TranscodeStatus(null, null);
		TranscodeStatus status2 = new TranscodeStatus(null, null);
		
		QueueService.getQueue().enqueue(handle1, status1);
		QueueService.getQueue().enqueue(handle2, status2);
		
		Assert.assertEquals(status1, QueueService.getQueue().peek(handle1));
		Assert.assertEquals(status2, QueueService.getQueue().peek(handle2));
		
		TranscodeStatus statusRes = QueueService.getQueue().dequeue(handle1);
		Assert.assertNull(QueueService.getQueue().peek(handle1));
		Assert.assertNotNull(statusRes);
	}

}
