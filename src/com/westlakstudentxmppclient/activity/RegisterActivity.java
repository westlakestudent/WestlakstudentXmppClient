package com.westlakstudentxmppclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.westlakstudentxmppclient.R;
import com.westlakstudentxmppclient.config.SystemConfigration;
import com.westlakstudentxmppclient.interfaces.ActivityJumpListener;
import com.westlakstudentxmppclient.utils.WestlakestudentDialog;
import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

/**
 * 
 * RegisterActivity
 * 
 * @author chendong 2014年10月30日 上午9:39:52
 * @version 1.0.0
 * 
 */
public class RegisterActivity extends Activity implements ActivityJumpListener {

	private static final String TAG = "RegisterActivity";
	private EditText usernameEdit = null;
	private EditText passwordEdit = null;
	private Button registerBtn = null;
	private String username = null;
	private String password = null;
	private XmppHandlerManager mXmppHandlerManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		mXmppHandlerManager = XmppHandlerManager.getInstance();
		mXmppHandlerManager.registerHandler(this);
		SystemConfigration.config(RegisterActivity.this);

		usernameEdit = (EditText) findViewById(R.id.username);
		passwordEdit = (EditText) findViewById(R.id.password);
		registerBtn = (Button) findViewById(R.id.register);
		registerBtn.setOnClickListener(listener);

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			username = usernameEdit.getText().toString().trim();
			password = passwordEdit.getText().toString().trim();
			
			Log.d(TAG, "username:" + username);
			Log.d(TAG, "password:" + password);

			if (username == null || username.equals("") || password == null
					|| password.equals("")) {
				WestlakestudentDialog
						.show(RegisterActivity.this, "用户名，密码不能为空!");
				return;
			}

			if(!mXmppHandlerManager.isConnected()){
				mXmppHandlerManager.startConnectTask(
						XmppHandlerManager.REGISTERTASK, username, password,
						RegisterActivity.this);
			}else{
				mXmppHandlerManager.sendEmptyMessage(XmppHandlerManager.REGISTERTASK);
			}
				
		}
	};

	@Override
	public void jump() {
		Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}
