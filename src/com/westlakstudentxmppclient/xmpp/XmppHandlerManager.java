package com.westlakstudentxmppclient.xmpp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.interfaces.ActivityJumpListener;
import com.westlakstudentxmppclient.tasks.ConnectTask;
import com.westlakstudentxmppclient.tasks.LoginTask;
import com.westlakstudentxmppclient.tasks.ReconnectTask;
import com.westlakstudentxmppclient.tasks.RegisterTask;
import com.westlakstudentxmppclient.utils.WestlakestudentDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * XmppHandlerManager
 * 
 * @author chendong 2014年10月30日 上午10:39:00
 * @version 1.0.0
 * 
 */
public class XmppHandlerManager extends Handler {

	private static final String TAG = "XmppHandlerManager";

	public static final int CONNECTION_AVAILABE = 0;
	public static final int REGISTERTASK = 1;
	public static final int LOGINTASK = 2;
	public static final int CONNECT_ERROR = 3;
	public static final int DUPLICATE_IMEI = 4;
	public static final int REGISTERE_SUCCESSFULLY = 5;
	public static final int LOGIN_SUCCESSFULLY = 6;
	public static final int DIALOG_DISMISS = 7;
	public static final int DIALOG_SHOW = 8;
	public static final int LOGIN_ERROR = 9;
	public static final int NO_REGISTER = 10;
	public static final int REGISTER_FAILRE = 11;
	public static final int ALREADY_ONLINE = 12;
	public static final int PASSWORD_ERROR = 13;
	public static final int USERNAME_ERROR = 14;

	private int flag = REGISTERTASK;

	private static XmppHandlerManager INSTANCE = null;

	private ProgressDialog dialog = null;
	private SharedPreferences sharedpref = null;
	private ExecutorService executorService = null;
	private String xmpphost = null;
	private int xmppport = 0;
	private XMPPConnection connection = null;
	private String username = null;
	private String password = null;
	private Context context = null;
	public boolean isConnected = false;

	private ActivityJumpListener mJumpListener = null;

	private ReconnectTask reconnection = null;

	private XmppHandlerManager() {
		initialize();
	}

	public static XmppHandlerManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new XmppHandlerManager();
		return INSTANCE;
	}

	private void initialize() {
		executorService = Executors.newSingleThreadExecutor();
	}

	public void registerHandler(Context context) {
		this.context = context;
		dialog = new ProgressDialog(context);
		dialog.setTitle("提示");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		sharedpref = context.getSharedPreferences(Constants.PREF_NAME,
				Context.MODE_PRIVATE);

		xmpphost = sharedpref.getString(Constants.XMPPHOST, "");
		xmppport = sharedpref.getInt(Constants.XMPPPORT, 5222);

		reconnection = new ReconnectTask(getInstance(), connectionListener);
	}

	public void startConnectTask(int flag, String username, String password,
			ActivityJumpListener listener) {
		this.flag = flag;
		this.username = username;
		this.password = password;
		mJumpListener = listener;

		if (flag == REGISTERTASK)
			dialog.setMessage("正在注册...");
		else if (flag == LOGINTASK)
			dialog.setMessage("正在登录...");

		dialog.show();

		executorService.submit(new ConnectTask(xmpphost, xmppport,
				getInstance()));

	}

	public void trying() {
		executorService.submit(new ConnectTask(xmpphost, xmppport,
				getInstance()));
	}

	private void startRegisterTask(XMPPConnection connection) {
		executorService.submit(new RegisterTask(getInstance(), username,
				password, sharedpref, connection));
	}

	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	private void startLoginTask(XMPPConnection connection) {
		executorService.submit(new LoginTask(connectionListener, connection,
				getInstance(), username, password, context));
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int key = msg.what;

		switch (key) {
		case CONNECTION_AVAILABE:
			if (reconnection != null)
				reconnection.setTrying(0);

			connection = (XMPPConnection) msg.obj;
			if (Constants.LOGINED) {
				startLoginTask(connection);
				return;
			}

			if (flag == REGISTERTASK)
				startRegisterTask(connection);
			else if (flag == LOGINTASK)
				startLoginTask(connection);

			break;
		case REGISTERTASK:
			startRegisterTask(connection);
			break;
		case LOGINTASK:
			startLoginTask(connection);
			break;
		case CONNECT_ERROR:
			dialog.dismiss();
			WestlakestudentDialog.show(context, "服务器连接失败!");
			break;
		case DUPLICATE_IMEI:
			WestlakestudentDialog.show(context, "此帐号已被注册!");
			break;
		case REGISTER_FAILRE:
			WestlakestudentDialog.show(context, "注册失败!");
			break;
		case REGISTERE_SUCCESSFULLY:
			startLoginTask(connection);
			break;
		case LOGIN_SUCCESSFULLY:
			isConnected = true;
			dialog.dismiss();
			Editor editor = sharedpref.edit();
			editor.putString(Constants.USERNAME, username);
			editor.putString(Constants.PASSWORD, password);
			editor.commit();
			Log.d(TAG, "此处跳转");
			if (mJumpListener != null && !Constants.LOGINED)
				mJumpListener.jump();
			break;
		case DIALOG_DISMISS:
			dialog.dismiss();
			break;
		case DIALOG_SHOW:
			dialog.show();
			break;
		case NO_REGISTER:
			if (Constants.LOGINED)
				return;
			dialog.dismiss();
			WestlakestudentDialog.show(context, "未注册的帐号");
			break;
		case ALREADY_ONLINE:
			if (Constants.LOGINED)
				return;
			dialog.dismiss();
			WestlakestudentDialog.show(context, "此帐号已经被登录");
			if (connection != null && connection.isConnected())
				connection.disconnect();
			break;
		case PASSWORD_ERROR:
			if (Constants.LOGINED)
				return;
			dialog.dismiss();
			WestlakestudentDialog.show(context, "密码错误");
			if (connection != null && connection.isConnected())
				connection.disconnect();
			break;
		case USERNAME_ERROR:
			if (Constants.LOGINED)
				return;
			dialog.dismiss();
			WestlakestudentDialog.show(context, "帐号错误");
			if (connection != null && connection.isConnected())
				connection.disconnect();
			break;
		case LOGIN_ERROR:
			if (Constants.LOGINED)
				return;
			dialog.dismiss();
			String errorMsg = (String) msg.obj;
			WestlakestudentDialog.show(context, errorMsg);
			break;
		}
	}

	public void startReconnectionThread() {
		synchronized (reconnection) {
			if (!reconnection.isAlive()) {
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection.start();
				Log.d(TAG, "reconnection started");
			}
		}
	}

	private ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void reconnectionSuccessful() {
			Log.i(TAG, "reconnectionSuccessful");
		}

		@Override
		public void reconnectionFailed(Exception e) {
			Log.e(TAG, "reconnectionFailed" + e);
		}

		@Override
		public void reconnectingIn(int seconds) {
			Log.i(TAG, "reconnectingIn " + seconds);
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			Log.e(TAG, "connectionClosedOnError " + e);

			if (connection != null && connection.isConnected()) {
				connection.disconnect();
			}
			isConnected = false;
			startReconnectionThread();
		}

		@Override
		public void connectionClosed() {
			Log.i(TAG, "connectionClosed");
		}
	};

}
