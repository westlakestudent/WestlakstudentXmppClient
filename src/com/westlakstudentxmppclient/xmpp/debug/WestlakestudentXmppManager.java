package com.westlakstudentxmppclient.xmpp.debug;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.XMPPException;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.notification.NotificationIQ;
import com.westlakstudentxmppclient.notification.NotificationIQProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * 暂时没有用到此类，已用XmppHandlerManager代替
 * WestlakestudentXmppManager
 * 
 * @author westlakestudent 2014年10月27日 下午4:48:24
 * @version 1.0.0
 * 
 */
public class WestlakestudentXmppManager {

	private static final String TAG = "WestlakestudentXmppManager";
//	private static final String XMPP_RESOURCE_NAME = "WESTLAKESTUDENT";

	private Context context = null;

	private String xmppHost = null;

	private int xmppPort = 0;

	private XMPPConnection connection = null;

	private SharedPreferences sharePref = null;

	private ExecutorService executorService = null;
	
	private ReconnectionThread reconnection = null;

	public WestlakestudentXmppManager(Context context) {
		this.context = context;
		sharePref = context.getSharedPreferences(Constants.PREF_NAME,
				Context.MODE_PRIVATE);
		executorService = Executors.newSingleThreadExecutor();

//		name = sharePref.getString(Constants.USERNAME, null);
//		password = sharePref.getString(Constants.PASSWORD, null);

		xmppHost = sharePref.getString(Constants.XMPPHOST, "192.168.8.192");
		xmppPort = sharePref.getInt(Constants.XMPPPORT, 8090);
		
		reconnection = new ReconnectionThread(this, handler, connectionListener);
	}

	private static Handler handler = new Handler();
	
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
			startReconnectionThread();
		}

		@Override
		public void connectionClosed() {
			Log.i(TAG, "connectionClosed");
		}
	};

	
	
	private PacketListener NotificationPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {

			Log.d(TAG, "NotificationPacketListener.processPacket()...");
			Log.d(TAG, "packet.toXML()=" + packet.toXML());

			if (packet instanceof NotificationIQ) {
				NotificationIQ notification = (NotificationIQ) packet;

				if (notification.getChildElementXML().contains(
						"androidpn:iq:notification")) {
					String notificationId = notification.getId();
					String notificationImei = notification.getImei();
					String notificationTitle = notification.getTitle();
					String notificationMessage = notification.getMessage();
					String notificationRemark = notification.getRemark();

					Intent intent = new Intent(
							Constants.ACTION_SHOW_NOTIFICATION);
					intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
					intent.putExtra(Constants.NOTIFICATION_IMEI,
							notificationImei);
					intent.putExtra(Constants.NOTIFICATION_TITLE,
							notificationTitle);
					intent.putExtra(Constants.NOTIFICATION_MESSAGE,
							notificationMessage);
					intent.putExtra(Constants.NOTIFICATION_REMARK,
							notificationRemark);

					context.sendBroadcast(intent);
				}
			}

		}
	};

	
	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	/*private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}*/

/*	private boolean isRegistered() {
		return sharePref.getBoolean(Constants.USER_REGISTERED, false);
	}*/

	private void startReconnectionThread() {
    	Log.d(TAG, "startReconnectionThread");
        synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
                Log.d(TAG, "reconnection start");
            }
        }
    }
	
	
	public void start() {
		Log.d(TAG, "XmppManager starting");
		startTask(new ConnectTask());
	}
	
	public void stop(){
		if(isConnected()){
			Log.d(TAG, "XmppManager stopping");
			connection.removePacketListener(NotificationPacketListener);
			connection.disconnect();
		}
		executorService.shutdown();
	}

	private synchronized void startTask(Runnable task) {
		Log.i(TAG, "startTask " + task.getClass().getName());
		executorService.submit(task);
	}

	private class ConnectTask implements Runnable {

		@Override
		public void run() {
			Log.d(TAG, "ConnectTask running...");

			if (!isConnected()) {
				ConnectionConfiguration config = new ConnectionConfiguration(
						xmppHost, xmppPort);

				config.setSecurityMode(SecurityMode.disabled);
				config.setSASLAuthenticationEnabled(false);
				config.setCompressionEnabled(false);
				connection = new XMPPConnection(config);

				try {
					connection.connect();
					Log.i(TAG, "Connect successfully!");
					
					if(reconnection != null)
						reconnection.setWaiting(0);
					
					ProviderManager.getInstance().addIQProvider("notification",
							"androidpn:iq:notification",
							new NotificationIQProvider());
				} catch (XMPPException e) {
					Log.e(TAG, "Connect failed!" + e);
					startReconnectionThread();
					return;
				}
			} else {
				Log.i(TAG, "XMPP connected already");
			}
		}

	}


/*
	private class LoginTask implements Runnable {

		@Override
		public void run() {
			Log.i(TAG, "LoginTask running");

			if (!isAuthenticated()) {
				try {
					connection.login("", "",XMPP_RESOURCE_NAME);
					Log.i(TAG, "Login successfully");

					if (connectionListener != null) {
						connection.addConnectionListener(connectionListener);
					}

					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					connection.addPacketListener(NotificationPacketListener,
							packetFilter);

				} catch (XMPPException e) {
					Log.e(TAG, "LoginTask.run()... xmpp error");
					Log.e(TAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";// 未验证通过的即没有注册成功的
					String errorMessage = e.getMessage();
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						return;
					}
					startReconnectionThread();
				} catch (Exception e) {
					Log.e(TAG, "LoginTask.run()... other error");
					Log.e(TAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					startReconnectionThread();
				}
			}else{
				 Log.i(TAG, "Logined in already");
				 
			}
		}

	}*/
}
