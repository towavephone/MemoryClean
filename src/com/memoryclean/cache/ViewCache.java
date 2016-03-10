package com.memoryclean.cache;

import com.wust.memoryclean.R;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {
	private ImageView application_icon;
	private ImageView hint_checked;
	private TextView application_name;
	private TextView application_memory;
	private CheckBox application_ischecked;
	private TextView clean_time;
	private CheckBox application_islocked;
	
	private View convertView;
	
	public ViewCache(View convertView){
		this.convertView=convertView;
	}

	public ImageView getApplication_icon() {
		if(application_icon==null)
			application_icon = (ImageView) convertView
			.findViewById(R.id.application_icon);
		return application_icon;
	}

	public ImageView getHint_checked() {
		if(hint_checked==null)
			hint_checked = (ImageView) convertView
					.findViewById(R.id.hint_checked);
		return hint_checked;
	}

	public TextView getApplication_name() {
		if(application_name==null)
		application_name = (TextView) convertView
				.findViewById(R.id.application_name);
		return application_name;
	}

	public TextView getApplication_memory() {
		if(application_memory==null)
			application_memory = (TextView) convertView
			.findViewById(R.id.application_memory);
		return application_memory;
	}

	public CheckBox getApplication_ischecked() {
		if(application_ischecked==null)
			application_ischecked = (CheckBox) convertView
			.findViewById(R.id.application_ischecked);
		return application_ischecked;
	}

	public TextView getClean_time() {
		if(clean_time==null)
			clean_time = (TextView) convertView
			.findViewById(R.id.clean_time);
		return clean_time;
	}

	public CheckBox getApplication_islocked() {
		if(application_islocked==null)
			application_islocked = (CheckBox) convertView
			.findViewById(R.id.application_islocked);
		return application_islocked;
	}
}
