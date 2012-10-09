package com.peng.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class MainThread {
		
	private static final long EXECUTE_TIMEOUT = 5 * 60 * 1000;
	
	private static ThreadPoolExecutor threadPool = null;
	
	private static int threadSize = 5;
	
	public static void init(){
		threadPool = new ThreadPoolExecutor(100, 100 * 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue());
	}
	
	public static void doWork() throws Exception {
		long start = System.currentTimeMillis();
		final AtomicInteger active = new AtomicInteger(threadSize);
		final AtomicInteger errorCount = new AtomicInteger(0);
	
		UploadImageThread[] threads = new UploadImageThread[threadSize];
		try{
			for(int i = 0; i < threadSize ; i++){
				threads[i] = new UploadImageThread(active, errorCount);
				threadPool.execute(threads[i]);			   
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}	
		
		long timeout = EXECUTE_TIMEOUT;
		try{
			while(active.get() > 0){
				long end = System.currentTimeMillis();
				if(end - start >= timeout)
				{
					errorCount.incrementAndGet();
					for(UploadImageThread thread : threads)
						thread.shutdownWork();
					System.out.println("exec timeout " + (end - start));
					break;
				}
				Thread.sleep(10);
			}
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
		
		long end = System.currentTimeMillis();		  
		System.out.println("total execute time " + (end - start));
		
		for(int i = 0; i < threads.length; i++){
			threads[i] = null;			
		}
		
		if(errorCount.get() > 0){
			throw new Exception("error!!!");
		}
	}
	
	public static void close(){
		if(threadPool != null){
			try{
				threadPool.shutdown();				
			}catch(SecurityException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			//线程池的启动和关闭建议只调用一次，执行一次调用一次开销很大
			init();
			doWork();
			close();
		} catch (Exception e) {
			System.out.println("exec failed ");
		}
	}
}
