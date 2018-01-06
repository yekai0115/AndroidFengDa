package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/28.
 */

public class PayData {

    private int delivery_status;//订单状态
    private String order_sn;
    private String pay_return_id;
    private String addtime;
    private String paytime;
    private String update_time;
    private String delivery_sn;
    private String buildtime;


    public String getBuildtime() {
        return buildtime;
    }

    public String getDelivery_sn() {
        return delivery_sn;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public String getPay_return_id() {
        return pay_return_id;
    }

    public String getAdd_time() {
        return addtime;
    }

    public String getPaytime() {
        return paytime;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public int getDelivery_status() {
        return delivery_status;
    }
}
