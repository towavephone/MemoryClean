package com.memoryclean.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class MemoryUsedMessage {
	private static long sum;
	private static long available;
	private static float percent;

	// ��ȡ�����˴��С
	public static long getAvailMemory(Context context) {
		// ��ȡandroid��ǰ�����ڴ��С
		ActivityManager am = (ActivityManager) context
			.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; ��ǰϵͳ�Ŀ����ڴ�
		// return Formatter.formatFileSize(context, mi.availMem);// ����ȡ���ڴ��С���
		available = mi.availMem / (1024 * 1024);
		return available;
	}

	// ��ȡ���˴��С
	public static long getTotalMemory() {
		String str1 = "/proc/meminfo";// ϵͳ�ڴ���Ϣ�ļ�
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// ��ȡ��һ�У�ϵͳ���ڴ��С
			arrayOfString = str2.split("\\s+");
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// ���ϵͳ���ڴ棬��λ��KB������1024ת��ΪByte
			localBufferedReader.close();
		} catch (IOException e) {
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byteת��ΪKB����MB���ڴ��С���
		sum = initial_memory / (1024 * 1024);
		return sum;
	}

	public static float getPercent(Context context) {
		if (sum == 0)
			sum = MemoryUsedMessage.getTotalMemory();
		available = MemoryUsedMessage.getAvailMemory(context);
		float tmp_double = (float) (1.0 * (sum - available) / sum * 100);
		BigDecimal bigDecimal = new BigDecimal(tmp_double);
		percent = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return percent;
	}
}
