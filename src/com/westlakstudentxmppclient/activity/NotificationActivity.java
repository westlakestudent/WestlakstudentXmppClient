package com.westlakstudentxmppclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.westlakstudentxmppclient.R;
import com.westlakstudentxmppclient.config.Constants;

/**
 * 
 * NotificationActivity
 * 
 * @author chendong 2014年10月31日 上午10:41:58
 * @version 1.0.0
 * 
 */
public class NotificationActivity extends Activity {

	private static final String TAG = "NotificationActivity";
	private TextView mText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification);

		mText = (TextView) findViewById(R.id.text);
		Intent i = getIntent();
		String message = i.getStringExtra(Constants.NOTIFICATION_MESSAGE);
		String imei = i.getStringExtra(Constants.NOTIFICATION_IMEI);
		String title = i.getStringExtra(Constants.NOTIFICATION_TITLE);
		String remark = i.getStringExtra(Constants.NOTIFICATION_REMARK);

		mText.setText("message : " + message + "\nimei :" + imei
				+ "\ntitle : " + title + "\nremark : " + remark);
		Log.d(TAG, i.getExtras().toString());
	}

}
