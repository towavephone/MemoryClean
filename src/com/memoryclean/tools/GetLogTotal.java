package com.memoryclean.tools;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

import com.memoryclean.properties.LogFile;
import com.memoryclean.properties.MLog;

public class GetLogTotal {
	public static String[] getLogTotal(Context context) {
		ArrayList<MLog> list = (ArrayList<MLog>) LogFile.getInstance().query(
				context);
		String[] total = new String[2];
		float total_clean = 0, day_clean = 0;
		if (list != null)
			for (MLog ml : list) {
				total_clean += ml.getMemory();
				Date dateA = new Date();
				if (TimeUtils.areSameDay(MLog.DateToDate(dateA), ml.getTime())) {
					day_clean += ml.getMemory();
				}
			}
		total[0] = String.format("%.2f", day_clean);
		total[1] = String.format("%.2f", total_clean);
		return total;
	}
}
