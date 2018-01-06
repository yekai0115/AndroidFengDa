package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/28.
 */

public class GoodsInfo {
    private String goods_id;
    private String attr_value;
    private String attr_price;
    private String attr_point;
    private String attr_number;
    private String goods_logo;
    private String gname;
    private String goods_type;
    private String shipping_tpl_id;
    private String price;
    private Shipping shippingTpl;
    private String return_point;

    private String onepostprice;//单个邮费
    private String  postprice;//小计运费
    private String  goods_price;//小计商品金额
    private int  number;
    private String goods_attr_id;

    public String getReturn_point() {
        return return_point;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public String getAttr_price() {
        return attr_price;
    }

    public String getAttr_point() {
        return attr_point;
    }

    public String getAttr_number() {
        return attr_number;
    }

    public String getGoods_logo() {
        return goods_logo;
    }

    public String getGname() {
        return gname;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public String getShipping_tpl_id() {
        return shipping_tpl_id;
    }

    public String getPostprice() {
        return postprice;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setAttr_number(String attr_number) {
        this.attr_number = attr_number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getGoods_attr_id() {
        return goods_attr_id;
    }

    public String getPrice() {
        return price;
    }

    public Shipping getShippingTpl() {
        return shippingTpl;
    }

    public String getOnepostprice() {
        return onepostprice;
    }
}
