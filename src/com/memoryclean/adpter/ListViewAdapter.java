package com.memoryclean.adpter;

import java.util.ArrayList;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;

import com.memoryclean.cache.ViewCache;
import com.memoryclean.tools.AsyncImageLoader;
import com.wust.memoryclean.R;

public class ListViewAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater;
	private ArrayList<Map<String, Object>> infoMapArr;
	private ArrayList<Map<String, Object>> infoMapArr_original;
	private boolean isVisibleCheckBox;
	private boolean isVisibleTextViewMemory;
	private boolean isVisibleTextViewCleanTime;
	private boolean isLocked;
	private boolean isLogs = false;

	private AsyncImageLoader asyncImageLoader;

	private ViewCache viewcache;
	private NameFilter filter;

	public ListViewAdapter(Context context, ArrayList<Map<String, Object>> infoMapArr, ListView listview) {
		mInflater = LayoutInflater.from(context);
		this.infoMapArr = infoMapArr;
		asyncImageLoader = new AsyncImageLoader(context);
	}

	public boolean isLogs() {
		return isLogs;
	}

	public void setLogs(boolean isLogs) {
		this.isLogs = isLogs;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public boolean isVisibleTextViewCleanTime() {
		return isVisibleTextViewCleanTime;
	}

	public void setVisibleTextViewCleanTime(boolean isVisibleTextViewCleanTime) {
		this.isVisibleTextViewCleanTime = isVisibleTextViewCleanTime;
	}

	public ArrayList<Map<String, Object>> getInfoMapArr() {
		return infoMapArr;
	}

	public void setInfoMapArr(ArrayList<Map<String, Object>> infoMapArr) {
		this.infoMapArr = infoMapArr;
	}

	public boolean isVisibleCheckBox() {
		return isVisibleCheckBox;
	}

	public void setVisibleCheckBox(boolean isVisibleCheckBox) {
		this.isVisibleCheckBox = isVisibleCheckBox;
	}

	public boolean isVisibleTextViewMemory() {
		return isVisibleTextViewMemory;
	}

	public void setVisibleTextViewMemory(boolean isVisibleTextView) {
		this.isVisibleTextViewMemory = isVisibleTextView;
	}

	@Override
	public int getCount() {
		return infoMapArr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return infoMapArr.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.application_item, null);
			viewcache = new ViewCache(convertView);
			convertView.setTag(viewcache);
		} else {
			viewcache = (ViewCache) convertView.getTag();
		}
		// viewcache.getApplication_icon().setImageBitmap(
		// (Bitmap) infoMapArr.get(position).get("application_icon"));
		String key;
		if (!isLogs)
			key = (String) infoMapArr.get(position).get("application_name");
		else {
			String[] s = infoMapArr.get(position).get("application_memory").toString().split("\t");
			String[] ss = s[1].split(",");
			int k = ss.length;
			if (k > 4)
				k = 4;
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < k; i++)
				str.append(ss[i] + ",");
			str.delete(str.length() - 1, str.length());
			key = new String(str);
		}
		// Bitmap bitmap = ImageCache.getInstance().get(appname);

		// if (bitmap == null)
		// viewcache.getApplication_icon().setImageResource(
		// R.drawable.clean_log_background);
		// else
		ImageView iv = viewcache.getApplication_icon();
		iv.setTag(key);
		// Bitmap bitmap = asyncImageLoader.loadDrawable(key, isLogs,
		// new ImageCallback() {
		// public void imageLoaded(Drawable imageDrawable,
		// String imageUrl) {
		// ImageView imageViewByTag = (ImageView) listview
		// .findViewWithTag(imageUrl);
		// if (imageViewByTag != null) {
		// imageViewByTag.setImageDrawable(imageDrawable);
		// }
		// }
		// });
		Bitmap bitmap = asyncImageLoader.loadDrawable(key, isLogs);
		if (bitmap != null)
			iv.setImageBitmap(bitmap);
		Map<String, Object> map = infoMapArr.get(position);
		viewcache.getApplication_name().setText(map.get("application_name").toString());

		viewcache.getApplication_memory().setText(map.get("application_memory").toString());

		viewcache.getApplication_memory().setVisibility(isVisibleTextViewMemory ? View.VISIBLE : View.GONE);

		viewcache.getApplication_ischecked().setVisibility(isVisibleCheckBox ? View.VISIBLE : View.INVISIBLE);

		viewcache.getApplication_ischecked().setChecked((Boolean) map.get("application_ischecked"));

		viewcache.getApplication_islocked().setVisibility(isLocked ? View.VISIBLE : View.INVISIBLE);

		viewcache.getApplication_islocked().setChecked((Boolean) map.get("application_ischecked"));

		viewcache.getClean_time().setText(map.get("clean_time") + "");

		viewcache.getClean_time().setVisibility(isVisibleTextViewCleanTime ? View.VISIBLE : View.GONE);

		if (!isVisibleCheckBox && !isLocked) {
			viewcache.getHint_checked().setVisibility(View.INVISIBLE);
		} else {
			viewcache.getHint_checked()
					.setVisibility((Boolean) map.get("application_ischecked") ? View.VISIBLE : View.INVISIBLE);
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new NameFilter();
		}
		return filter;
	}

	public class NameFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (infoMapArr_original == null) {
				synchronized (this) {
					infoMapArr_original = new ArrayList<Map<String, Object>>(infoMapArr);
				}
			}
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<Map<String, Object>> infoMapArr_search = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map : infoMapArr_original) {
					if (map.get("application_name").toString().toLowerCase().contains(constraint)) {
						infoMapArr_search.add(map);
					}
				}
				result.count = infoMapArr_search.size();
				result.values = infoMapArr_search;
				infoMapArr_search = null;
			} else {
				synchronized (this) {
					result.values = infoMapArr_original;
					result.count = infoMapArr_original.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			infoMapArr = (ArrayList<Map<String, Object>>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

	}
}