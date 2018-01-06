package com.fengda.app.bean;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CartGoods extends Observable implements Observer {


	/**该商品在购物车的ID*/
	private long cart_goods_id;
	/**商品ID*/
	private long goods_id;
	/**该规格商品购买数量*/
	private int number;
	/**总价格*/
	private String total_price;
	/**商品图片*/
	private String goods_thumb;
	/**商品名称*/
	private String goods_name;
	/**让利*/
	private String profit;
	/**规格名称*/
	private String attr_value;
	/*该规格商品库存*/
	private int attr_number;
	/**单价*/
	private String attr_price;
	/**该商品所有规格*/
	private List<Specifications> attr_list;

	private boolean isSelect;
	private boolean isEidt;

	public long getCart_goods_id() {
		return cart_goods_id;
	}

	public void setCart_goods_id(long cart_goods_id) {
		this.cart_goods_id = cart_goods_id;
	}

	public long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(long goods_id) {
		this.goods_id = goods_id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}

	public String getGoods_thumb() {
		return goods_thumb;
	}

	public void setGoods_thumb(String goods_thumb) {
		this.goods_thumb = goods_thumb;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getAttr_value() {
		return attr_value;
	}

	public void setAttr_value(String attr_value) {
		this.attr_value = attr_value;
	}

	public int getAttr_number() {
		return attr_number;
	}

	public void setAttr_number(int attr_number) {
		this.attr_number = attr_number;
	}

	public String getAttr_price() {
		return attr_price;
	}

	public void setAttr_price(String attr_price) {
		this.attr_price = attr_price;
	}

	public List<Specifications> getAttr_list() {
		return attr_list;
	}

	public void setAttr_list(List<Specifications> attr_list) {
		this.attr_list = attr_list;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}


	public boolean isEidt() {
		return isEidt;
	}

	public void setEidt(boolean eidt) {
		isEidt = eidt;
	}

	public void changeChecked() {
		isSelect = !isSelect;
		setChanged();
		notifyObservers();
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof Boolean) {
			this.isSelect = (Boolean) data;
		}

	}

}
