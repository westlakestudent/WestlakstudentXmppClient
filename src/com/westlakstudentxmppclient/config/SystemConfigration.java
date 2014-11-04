package com.westlakstudentxmppclient.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 
 * SystemConfigration
 * 
 * @author chendong 2014年10月28日 上午11:16:13
 * @version 1.0.0
 * 
 */
public class SystemConfigration {

	private static final String TAG = "SystemConfigration";

	public static void config(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		SharedPreferences sharedPref = context.getSharedPreferences(
				Constants.PREF_NAME, Context.MODE_PRIVATE);
		
		String imei = telephonyManager.getDeviceId();
		Editor editor = sharedPref.edit();
		editor.putString(Constants.IMEI, imei);
		InputStream in = context.getClass().getResourceAsStream(
				"/com/westlakstudentxmppclient/config/config.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			String port = prop.getProperty("xmppport");
			String host = prop.getProperty("xmpphost");
			editor.putString(Constants.XMPPHOST, host);
			editor.putInt(Constants.XMPPPORT, Integer.valueOf(port));
			editor.commit();
		} catch (IOException e) {
			Log.e(TAG, "config failed" + e);

		}
	}
}
