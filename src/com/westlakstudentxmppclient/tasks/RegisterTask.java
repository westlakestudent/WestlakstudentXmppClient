package com.westlakstudentxmppclient.tasks;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.westlakstudentxmppclient.config.Constants;
import com.westlakstudentxmppclient.xmpp.XmppHandlerManager;

/**
 * 
 * RegisterTask
 * 
 * @author chendong 2014年10月30日 上午11:03:02
 * @version 1.0.0
 * 
 */
public class RegisterTask implements Runnable {

	private static final String TAG = "RegisterTask";
	private XMPPConnection connection = null;
	private String username = null;
	private String password = null;
	private String imei = null;
	private XmppHandlerManager mHandler = null;
	private SharedPreferences sharePref = null;

	public RegisterTask(XmppHandlerManager handler, String username,
			String password, SharedPreferences sharedpref,XMPPConnection connection) {
		mHandler = handler;
		this.username = username;
		this.password = password;
		this.connection = connection;
		sharePref = sharedpref;
		
		imei = sharePref.getString(Constants.IMEI, "A00000000F");
	}

	private boolean isConnected(){
		return connection != null && connection.isConnected();
	}
	
	@Override
	public void run() {
		if (isConnected()) {
			Registration registration = new Registration();
			PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
					registration.getPacketID()), new PacketTypeFilter(
					IQ.class));
			connection.addPacketListener(packetListener, packetFilter);
			registration.setType(IQ.Type.SET);
			registration.addAttribute("username", username);
			registration.addAttribute("password", password);
			registration.addAttribute("imei", imei);
			connection.sendPacket(registration);
		}
	}

	private PacketListener packetListener = new PacketListener() {

		public void processPacket(Packet packet) {
			Log.d("RegisterTask.PacketListener", "processPacket().....");
			Log.d("RegisterTask.PacketListener", "packet=" + packet.toXML());
			mHandler.sendEmptyMessage(XmppHandlerManager.DIALOG_DISMISS);
			if (packet instanceof IQ) {
				IQ response = (IQ) packet;
				if (response.getType() == IQ.Type.ERROR) {
					if (!response.getError().toString().contains("409")) {
						// 用户名重复
						Log.e(TAG,
								"Unknown error while registering XMPP account! "
										+ response.getError().getCondition());
						mHandler.sendEmptyMessage(XmppHandlerManager.REGISTER_FAILRE);
					}else{
						mHandler.sendEmptyMessage(XmppHandlerManager.DUPLICATE_IMEI);
					}
				} else if (response.getType() == IQ.Type.RESULT) {
					Log.d(TAG, "username = " + username);
					Log.d(TAG, "password = " + password);
					Editor editor = sharePref.edit();
					editor.putString(Constants.USERNAME, username);
					editor.putString(Constants.PASSWORD, password);
					editor.commit();
					Log.i(TAG, "Account registered successfully");
					mHandler.sendEmptyMessage(XmppHandlerManager.REGISTERE_SUCCESSFULLY);
				}
			}
		}
	};

}
