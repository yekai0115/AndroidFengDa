package com.fengda.app.bean;

public class CartGoodsBase {

	private String id;
	private String goods_attr_id;
	private String goods_logo;
	private String gname;
	private String attr_value;
	private String attr_point;
	private String attr_price;
	private String price;
	private String goods_number;
	private boolean isChecked;
	private String number;
	private String goods_id;

	public String getGoods_id() {
		return goods_id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	/**选项框是否可见*/
	private Boolean isVisibility=false;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoods_attr_id() {
		return goods_attr_id;
	}

	public void setGoods_attr_id(String goods_attr_id) {
		this.goods_attr_id = goods_attr_id;
	}

	public String getGoods_logo() {
		return goods_logo;
	}

	public void setGoods_logo(String goods_logo) {
		this.goods_logo = goods_logo;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public String getAttr_value() {
		return attr_value;
	}

	public void setAttr_value(String attr_value) {
		this.attr_value = attr_value;
	}

	public String getAttr_point() {
		return attr_point;
	}

	public void setAttr_point(String attr_point) {
		this.attr_point = attr_point;
	}

	public String getAttr_price() {
		return attr_price;
	}

	public void setAttr_price(String attr_price) {
		this.attr_price = attr_price;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getGoods_number() {
		return goods_number;
	}

	public void setGoods_number(String goods_number) {
		this.goods_number = goods_number;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public Boolean getVisibility() {
		return isVisibility;
	}

	public void setVisibility(Boolean visibility) {
		isVisibility = visibility;
	}
}
