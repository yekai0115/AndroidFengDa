package com.fengda.app.eventbus;
/**
 *主页发送给个人页面；
 * 修改昵称发送给个人页面
 */
public class ToGeRenMsgEvent {
	private String message;
	private String title;
	private int tage;
	public ToGeRenMsgEvent() {
		super();
	}
	
	public ToGeRenMsgEvent(int tage) {
		super();
		this.tage = tage;
	}

	public ToGeRenMsgEvent(String message, String title) {
		super();
		this.message = message;
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTage() {
		return tage;
	}
	public void setTage(int tage) {
		this.tage = tage;
	}
	
	

	
	
	
	
}
