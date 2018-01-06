package com.fengda.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 确认订单对象
 */
public class OrderCartBase implements Serializable {
    private Address addr_info;

    private List<GoodsInfo> goods_info;
    private int goods_num;
    private String sun_consumption;
    private UserInfo user_info;
    private PayTypeControl pay_auth;


    public Address getAddr_info() {
        return addr_info;
    }

    public List<GoodsInfo> getGoods_info() {
        return goods_info;
    }

    public int getGoods_num() {
        return goods_num;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public String getSun_consumption() {
        return sun_consumption;
    }

    public PayTypeControl getPay_auth() {
        return pay_auth;
    }
}
