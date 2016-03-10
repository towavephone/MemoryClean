package com.memoryclean.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import com.memoryclean.activity.Application_Detail;
import com.memoryclean.adpter.ListViewAdapter;
import com.memoryclean.views.XListView.IXListViewListener;
import com.wust.memoryclean.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 包装内存清理那一页的控件 便于状态处理
 * 
 * @author tianwailaike
 *
 */
public class ViewHolder implements OnClickListener {
	private TextView textView1, textView2;
	private CheckBox checkBox;
	private XListView listview;
	private Button[] buttons;
	private View view;
	private LinearLayout linearLayout1, linearLayout2;// 两套按钮

	private Context context;

	private ListViewAdapter adapter;

	private ArrayList<Map<String, Object>> infoMapArr;// listView显示内容
	private ArrayList<String> checked_items;// 选中列表

	private final int[] ids = new int[] { R.id.btn_refresh, R.id.btn_whitelist, R.id.btn_edit, R.id.btn_clean,
			R.id.btn_ignore, R.id.btn_more, R.id.btn_finish };// 按钮的id

	private final int[] ids_background = new int[] { R.drawable.purplebuttonstyle, R.drawable.bluebuttonstyle,
			R.drawable.greenbuttonstyle, R.drawable.bluebuttonstyle, R.drawable.greenbuttonstyle,
			R.drawable.bluebuttonstyle, R.drawable.purplebuttonstyle };// Button样式

	public ViewHolder(Context context, View view, ArrayList<Map<String, Object>> infoMapArr) {
		this.context = context;
		this.infoMapArr = infoMapArr;
		this.view = view;
		checked_items = new ArrayList<String>();
		initView();
	}

	private void initView() {
		linearLayout1 = (LinearLayout) view.findViewById(R.id.linearlayout1);
		linearLayout2 = (LinearLayout) view.findViewById(R.id.linearlayout2);
		listview = (XListView) view.findViewById(R.id.application_listview);
		checkBox = (CheckBox) view.findViewById(R.id.is_checked_all);
		textView1 = (TextView) view.findViewById(R.id.hint_running_app);
		textView2 = (TextView) view.findViewById(R.id.hint_checked_all);
		buttons = new Button[ids.length];
		for (int i = 0; i < ids.length; i++) {
			buttons[i] = (Button) view.findViewById(ids[i]);
		}
		adapter = new ListViewAdapter(context, infoMapArr, listview);
		checkBox.setOnClickListener(this);
	}

	public void setText() {
		textView1.setText("正在运行的程序:" + infoMapArr.size() + "个");
	}

