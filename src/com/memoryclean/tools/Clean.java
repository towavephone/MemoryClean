package com.memoryclean.tools;

import java.util.ArrayList;

import android.content.Context;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;

public abstract class Clean {
	public abstract void killProgram(MPrograme mp);

	public abstract void killPrograms(ArrayList<MPrograme> programs);
	
	public abstract void killProcess(MProcess m,Context context);
}
