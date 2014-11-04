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
 * LoginActivity
 * @author chendong
 * 2014年10月30日 下午6:51:05
 * @version 1.0.0
 *
 */
public class LoginActivity extends Activity implements OnClickListener,ActivityJumpListener{

	private static final String TAG = "LoginActivity";
	
	private EditText username = null;
	private EditText password = null;
	private Button loginBtn = null;
	
	private String name = null;
	private String pass = null;
	
	private XmppHandlerManager mXmppHandlerManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mXmppHandlerManager = XmppHandlerManager.getInstance();
		mXmppHandlerManager.registerHandler(this);
		SystemConfigration.config(LoginActivity.this);
		
		
		username = (EditText)findViewById(R.id.name);
		password = (EditText)findViewById(R.id.pwd);
		loginBtn = (Button)findViewById(R.id.login);
		loginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		name = username.getText().toString().trim();
		pass = password.getText().toString().trim();
		
		Log.d(TAG, "username:" + name);
		Log.d(TAG, "password:" + pass);
		
		if (name == null || name.equals("") || pass == null
				|| pass.equals("")) {
			WestlakestudentDialog
					.show(LoginActivity.this, "用户名，密码不能为空!");
			return;
		}

		
		if(!mXmppHandlerManager.isConnected()){
			mXmppHandlerManager.startConnectTask(
					XmppHandlerManager.LOGINTASK, name, pass,
					LoginActivity.this);
		}else{
			mXmppHandlerManager.sendEmptyMessage(XmppHandlerManager.LOGINTASK);
		}
		
	}

	@Override
	public void jump() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	

}
