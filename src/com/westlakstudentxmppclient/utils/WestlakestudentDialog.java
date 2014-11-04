package com.westlakstudentxmppclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 *
 * WestlakestudentDialog
 * @author chendong
 * 2014年10月30日 上午10:01:35
 * @version 1.0.0
 *
 */
public class WestlakestudentDialog {

	public static void show(Context context,String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示");
		builder.setMessage(msg);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确定", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	} 
	
	public static void showWithListener(Context context,String msg,OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示");
		builder.setMessage(msg);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确定", listener);
		AlertDialog dialog = builder.create();
		dialog.show();
	} 
}
