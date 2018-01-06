package com.fengda.app.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/***
 * 搜索历史表
 * 
 */
@Table(name="ddz_search_history")
public class SearchHistoryTable {
	@Column(name="id",isId=true,autoGen=true)
    private int id;
	@Column(name="title")
	private String title; // 搜索内容
	@Column(name="searchtime")
	private String searchtime; // 搜索时间

	@Column(name="type")
	private int type;        //商品列别（为了区分是商品还是商店）
	@Column(name="user")
	private String user;//浏览用户
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSearchtime() {
		return searchtime;
	}
	public void setSearchtime(String searchtime) {
		this.searchtime = searchtime;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	
	
	
	
}
