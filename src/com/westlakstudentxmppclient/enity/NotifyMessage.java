package com.westlakstudentxmppclient.enity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * NotifyMessage
 * @author chendong
 * 2014年10月31日 下午1:56:07
 * @version 1.0.0
 *
 */
public class NotifyMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String message;
	private Date receiveTime = new Date();
	private String remark;
	
	public NotifyMessage(String title,String message,String remark){
		this.title = title;
		this.message = message;
		this.remark = remark;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
