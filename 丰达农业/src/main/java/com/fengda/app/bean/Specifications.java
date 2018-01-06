package com.fengda.app.bean;

/**
 * 商品规格
 */

public class Specifications {

    /**
     * 规格ID
     */
    private String goods_attr_id;
    /**
     * 规格名称
     */
    private String attr_value;
    /**
     * 该规格价格
     */
    private String attr_price;
    /**
     * 该规格库存
     */
    private int attr_number;
    private String attr_point;
    private String price_cost;//原价
    private Boolean isChecked;
    public String getGoods_attr_id() {
        return goods_attr_id;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public String getAttr_price() {
        return attr_price;
    }

    public int getAttr_number() {
        return attr_number;
    }

    public String getAttr_point() {
        return attr_point;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getPrice_cost() {
        return price_cost;
    }
}
