package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by yuguang on 2017/10/21.
 */

public class PayTypeControl implements Serializable{
    private String alipay_auth;//支付宝支付开关 1开启 0关闭
    private String wx_auth;//微信支付开关 1开启 0关闭
    private String card_pay;//银联支付开关 1开启 0关闭
    private String pay_max;// 拉卡拉支付开关 1开启 0关闭(拉卡拉)
    private String consumption_auth;//消费果开关（0关闭消费果抵扣开关）
    public String getAlipay_auth() {
        return alipay_auth;
    }

    public void setAlipay_auth(String alipay_auth) {
        this.alipay_auth = alipay_auth;
    }

    public String getWx_auth() {
        return wx_auth;
    }

    public void setWx_auth(String wx_auth) {
        this.wx_auth = wx_auth;
    }

    public String getCard_pay() {
        return card_pay;
    }

    public void setCard_pay(String card_pay) {
        this.card_pay = card_pay;
    }

    public String getPay_max() {
        return pay_max;
    }

    public void setPay_max(String pay_max) {
        this.pay_max = pay_max;
    }

    public String getConsumption_auth() {
        return consumption_auth;
    }

    public void setConsumption_auth(String consumption_auth) {
        this.consumption_auth = consumption_auth;
    }
}
