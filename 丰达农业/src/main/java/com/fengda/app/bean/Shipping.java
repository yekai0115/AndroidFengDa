package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/28.
 */

public class Shipping {


    private String shipping_tpl_id;
    private String shipping_name;
    private String price;

    public String getShipping_tpl_id() {
        return shipping_tpl_id;
    }

    public void setShipping_tpl_id(String shipping_tpl_id) {
        this.shipping_tpl_id = shipping_tpl_id;
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
