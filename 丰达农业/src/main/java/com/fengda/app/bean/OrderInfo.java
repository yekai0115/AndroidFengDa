package com.fengda.app.bean;

import java.util.List;

/**
 * Created by yuguang on 2017/10/28.
 */

public class OrderInfo {

    private String order_sn;
    private String postage;
    private String totalprice;
    private String nocan_points;
    private String delivery_sn;
    private int status;
    private String goods_info;
    private String add_time;
    private String remark;

    public String getOrder_sn() {
        return order_sn;
    }

    public int getStatus() {
        return status;
    }

    public String getGoods_info() {
        return goods_info;
    }

    public String getPostage() {
        return postage;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public String getNocan_points() {
        return nocan_points;
    }

    public String getDelivery_sn() {
        return delivery_sn;
    }

    public String getAdd_time() {
        return add_time;
    }

    public String getRemark() {
        return remark;
    }

}
