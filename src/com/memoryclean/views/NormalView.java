package com.memoryclean.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.memoryclean.properties.SettingManager;
import com.wust.memoryclean.R;

/**
 * 正常状态的对话框即一般形式的对话框有title Message Button
 * 
 * @author tianwailaike
 *
 */
public class NormalView extends MView {
	View parent, child;
	TextView title, message;
	Button bt1, bt2;
	CheckBox cb1, cb2;
	Context context;

	@SuppressLint("InflateParams")
	public NormalView(Context context, boolean showcheckbox, boolean showbutton) {
		this.context = context;
		parent = LayoutInflater.from(context).inflate(R.layout.xdialog_item,
				null);
		child = parent.findViewById(R.id.normal_dialog);
		child.setVisibility(View.VISIBLE);
		title = (TextView) parent.findViewById(R.id.xdialog_title);
		message = (TextView) child.findViewById(R.id.dialog_message);
		bt1 = (Button) child.findViewById(R.id.dialog_sure);
		bt2 = (Button) child.findViewById(R.id.dialog_cancel);
		cb1 = (CheckBox) child.findViewById(R.id.exit);
		cb2 = (CheckBox) child.findViewById(R.id.remenber);

		if (showcheckbox) {
			CsetVisibility(View.VISIBLE);
		} else
			CsetVisibility(View.GONE);
		if (showbutton) {
			BsetVisibility(View.VISIBLE);
		} else
			BsetVisibility(View.GONE);

		loadData();
	}

	private void CsetVisibility(int flag) {
		cb1.setVisibility(flag);
		cb2.setVisibility(flag);
	}

	private void BsetVisibility(int flag) {
		bt1.setVisibility(flag);
		bt2.setVisibility(flag);
	}

	public void setListener(OnClickListener listener) {
		bt1.setOnClickListener(listener);
		bt2.setOnClickListener(listener);
	}

	public void setTitle(String s) {
		title.setText(s);
	}

	public void setMessage(String s) {
		message.setText(s);
	}

	public void setCheckBoxMessage(String s1, String s2) {
		cb1.setText(s1);
		cb2.setText(s2);
	}

	public void setBothSelected(boolean flag) {
		if (!flag) {
			cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked && cb2.isChecked())
						cb2.setChecked(false);

				}
			});
			cb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked && cb1.isChecked())
						cb1.setChecked(false);
				}
			});
		}
	}

	/**
	 * 根据用户设置判断单选框是否被选中
	 */
	private void loadData() {
		switch (new SettingManager(context).getInt("exit")) {
		case 0:// 不记住
			cb1.setChecked(false);
			cb2.setChecked(false);
			break;
		case -1:// 记住，退出
			cb1.setChecked(false);
			cb2.setChecked(true);
			break;
		case 1:// 记住，不退出
			cb1.setChecked(true);
			cb2.setChecked(true);
			break;

		default:
			break;
		}
	}

	public CheckBox[] getcheckbox() {
		CheckBox[] cb = { cb1, cb2 };
		return cb;
	}

	public View getView() {
		return parent;
	}
}