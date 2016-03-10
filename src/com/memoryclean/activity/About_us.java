package com.memoryclean.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.wust.memoryclean.R;

public class About_us extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
