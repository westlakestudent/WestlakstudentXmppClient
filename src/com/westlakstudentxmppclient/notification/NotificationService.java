package com.westlakstudentxmppclient.notification;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 
 * NotificationService
 * 
 * @author chendong 2014年10月28日 上午10:37:15
 * @version 1.0.0
 * 
 */
public class NotificationService extends Service {

	private static final String TAG = "NotificationService";

	private TelephonyManager telephonyManager = null;

	private XmppHandlerManager xmppHandlerManager = null;

	private ConnectivityReceiver connectivityReceiver = null;

	private BroadcastReceiver notificationReceiver = null;
	
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {

		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
			Log.d(TAG, "onDataConnectionStateChanged()...");
			Log.d(TAG, "Data Connection State = " + getState(state));
			if (state == TelephonyManager.DATA_CONNECTED) {
				if(!xmppHandlerManager.isConnected()){
					Log.d(TAG, "connection is not connected,trying connecting again!");
					xmppHandlerManager.trying();
					}
			}
		}

	};

	private String getState(int state) {
		switch (state) {
		case 0: // '\0'
			return "DATA_DISCONNECTED";
		case 1: // '\001'
			return "DATA_CONNECTING";
		case 2: // '\002'
			return "DATA_CONNECTED";
		case 3: // '\003'
			return "DATA_SUSPENDED";
		}
		return "DATA_<UNKNOWN>";
	}

	private class ConnectivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "action=" + action);

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();

			if (networkInfo != null) {
				Log.d(TAG, "Network Type  = " + networkInfo.getTypeName());
				Log.d(TAG, "Network State = " + networkInfo.getState());
				if (networkInfo.isConnected()) {
					Log.i(TAG, "Network connected");
					if(!xmppHandlerManager.isConnected()){
						Log.d(TAG, "connection is not connected,,trying connecting again!");
						xmppHandlerManager.trying();
					}
				}
			} else {
				Log.e(TAG, "Network unavailable");
			}
		}

	}

	private void registerConnectivityReceiver() {
		Log.d(TAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		Log.d(TAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	private void registerNotificationReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
		filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
		filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
		registerReceiver(notificationReceiver, filter);
	}

	private void unregisterNotificationReceiver() {
		unregisterReceiver(notificationReceiver);
	}

	private void start() {
		Log.d(TAG, "start()...");
		registerNotificationReceiver();
		registerConnectivityReceiver();

	}

	private void stop() {
		unregisterConnectivityReceiver();
		unregisterNotificationReceiver();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate...");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		xmppHandlerManager = XmppHandlerManager.getInstance();
		connectivityReceiver = new ConnectivityReceiver();
		notificationReceiver = new NotificationReceiver();

		start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop();
	}

}
