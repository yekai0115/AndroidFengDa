package com.fengda.app.bean;

import java.io.Serializable;

/**
 * 智品订单详情对象
 */
public class WisdomOrderDetals implements Serializable {


    private String id;
    private String order_sn;
    private String subtotal;
    private String buynum;
    private String totalprice;
    private String postage;
    private String buildtime;
    private String addr_id;
    private String addr_name;
    private String addr_mobile;
    private String addr_detail;
    private String deli_id;
    private int deli_status;
    private String deli_time;
    private String deli_update_time;
    private String pay_time;
    private String goods_info;
    private String province;
    private String city;
    private String county;
    private String pay_return_id;

    public String getId() {
        return id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getBuynum() {
        return buynum;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public String getPostage() {
        return postage;
    }

    public String getBuildtime() {
        return buildtime;
    }

    public String getAddr_id() {
        return addr_id;
    }

    public String getAddr_name() {
        return addr_name;
    }

    public String getAddr_mobile() {
        return addr_mobile;
    }

    public String getAddr_detail() {
        return addr_detail;
    }

    public String getDeli_id() {
        return deli_id;
    }

    public int getDeli_status() {
        return deli_status;
    }

    public String getDeli_time() {
        return deli_time;
    }

    public String getDeli_update_time() {
        return deli_update_time;
    }

    public String getPay_time() {
        return pay_time;
    }

    public String getGoods_info() {
        return goods_info;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getPay_return_id() {
        return pay_return_id;
    }
}
