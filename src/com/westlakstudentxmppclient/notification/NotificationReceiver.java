package com.westlakstudentxmppclient.notification;

import com.westlakstudentxmppclient.config.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * NotificationReceiver
 * 
 * @author chendong 2014年10月28日 上午10:13:49
 * @version 1.0.0
 * 
 */
public class NotificationReceiver extends BroadcastReceiver {

	private static final String TAG = "NotificationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Constants.ACTION_SHOW_NOTIFICATION)) {
			String notificationId = intent
					.getStringExtra(Constants.NOTIFICATION_ID);
			String notificationImei = intent
					.getStringExtra(Constants.NOTIFICATION_IMEI);
			String notificationTitle = intent
					.getStringExtra(Constants.NOTIFICATION_TITLE);
			String notificationMessage = intent
					.getStringExtra(Constants.NOTIFICATION_MESSAGE);
			String notificationRemark = intent
					.getStringExtra(Constants.NOTIFICATION_REMARK);

			Log.d(TAG, "notificationId=" + notificationId);
			Log.d(TAG, "notificationImei=" + notificationImei);
			Log.d(TAG, "notificationTitle=" + notificationTitle);
			Log.d(TAG, "notificationMessage=" + notificationMessage);
			Log.d(TAG, "notificationRemark=" + notificationRemark);

			Notifier notifier = new Notifier(context);
			notifier.notify(notificationId, notificationImei,
					notificationTitle, notificationMessage, notificationRemark);
		}
	}

}
