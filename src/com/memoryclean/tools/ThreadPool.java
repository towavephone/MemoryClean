package com.memoryclean.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * �̳߳� ----��ֹ�����߳�����Ӱ�������ٶ�
 * 
 * @author tianwailaike
 *
 */
public class ThreadPool {
	private ExecutorService executorService;
	private static ThreadPool tp = null;

	private ThreadPool() {
		executorService = Executors.newFixedThreadPool(5);// �̶��߳����̳߳�
	}

	public static ThreadPool getInstance() {
		if (tp == null)
			tp = new ThreadPool();
		return tp;
	}

	/**
	 * ����߳�
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
	 * �ر��̳߳�
	 * */
	public void shutsown() {
		executorService.shutdown();
		tp = null;
	}
}
