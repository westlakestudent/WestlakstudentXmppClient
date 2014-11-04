package com.westlakstudentxmppclient.activity;

import com.westlakstudentxmppclient.R;
import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.config.SystemConfigration;
import com.westlakstudentxmppclient.notification.NotificationService;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Constants.LOGINED = true;
		findViewById(R.id.start).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SystemConfigration.config(MainActivity.this);
				Intent intent = new Intent(MainActivity.this, NotificationService.class);
				startService(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	
}
