package com.fengda.app.bean;

import java.io.Serializable;

/**
 * 确认订单对象
 */
public class OrderPay implements Serializable {

    private String trade_id;
    private String pay_money;

    public String getTrade_id() {
        return trade_id;
    }

    public String getPay_money() {
        return pay_money;
    }
}
