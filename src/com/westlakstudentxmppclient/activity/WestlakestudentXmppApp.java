package com.westlakstudentxmppclient.activity;

import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

import android.app.Application;

/**
 *
 * WestlakestudentXmppApp
 * @author chendong
 * 2014年11月10日 下午4:08:21
 * @version 1.0.0
 *
 */
public class WestlakestudentXmppApp extends Application {

	
	private XmppHandlerManager xmppHandlerManager = null;
	@Override
	public void onCreate() {
		super.onCreate();
		xmppHandlerManager = XmppHandlerManager.getInstance();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		xmppHandlerManager.close();
	}

	
}
