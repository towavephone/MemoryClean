package com.memoryclean.views;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.memoryclean.activity.Application_Detail;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.manager.StateMamager;

public class NormalListener extends listViewListener {
	private ArrayList<Map<String, Object>> infoMapArr;
	private ViewHolder vh;
	private Context context;
	private StateMamager sm;

	public NormalListener(Context context, StateMamager sm) {
		this.vh = sm.getVh();
		this.infoMapArr = this.vh.getInfoMapArr();
		this.context = context;
		this.sm = sm;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String, Object> map = infoMapArr.get(position - 1);
		ProgramManager p = ProgramManager.getInstance();
		MPrograme mp = p.getMProgramByName(map.get("application_name") + "");
		if (p.isRunning(mp.getPkgname())) {
			Intent intent = new Intent(context, Application_Detail.class);
			intent.putExtra("application_icon",
					(Bitmap) map.get("application_icon"));
			intent.putExtra("application_name", map.get("application_name")
					+ "");
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "该应用已被清理,请点击刷新按钮或者下拉,更新列表!!",
					Toast.LENGTH_LONG).show();
			infoMapArr.remove(map);
			vh.setText();
			vh.getAdapter().notifyDataSetChanged();
		}
		p = null;
		mp = null;
		map = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		sm.setState(StateMamager.NORMAL_EDIT);
		return false;
	}
}
