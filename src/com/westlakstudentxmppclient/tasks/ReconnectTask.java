package com.westlakstudentxmppclient.tasks;

import org.jivesoftware.smack.ConnectionListener;

import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

import android.util.Log;

/**
 *
 * ReconnectTask
 * @author chendong
 * 2014年10月30日 下午2:04:00
 * @version 1.0.0
 *
 */
public class ReconnectTask extends Thread {

	private static final String TAG = "ReconnectTask";
	private XmppHandlerManager handler = null;
	private int trying = 0;
	private ConnectionListener connectionListener = null;
	
	public ReconnectTask(XmppHandlerManager handler,ConnectionListener listener){
		this.handler = handler;
		connectionListener = listener;
	}
	
	public void setTrying(int trying){
		this.trying = trying;
	}
	
	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				if (handler.isConnected){
					continue;
				}
				Log.d(TAG, "Trying to reconnect in " + trying() + " seconds");
				Thread.sleep((long) trying() * 1000L);
				handler.trying();
				trying ++;
				Log.d(TAG, "try " + trying + " reconnection");
			}
		} catch (final InterruptedException e) {
			handler.post(new Runnable() {
				public void run() {
					connectionListener.reconnectionFailed(e);
				}
			});
		}
		
	}

	private int trying() {
		if (trying > 25) {
			return 300;
		}
		if (trying > 16) {
			return 120;
		}
		if (trying > 8) {
			return 60;
		}
		return trying <= 4 ? 10 : 30;
	}
	
}
