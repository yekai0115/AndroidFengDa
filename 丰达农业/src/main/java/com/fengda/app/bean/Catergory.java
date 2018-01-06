package com.fengda.app.bean;

import java.io.Serializable;

/**
 * 商品分类对象:缓存需序列化
 * @author Administrator
 *
 */
public class Catergory implements Serializable{


	private String cid;
	private String name;

	public String getCid() {
		return cid;
	}

	public String getName() {
		return name;
	}
}
