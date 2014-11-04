package com.westlakstudentxmppclient.tasks;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.provider.ProviderManager;

import android.os.Message;
import android.util.Log;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.notification.NotificationIQProvider;
import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

/**
 * 
 * ConnectTask
 * 
 * @author chendong 2014年10月30日 上午10:36:44
 * @version 1.0.0
 * 
 */
public class ConnectTask implements Runnable {

	private XmppHandlerManager mHandler = null;
	private String mXmppHost = null;
	private int mXmppPort = 0;

	private static final String TAG = "ConnectTask";

	public ConnectTask(String xmpphost, int xmppport, XmppHandlerManager handler) {
		mHandler = handler;
		mXmppHost = xmpphost;
		mXmppPort = xmppport;
	}

	@Override
	public void run() {
		Log.d(TAG, "running...");

		ConnectionConfiguration config = new ConnectionConfiguration(mXmppHost,
				mXmppPort);

		config.setSecurityMode(SecurityMode.disabled);
		config.setSASLAuthenticationEnabled(false);
		config.setCompressionEnabled(false);
		XMPPConnection connection = new XMPPConnection(config);
		try {
			connection.connect();
			Log.i(TAG, "Connect successfully!");

			ProviderManager.getInstance().addIQProvider("notification",
					"androidpn:iq:notification", new NotificationIQProvider());

			Message msg = mHandler.obtainMessage();
			msg.obj = connection;
			msg.what = XmppHandlerManager.CONNECTION_AVAILABE;
			mHandler.sendMessage(msg);
		} catch (XMPPException e) {
			Log.e(TAG, "Connect failed!" + e);
			boolean logined = Constants.LOGINED;
			if (!logined)
				mHandler.sendEmptyMessage(XmppHandlerManager.CONNECT_ERROR);
			return;
		}
	}

}
