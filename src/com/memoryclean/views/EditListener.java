package com.memoryclean.views;

import java.util.ArrayList;
import java.util.Map;

import android.view.View;
import android.widget.AdapterView;

public class EditListener extends listViewListener {
	private ArrayList<Map<String, Object>> infoMapArr;
	private ArrayList<String> checked_items;
	private ViewHolder vh;

	public EditListener(ViewHolder vh) {
		super();
		this.vh = vh;
		this.infoMapArr = vh.getInfoMapArr();
		this.checked_items = vh.getChecked_items();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		chose(position);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		chose(position);
		return true;
	}

	private void chose(int arg2) {
		Map<String, Object> map = infoMapArr.get(arg2 - 1);
		boolean ischecked = (Boolean) map.get("application_ischecked");
		String name = map.get("application_name").toString();
		map.put("application_ischecked", !ischecked);
		if (ischecked) {
			checked_items.remove(name);
		} else {
			checked_items.add(name);
		}
		int size = checked_items.size();
		vh.setChosedSize(size);
		vh.setCheckBoxChecked(size);
		vh.getAdapter().notifyDataSetChanged();
		name = null;
		map = null;
	}
}
