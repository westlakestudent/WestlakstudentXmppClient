package com.westlakstudentxmppclient.tasks;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.notification.NotificationIQ;
import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

/**
 * 
 * LoginTask
 * 
 * @author chendong 2014年10月30日 下午2:38:31
 * @version 1.0.0
 * 
 */
public class LoginTask implements Runnable {

	private static final String TAG = "LoginTask";
	private static final String XMPP_RESOURCE_NAME = "WESTLAKESTUDENT";
	private ConnectionListener connListener = null;
	private XMPPConnection connection = null;
	private XmppHandlerManager mHandler = null;
	private String username = null;
	private String password = null;
	private Context context = null;

	public LoginTask(ConnectionListener connectionListener,
			XMPPConnection connection, XmppHandlerManager handler,
			String username, String password,Context context) {
		this.connListener = connectionListener;
		this.connection = connection;
		this.username = username;
		this.password = password;
		this.context = context;
		mHandler = handler;

	}

	private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	@Override
	public void run() {

		if (!isAuthenticated()) {
			try {
				connection.login(username, password, XMPP_RESOURCE_NAME);
				Log.i(TAG, "Login successfully! " + "username :" + username + ";password:" + password);
				

				if (connListener != null) {
					connection.addConnectionListener(connListener);
				}

				PacketFilter packetFilter = new PacketTypeFilter(
						NotificationIQ.class);
				connection.addPacketListener(NotificationPacketListener,
						packetFilter);
				mHandler.sendEmptyMessage(XmppHandlerManager.LOGIN_SUCCESSFULLY);
			} catch (XMPPException e) {
				Log.e(TAG, "LoginTask.run()... xmpp error");
				Log.e(TAG,
						"Failed to login to xmpp server. Caused by: "
								+ e.getMessage());
				// 未验证通过的即没有注册的   
				String INVALID_CREDENTIALS_ERROR_CODE = "401";  
				String errorMessage = e.getMessage();
				if (errorMessage != null
						&& errorMessage
								.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
					mHandler.sendEmptyMessage(XmppHandlerManager.NO_REGISTER);
					return;
				}
				//405not_allowed 一个账号只允许一台设备在线
				INVALID_CREDENTIALS_ERROR_CODE = "405";
				if (errorMessage != null
						&& errorMessage
								.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
					mHandler.sendEmptyMessage(XmppHandlerManager.ALREADY_ONLINE);
					return;
				}
				
				INVALID_CREDENTIALS_ERROR_CODE = "403";
				if (errorMessage != null
						&& errorMessage
								.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
					mHandler.sendEmptyMessage(XmppHandlerManager.PASSWORD_ERROR);
					return;
				}
				
				INVALID_CREDENTIALS_ERROR_CODE = "407";
				if (errorMessage != null
						&& errorMessage
								.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
					mHandler.sendEmptyMessage(XmppHandlerManager.USERNAME_ERROR);
					return;
				}
				
				Message msg = mHandler.obtainMessage();
				msg.what = XmppHandlerManager.LOGIN_ERROR;
				msg.obj = errorMessage;
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				Log.e(TAG, "LoginTask.run()... other error");
				Log.e(TAG,
						"Failed to login to xmpp server. Caused by: "
								+ e.getMessage());
				Message msg = mHandler.obtainMessage();
				msg.what = XmppHandlerManager.LOGIN_ERROR;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}
		} else {
			Log.i(TAG, "Logined in already");

		}

	}

	
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

	
	
}
