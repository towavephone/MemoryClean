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
 * ��ֹwaitFor������ ---����ԭ������Ϊ���������������˶������˸��߳�
 * 
 * @author tianwailaike
 *
 */
public class CommandUtil {
	// ������̵���������Ϣ
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
		// �����
		stdoutList.clear();
		errorList.clear();
		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes(command);
			out.writeBytes("exit\n");
			out.flush();
			// �����̣߳��ֱ��ȡ������������
			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			// �����̶߳�ȡ����������
			stdoutUtil.start();
			// // �����̣߳��ֱ��ȡ������Ϣ��������
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // �����̶߳�ȡ����������
			// errorUtil.start();

			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executeCommand(ArrayList<String> commands) {
		// �����
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

			// �����̣߳��ֱ��ȡ������������
			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			// �����̶߳�ȡ����������
			stdoutUtil.start();

			// �����̣߳��ֱ��ȡ������Ϣ��������
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // �����̶߳�ȡ����������
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
			// ִ�е��⣬Superuser����������ѡ���Ƿ������ȡ���Ȩ��
			DataOutputStream out = new DataOutputStream(p.getOutputStream());

			String temp = command + "\n";
			// �ӻس�
			out.writeBytes(temp);
			// ִ��
			out.flush();
			// ˢ�£�ȷ�������͵�outputstream
			out.writeBytes("exit\n");
			// �˳�
			out.flush();

			ThreadUtil stdoutUtil = new ThreadUtil(p.getInputStream(),
					stdoutList, handler, 0);
			stdoutUtil.start();
			// �����̣߳��ֱ��ȡ������Ϣ��������
			// ThreadUtil errorUtil = new ThreadUtil(p.getInputStream(),
			// errorList, handler, 1);
			// // �����̶߳�ȡ����������
			// errorUtil.start();

			p.waitFor();
			// return DIPS;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ThreadUtil implements Runnable {
	// ���ö�ȡ���ַ�����
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
		thread.setDaemon(true);// ��������Ϊ�ػ��߳�
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