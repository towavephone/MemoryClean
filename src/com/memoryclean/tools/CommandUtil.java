package com.memoryclean.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

/**
 * 防止waitFor的阻塞 ---阻塞原因是因为输入流缓冲区满了而阻塞了该线程
 * 
 * @author tianwailaike
 *
 */
public class CommandUtil {
	// 保存进程的输入流信息
	private List<String> stdoutList = new ArrayList<String>();
	private List<String> errorList = new ArrayList<String>();
	private boolean stdoutEnd = false;
	private boolean errorEnd = false;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				stdoutEnd = true;
			} else {
				errorEnd = true;
			}
		};
	};

	public List<String> getStdoutList() {
		while (!stdoutEnd) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return stdoutList;
	}

	public List<String> getErrorList() {
		while (!errorEnd) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return errorList;
	}

	public void executeCommand(String command) {
		// 先清空
		stdoutList.clear();
		errorList.clear();
		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes(command);
			out.writeBytes("exit\n");
			out.flush();
			// 创建线程，分别读取输入流缓冲区
			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			// 启动线程读取缓冲区数据
			stdoutUtil.start();
			// // 创建线程，分别读取错误信息流缓冲区
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // 启动线程读取缓冲区数据
			// errorUtil.start();

			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executeCommand(ArrayList<String> commands) {
		// 先清空
		stdoutList.clear();
		errorList.clear();
		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(p.getOutputStream());

			for (String command : commands) {
				out.writeBytes(command);
				out.flush();
			}
			out.writeBytes("exit\n");
			out.flush();

			// 创建线程，分别读取输入流缓冲区
			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			// 启动线程读取缓冲区数据
			stdoutUtil.start();

			// 创建线程，分别读取错误信息流缓冲区
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // 启动线程读取缓冲区数据
			// errorUtil.start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getRoot(String command) throws Exception {
		stdoutList.clear();
		errorList.clear();
		try {
			Process p = Runtime.getRuntime().exec("su");
			// 执行到这，Superuser会跳出来，选择是否允许获取最高权限
			DataOutputStream out = new DataOutputStream(p.getOutputStream());

			String temp = command + "\n";
			// 加回车
			out.writeBytes(temp);
			// 执行
			out.flush();
			// 刷新，确保都发送到outputstream
			out.writeBytes("exit\n");
			// 退出
			out.flush();

			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			stdoutUtil.start();
			// 创建线程，分别读取错误信息流缓冲区
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // 启动线程读取缓冲区数据
			// errorUtil.start();

			p.waitFor();
			// return DIPS;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ThreadUtil implements Runnable {
	// 设置读取的字符编码
	private String character = "GB2312";
	private List<String> list;
	private InputStream inputStream;
	private Handler handler;
	private int flag;

	public ThreadUtil(InputStream inputStream, List<String> list,
			Handler handler, int flag) {
		this.inputStream = inputStream;
		this.list = list;
		this.handler = handler;
		this.flag = flag;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);// 将其设置为守护线程
		thread.start();
	}

	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream,
					character));
			String line = null;
			if (br != null)
				while ((line = br.readLine()) != null) {
					if (line != null) {
						list.add(line);
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Message msg = Message.obtain();
			msg.what = flag;
			handler.handleMessage(msg);
			try {
				inputStream.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}