	public void showtextview2(boolean flag) {
		textView2.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	public void setCheckBoxState(boolean flag) {
		checkBox.setChecked(false);
		checkBox.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}

	public void setCheckBoxChecked(int k) {
		checkBox.setChecked(k == infoMapArr.size());
	}

	@Override
	public void onClick(View v) {
		boolean isChecked = checkBox.isChecked();
		checked_items.clear();
		for (Map<String, Object> map : infoMapArr) {
			map.put("application_ischecked", isChecked);
			if (isChecked)
				checked_items.add(map.get("application_name").toString());
		}
		int size = checked_items.size();
		setChosedSize(size);
		adapter.notifyDataSetChanged();

	}

	/* 设置listView显示内容 */
	public void setData(ArrayList<Map<String, Object>> infoMapArr) {
		/* 根据内存大小排序 */
		Collections.sort(infoMapArr, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
				String str0 = arg0.get("application_memory").toString();
				String str1 = arg1.get("application_memory").toString();
				BigDecimal tmp0 = new BigDecimal(Double.parseDouble(str0.substring(0, str0.length() - 2)));
				BigDecimal tmp1 = new BigDecimal(Double.parseDouble(str1.substring(0, str1.length() - 2)));
				int flag = tmp1.compareTo(tmp0);
				str0 = null;
				str1 = null;
				tmp0 = null;
				tmp1 = null;
				return flag;
			}
		});
		setText();
		startAnimation();
		stoprefresh();
		adapter.notifyDataSetChanged();
	}

	public void setAdapter() {
		setlistview(true, true);
		adapter.setVisibleTextViewMemory(true);
		listview.setAdapter(adapter);
		listview.setTyzRefreshed();
		listview.setLayoutAnimation(AnimationTranslate.translate_item(context));
	}

	public ListViewAdapter getAdapter() {
		return adapter;
	}

	public void performItemClick(int position, long id) {
		listview.performItemClick(view, position, id);
	}

	public ArrayList<String> getChecked_items() {
		return checked_items;
	}

	public ArrayList<Map<String, Object>> getInfoMapArr() {
		return infoMapArr;
	}

	public void setListViewListener(listViewListener ll) {
		listview.setOnItemClickListener(ll);
		listview.setOnItemLongClickListener(ll);
	}

	public void stoprefresh() {
		listview.stopRefresh();
	}

	/**
	 * listView刷新
	 */
	public void refresh() {
		listview.setTyzRefreshed();
	}

	public Date getRefreshTime() {
		return listview.getRefreshTime();
	}

	public void setListViewRefresh(IXListViewListener l) {
		listview.setXListViewListener(l);
	}

	/**
	 * 动画效果
	 */
	public void startAnimation() {
		listview.setLayoutAnimation(AnimationTranslate.translate_item(context));
		listview.startLayoutAnimation();
	}

	/**
	 * 设置listview是否可点击 可下拉刷新
	 * 
	 * @param flag
	 */
	public void setlistview(boolean... flag) {
		switch (flag.length) {
		case 0:
			listview.setClickable(true);
			listview.setPullRefreshEnable(true, true);// listView可以刷新
			break;
		case 1:
			listview.setClickable(flag[0]);
			break;
		case 2:
			listview.setPullRefreshEnable(flag[0], flag[1]);
			break;
		case 3:
			listview.setOnItemLongClickListener(null);
			listview.setPullRefreshEnable(flag[1], flag[2]);
			break;

		default:
			break;
		}
	}

	/**
	 * k=4 编辑状态 全部可点击 k=-1,-2,-3 正常状态 k=0,1,2,3 编辑状态 部分可点击
	 * 
	 * @param k
	 * @param flag
	 */
	// public void setButtonState(int k, boolean flag) {
	// if (k < 0) {
	// showButtons(true);
	// setNormalButtonsState(flag);
	// } else {
	// showButtons(false);
	// setButtonState(k, flag);
	// }
	// }

	/* 判断显示哪一套Buttons */
	public void showButtons(boolean flag) {
		if (flag) {
			linearLayout1.setVisibility(View.VISIBLE);
			linearLayout2.setVisibility(View.GONE);
		} else {
			linearLayout1.setVisibility(View.INVISIBLE);
			linearLayout2.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 正常状态下button的显示
	 * 
	 * @param flag
	 */
	public void setNormalButtonsState(boolean flag) {
		for (int i = 0; i < 3; i++) {
			buttons[i].setClickable(flag);
			buttons[i].setTextColor(flag ? Color.WHITE : Color.GRAY);// 设置字体颜色
			buttons[i].setBackgroundResource(flag ? ids_background[i] : R.drawable.button_unclicked);// 设置样式
		}
	}

	/**
	 * 编辑状态下button的显示
	 * 
	 * @param flag
	 */
	public void setEditButtonsState(int k, boolean flag) {
		for (int i = 3; i < 7; i++) {
			if (i != k + 3) {
				buttons[i].setClickable(flag);
			} else {
				buttons[i].setClickable(!flag);
			}
			buttons[i]
					.setBackgroundResource(buttons[i].isClickable() ? ids_background[i] : R.drawable.button_unclicked);// 设置样式
			buttons[i].setTextColor(buttons[i].isClickable() ? Color.WHITE : Color.GRAY);// 设置字体颜色
		}
	}

	/**
	 * 根据选中的个数设置应该button显示的效果
	 * 
	 * @param m
	 */
	public void setChosedSize(int m) {
		switch (m) {
		case 0:
			setEditButtonsState(3, false);
			break;
		case 1:
			setEditButtonsState(4, true);
			break;
		default:
			setEditButtonsState(2, true);
			break;
		}
		setButtontext(m);
	}

	/**
	 * 根据选中的个数设置应该button显示的文字
	 * 
	 * @param k
	 */
	public void setButtontext(int k) {
		if (k == 0) {
			buttons[3].setText("清理");
			buttons[4].setText("忽略");
		} else {
			buttons[3].setText("清理(" + k + ")");
			buttons[4].setText("忽略(" + k + ")");
		}
	}

	public void setButtonsListener(OnClickListener listener) {
		for (int i = 0; i < ids.length; i++) {
			buttons[i].setOnClickListener(listener);
		}
	}

	/**
	 * 跳转到详情显示
	 * 
	 * @param bitmap
	 * @param name
	 */
	public void turnto(Bitmap bitmap, String name) {
		Intent intent = new Intent(context, Application_Detail.class);
		intent.putExtra("application_icon", bitmap);
		intent.putExtra("application_name", name);
		context.startActivity(intent);
	}
}
