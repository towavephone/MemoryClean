package com.memoryclean.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池 ----防止过多线程运行影响运行速度
 * 
 * @author tianwailaike
 *
 */
public class ThreadPool {
	private ExecutorService executorService;
	private static ThreadPool tp = null;

	private ThreadPool() {
		executorService = Executors.newFixedThreadPool(5);// 固定线程数线程池
	}

	public static ThreadPool getInstance() {
		if (tp == null)
			tp = new ThreadPool();
		return tp;
	}

	/**
	 * 添加线程
	 * 
	 * @param t
	 */
	public void AddThread(Thread t) {
		executorService.execute(t);
	}

	public void AddThread(Runnable r) {
		executorService.execute(r);
	}
	/**
	 * 关闭线程池
	 * */
	public void shutsown() {
		executorService.shutdown();
		tp = null;
	}
}
