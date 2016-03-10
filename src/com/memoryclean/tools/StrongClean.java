package com.memoryclean.tools;

import java.util.ArrayList;

import android.content.Context;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;

public class StrongClean extends Clean {
	private CommandUtil util;

	public StrongClean() {
		util = new CommandUtil();
	}

	@Override
	public void killProgram(MPrograme mp) {
		String command = "am force-stop " + mp.getPkgname() + "\n";
		util.executeCommand(command);
	}

	@Override
	public void killPrograms(ArrayList<MPrograme> programs) {
		ArrayList<String> commands = new ArrayList<String>();
		for (MPrograme mp : programs) {
			commands.add("am force-stop " + mp.getPkgname() + "\n");
		}
		util.executeCommand(commands);
	}

	@Override
	public void killProcess(MProcess m, Context context) {
		String command = "kill -9 " + m.getPid() + "\n";
		util.executeCommand(command);
		
	}

}
