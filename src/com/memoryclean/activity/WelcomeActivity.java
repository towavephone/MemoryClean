package com.memoryclean.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.wust.memoryclean.R;

public class WelcomeActivity extends Activity {
	private static final int TIME = 2000;
	private static final int GO_HOME = 1001;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startActivity(new Intent(WelcomeActivity.this, Main.class));
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
			}

		}, 50);
	}
}
