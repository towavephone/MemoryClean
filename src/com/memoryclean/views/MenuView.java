package com.memoryclean.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.memoryclean.classes.MPrograme;
import com.wust.memoryclean.R;

/**
 * 对话框以menu的形式显示
 * 
 * @author tianwailaike
 *
 */
public class MenuView extends MView implements OnClickListener {
	private View parent, child, head;
	private Button bt1, bt2, bt3;
	private MPrograme mp;
	private Context context;

	public MenuView(Context context, MPrograme mp) {
		this.mp = mp;
		this.context = context;
		parent = LayoutInflater.from(context).inflate(R.layout.xdialog_item,
				null);
		head = parent.findViewById(R.id.dialog_head);
		child = parent.findViewById(R.id.menu_button);
		child.setVisibility(View.VISIBLE);
		head.setVisibility(View.GONE);
		bt1 = (Button) child.findViewById(R.id.turnto);
		bt2 = (Button) child.findViewById(R.id.detail);
		bt3 = (Button) child.findViewById(R.id.uninstall);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
	}

	public View getView() {
		return parent;
	}

	@Override
	public void onClick(View v) {
		try {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.uninstall:
				intent.setAction(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:" + mp.getPkgname()));
				break;
			case R.id.turnto:
				intent = mp.getLaunchIntent();
				break;
			case R.id.detail:
				Uri packageURI = Uri.parse("package:" + mp.getPkgname());
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(packageURI);
				break;
			}
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "该动作无法完成", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

}
