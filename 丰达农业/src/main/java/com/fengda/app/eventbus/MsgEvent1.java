package com.fengda.app.eventbus;
/**
 */
public class MsgEvent1 {
	private String message;
	private String title;
	private int tage;
	public MsgEvent1() {
		super();
	}

	public MsgEvent1(int tage) {
		super();
		this.tage = tage;
	}

	public MsgEvent1(String message, String title) {
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
