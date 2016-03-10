package com.memoryclean.adpter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.classes.MService;
import com.wust.memoryclean.R;

/**
 * 自定义ExpandableListView需要自定义Adapter ------控制其中各控件的显示
 * 
 * @author tianwailaike
 *
 */
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<MProcess> processdata;
	private MPrograme programe;
	private Map<String, List<MService>> servicedata;

	// private Bitmap up, down;

	private class ProcessViewHolder {
		public TextView name;
		public TextView pid;
		public TextView servicecount;
		private LinearLayout ll;

		@SuppressLint("InflateParams")
		public ProcessViewHolder(Context context) {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.process_item, null);
			name = (TextView) ll.findViewById(R.id.process_name);
			pid = (TextView) ll.findViewById(R.id.process_PID);
			servicecount = (TextView) ll
					.findViewById(R.id.process_servicecount);
		}

		public View getView() {
			return ll;
		}
	}

	private class ServiceViewHolder {
		public TextView name;
		private LinearLayout ll;

		@SuppressLint("InflateParams")
		public ServiceViewHolder(Context context) {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.service_item, null);
			name = (TextView) ll.findViewById(R.id.servicename);
		}

		public View getView() {
			return ll;
		}
	}

	public ExpandableListViewAdapter(Context context, ArrayList<MProcess> data,MPrograme programe) {
		this.context = context;
		this.processdata = data;
		servicedata = new HashMap<String, List<MService>>();
		this.programe=programe;
		// Resources res = context.getResources();
		// Bitmap d = BitmapFactory.decodeResource(res, R.drawable.up);
		// int size = 2 * Screen.screenHeight / Screen.screenWidth;
		// int w = d.getWidth() / size;
		// int h = d.getHeight() / size;
		// up = ImageProcessing.zoomBitmap(
		// BitmapFactory.decodeResource(res, R.drawable.up), w, h);
		// down = ImageProcessing.zoomBitmap(
		// BitmapFactory.decodeResource(res, R.drawable.down), w, h);
	}

	@Override
	public int getGroupCount() {
		return processdata.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<MService> ms = servicedata.get(processdata.get(groupPosition)
				.getName());
		if (ms == null || ms.size() == 0)
			return 0;
		return ms.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return processdata.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return servicedata.get(processdata.get(groupPosition).getName());
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ProcessViewHolder holder = null;
		if (convertView == null) {
			holder = new ProcessViewHolder(context);
			MProcess m = processdata.get(groupPosition);
			holder.name.setText("进程名:" + m.getName());
			holder.pid.setText("PID:" + m.getPid());
			List<MService> ms = m.getServices(programe.getPkgname());
			holder.servicecount.setText("服务数:" + ms.size() + "个");
			convertView = holder.getView();
			convertView.setTag(holder);
			servicedata.put(m.getName(), ms);
		} else {
			holder = (ProcessViewHolder) convertView.getTag();
		}
		// if (isExpanded)
		// iv.setImageBitmap(up);
		// else
		// iv.setImageBitmap(down);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ServiceViewHolder holder = null;
		if (convertView == null) {
			holder = new ServiceViewHolder(context);
			MService m = servicedata.get(
					processdata.get(groupPosition).getName())
					.get(childPosition);
			holder.name.setText("服务名:" + m.getName());
			convertView = holder.getView();
			convertView.setTag(holder);
		} else
			holder = (ServiceViewHolder) convertView.getTag();
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
