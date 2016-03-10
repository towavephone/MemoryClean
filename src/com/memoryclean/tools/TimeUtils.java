package com.memoryclean.tools;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static boolean areSameDay(Date dateA, Date dateB) {
	Calendar calDateA = Calendar.getInstance();
	calDateA.setTime(dateA);

	Calendar calDateB = Calendar.getInstance();
	calDateB.setTime(dateB);

	return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
		&& calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
		&& calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
			.get(Calendar.DAY_OF_MONTH);
    }
}
