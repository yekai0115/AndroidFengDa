package com.fengda.app.bean;

import java.io.Serializable;

public class OrderGoods implements Serializable{


	private String goods_attr_id;
	private String number;

	public OrderGoods(String goods_attr_id, String number) {
		this.goods_attr_id = goods_attr_id;
		this.number = number;
	}

	public String getGoods_attr_id() {
		return goods_attr_id;
	}

	public void setGoods_attr_id(String goods_attr_id) {
		this.goods_attr_id = goods_attr_id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "OrderGoods{" +
				"goods_attr_id='" + goods_attr_id + '\'' +
				", number='" + number + '\'' +
				'}';
	}
}
