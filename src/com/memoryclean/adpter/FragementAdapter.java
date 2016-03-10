package com.memoryclean.adpter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragementAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> items;
	//private FragmentManager fm;

	public FragementAdapter(FragmentManager fm, ArrayList<Fragment> items) {
		super(fm);
		this.items = items;
		//this.fm = fm;
	}

	@Override
	public Fragment getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
}
