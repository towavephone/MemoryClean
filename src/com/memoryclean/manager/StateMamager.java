package com.memoryclean.manager;

import java.util.Map;

import android.content.Context;

import com.memoryclean.adpter.ListViewAdapter;
import com.memoryclean.views.EditListener;
import com.memoryclean.views.NormalListener;
import com.memoryclean.views.ViewHolder;

/**
 * ״̬���� ����RunningAppsFragment�ļ���״̬�仯
 * 
 * @author tianwailaike
 *
 */
public class StateMamager {
	private ViewHolder vh;
	private ListViewAdapter adapter;

	private Context context;

	private int state = EDIT_NORMAL;
	public static final int EDIT_NORMAL = 1;// �༭״̬�ص�����״̬
	public static final int NORMAL_EDIT = 2;// ����״̬����༭״̬
	public static final int REFRESH_NORMAL = 3;// ˢ��״̬�ص�����״̬
	public static final int NORMAL_REFRESH = 4;// ����״̬����ˢ��״̬

	public StateMamager(ViewHolder vh, Context context) {
		this.vh = vh;
		adapter = vh.getAdapter();
		this.context = context;
	}

	/**
	 * ���д�����״̬�仯
	 */
	private void handleState() {
		switch (state) {
		case EDIT_NORMAL:// 1
			vh.showButtons(true);
			vh.setNormalButtonsState(true);
			vh.setCheckBoxState(false);
			vh.showtextview2(false);
			vh.setlistview(true, true, true);
			vh.setListViewListener(new NormalListener(context, this));
			adapter.setVisibleCheckBox(false);
			adapter.notifyDataSetChanged();
			break;
		case REFRESH_NORMAL:// 3
			vh.setListViewListener(new NormalListener(context, this));
			vh.setNormalButtonsState(true);
			vh.setlistview();
			break;
		case NORMAL_EDIT:// 2
			vh.showtextview2(true);
			vh.setCheckBoxState(true);
			vh.setButtontext(0);
			vh.showButtons(false);
			vh.setEditButtonsState(3, false);
			vh.setlistview(false, false);
			for (Map<String, Object> map : vh.getInfoMapArr()) {
				map.put("application_ischecked", false);
			}
			vh.setListViewListener(new EditListener(vh));
			adapter.setVisibleCheckBox(true);
			adapter.notifyDataSetChanged();
			break;
		case NORMAL_REFRESH:// 4
			vh.setListViewListener(null);
			vh.setNormalButtonsState(false);
			vh.setlistview(false, false, true);
			break;

		}
	}

	/**
	 * ����״̬������
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
		handleState();
	}

	public int getState() {
		return state;
	}

	public ViewHolder getVh() {
		return vh;
	}
}