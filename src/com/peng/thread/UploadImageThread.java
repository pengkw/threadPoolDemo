package com.peng.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class UploadImageThread implements Runnable {

	private Object result;

	// 活动数
	private AtomicInteger activeCount;

	// 出错数
	private AtomicInteger errorCount;

	// 关闭标志位
	private boolean shutdown = false;

	public UploadImageThread(final AtomicInteger activeCount,
			final AtomicInteger errorCount) {
		this.activeCount = activeCount;
		this.errorCount = errorCount;
	}

	public void shutdownWork() {
		this.shutdown = true;
	}

	@Override
	public void run() {
		try {
			if (this.shutdown)
				throw new Exception(
						"Not executed in thread pool, caused by time out.");
			result = __doUpload();
			if (this.shutdown)
				throw new Exception(
						"Abandoned returned result in thread pool, caused by time out.");
		} catch (Exception e) {
			errorCount.incrementAndGet();
			System.out.println(e.getMessage());;
		} finally {
			activeCount.decrementAndGet();
		}
	}

	public  Object __doUpload()  {
		System.out.println("I am working !");
		return result;
	}
}
