package com.memoryclean.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.memoryclean.activity.Global_Config;
import com.memoryclean.activity.Main;
import com.memoryclean.manager.MemoryUsedMessage;
import com.memoryclean.tools.GetLogTotal;
import com.wust.memoryclean.R;

public class NotificationService extends Service {
	public static final String STATUS_CLICK_ACTION[] = new String[] {
			"com.example.action.main", "com.example.action.clean",
			"com.example.action.log", "com.example.action.settings" };
	public static final int ids[] = new int[] { R.id.launch_main,
			R.id.launch_clean, R.id.switch_state_total, R.id.launch_settings };
	int notification_id = 19172434;
	private NotificationManager mNM;
	Notification notification;
	Context mContext;
	int delaytime = 1000;
	boolean flag;

	private MReceiver receiver = new MReceiver();

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		mContext = this;
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		showNotification(R.drawable.ic_launcher, "内存信息", "标题", "内容");
		handler.postDelayed(task, delaytime);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mNM.cancel(notification_id);
		handler.removeCallbacks(task);
		unregisterReceiver(receiver);
		unregisterReceiver(onClickReceiver);
	}

	@SuppressWarnings("deprecation")
	public void showNotification(int icon, String tickertext,
			String contentTitle, String contentText) {
		notification = new Notification(icon, tickertext,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_memory);
		contentView.setTextViewText(R.id.used_memory, "35%");
		contentView.setTextViewText(R.id.clean_memory_total, "70.3M");
		notification.contentView = contentView;
		mNM.notify(notification_id, notification);
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			dataRefresh();
			handler.postDelayed(this, delaytime);
		}
	};

	protected void dataRefresh() {
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_memory);
		contentView.setTextViewText(R.id.used_memory,
				MemoryUsedMessage.getPercent(mContext) + "%");
		String[] total = GetLogTotal.getLogTotal(mContext);
		if (!flag) {
			contentView
					.setTextViewText(R.id.clean_memory_total, total[0] + "M");
			contentView.setTextViewText(R.id.hint_message, "今日释放");
		} else {
			contentView
					.setTextViewText(R.id.clean_memory_total, total[1] + "M");
			contentView.setTextViewText(R.id.hint_message, "总释放");
		}

		for (int i = 0; i < STATUS_CLICK_ACTION.length; i++) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(STATUS_CLICK_ACTION[i]);
			registerReceiver(onClickReceiver, filter);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i,
					new Intent(STATUS_CLICK_ACTION[i]),
					PendingIntent.FLAG_UPDATE_CURRENT);
			contentView.setOnClickPendingIntent(ids[i], pendingIntent);
		}

		notification.contentView = contentView;
		mNM.notify(notification_id, notification);
	}

	BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int click_button_id = 0;
			for (int i = 0; i < STATUS_CLICK_ACTION.length; i++) {
				if (intent.getAction().equals(STATUS_CLICK_ACTION[i])) {
					click_button_id = i;
					break;
				}
			}
			Intent click_intent;
			switch (click_button_id) {
			case 0:
				click_intent = new Intent(mContext, Main.class);
				click_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(click_intent);
				sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
				break;
			case 1:
				break;
			case 2:
				flag = !flag;
				break;
			case 3:
				click_intent = new Intent(mContext, Global_Config.class);
				click_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(click_intent);
				sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
				break;
			default:
				break;
			}
		};
	};
}
