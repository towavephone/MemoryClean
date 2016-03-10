package com.memoryclean.tools;

import java.util.ArrayList;

import android.content.Context;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;

public class NormalClean extends Clean {
	@Override
	public void killProgram(MPrograme mp) {
		mp.kill();
	}

	@Override
	public void killPrograms(ArrayList<MPrograme> programs) {
		for (MPrograme mp : programs)
			mp.kill();
	}

	@Override
	public void killProcess(MProcess m, Context context) {
		//m.kill(context);
	}

}
