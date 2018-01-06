package com.fengda.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类对象:缓存需序列化
 * @author Administrator
 *
 */
public class ParentCatergory implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 6280688171816420071L;

	private String title;
	private List<Catergory> catelist;//父分类下的字分类列表


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getTitle() {
		return title;
	}

	public List<Catergory> getCatelist() {
		return catelist;
	}
}
