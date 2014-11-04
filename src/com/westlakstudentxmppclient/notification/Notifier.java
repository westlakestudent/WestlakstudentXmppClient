package com.westlakstudentxmppclient.notification;

import java.util.Random;

import com.westlakstudentxmppclient.R;
import com.westlakstudentxmppclient.activity.NotificationActivity;
import com.westlakstudentxmppclient.config.Constants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * 
 * Notifier
 * 
 * @author chendong 2014年10月28日 上午10:20:03
 * @version 1.0.0
 * 
 */
public class Notifier {
	private static final String TAG = "Notifier";

	private SharedPreferences sharedPref = null;

	private NotificationManager notificationManager = null;

	private Context context = null;

	private static final Random random = new Random(System.currentTimeMillis());

	public Notifier(Context context) {
		sharedPref = context.getSharedPreferences(Constants.PREF_NAME,
				Context.MODE_PRIVATE);
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.context = context;
	}

	public void notify(String notificationId, String imei, String title,
			String message, String remark) {
		Log.d(TAG, "notify()...");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setTicker("New message");
		builder.setWhen(System.currentTimeMillis());
		builder.setSmallIcon(R.drawable.notification);

		builder.setAutoCancel(true);

		Intent intent = new Intent(context, NotificationActivity.class);
		intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
		intent.putExtra(Constants.NOTIFICATION_IMEI, imei);
		intent.putExtra(Constants.NOTIFICATION_TITLE, title);
		intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
		intent.putExtra(Constants.NOTIFICATION_REMARK, remark);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context,
				random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(contentIntent);
		
		Notification notification = builder.build();
		notification.defaults = Notification.DEFAULT_LIGHTS;
		if (isNotificationSoundEnabled()) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (isNotificationVibrateEnabled()) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		notificationManager.notify(random.nextInt(), notification);

	}

	private boolean isNotificationSoundEnabled() {
		return sharedPref.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
	}

	private boolean isNotificationVibrateEnabled() {
		return sharedPref.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
	}

}
