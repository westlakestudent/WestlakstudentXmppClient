package com.westlakstudentxmppclient.xmpp.debug;

import org.jivesoftware.smack.ConnectionListener;

import android.os.Handler;
import android.util.Log;

/**
 * 暂时未用此类
 * ReconnectionThread
 * 
 * @author chendong 2014年10月28日 下午2:41:20
 * @version 1.0.0
 * 
 */
public class ReconnectionThread extends Thread {

	private WestlakestudentXmppManager xmppManager = null;
	private ConnectionListener connectionListener = null;
	private Handler handler = null;
	private int waiting = 0;

	private static final String TAG = "ReconnectionThread";

	public ReconnectionThread(WestlakestudentXmppManager xmppmanager,
			Handler handler, ConnectionListener listener) {
		connectionListener = listener;
		this.handler = handler;
		xmppManager = xmppmanager;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	@Override
	public void run() {
		super.run();
		try {
			while (!isInterrupted()) {
				if (xmppManager.isConnected())
					continue;
				Log.d(TAG, "Trying to reconnect in " + waiting() + " seconds");
				Thread.sleep((long) waiting() * 1000L);
				xmppManager.start();
				waiting++;
				Log.d(TAG, "try " + waiting + " reconnection");
			}
		} catch (final InterruptedException e) {
			handler.post(new Runnable() {
				public void run() {
					connectionListener.reconnectionFailed(e);
				}
			});
		}
	}

	private int waiting() {
		if (waiting > 25) {
			return 300;
		}
		if (waiting > 16) {
			return 120;
		}
		if (waiting > 8) {
			return 60;
		}
		return waiting <= 4 ? 10 : 30;
	}

}
