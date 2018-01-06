package com.fengda.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 订单详情对象
 */
public class OrderDetals implements Serializable {
    private Address addr_info;
    private GoodsOrderInfo goods_info;
    private PayData payData;
    private PayTypeControl auth;
    private List<LogisticsBase> deliver_list;//物流列表
    public PayTypeControl getAuth() {
        return auth;
    }

    public Address getAddr_info() {
        return addr_info;
    }

    public GoodsOrderInfo getGoods_info() {
        return goods_info;
    }

    public PayData getPayData() {
        return payData;
    }

    public List<LogisticsBase> getDeliver_list() {
        return deliver_list;
    }
}
