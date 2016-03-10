package com.memoryclean.cache;

import android.graphics.Bitmap;

public class MMap {
	private int count;
	private String uri;
	private Bitmap picture;

	public MMap(int count, String uri, Bitmap picture) {
		this.count = count;
		this.uri = uri;
		this.picture = picture;
	}

	public MMap(String uri, Bitmap picture) {
		this.count = 1;
		this.uri = uri;
		this.picture = picture;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

}
