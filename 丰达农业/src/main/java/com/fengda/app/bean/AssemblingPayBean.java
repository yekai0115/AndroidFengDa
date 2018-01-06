package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by yuguang on 2017/10/21.
 */

public class AssemblingPayBean implements Serializable{
    private String money_need;//
    private PayTypeControl pay_auth;//1开启 0关闭


    public String getMoney_need() {
        return money_need;
    }

    public void setMoney_need(String money_need) {
        this.money_need = money_need;
    }

    public PayTypeControl getPay_auth() {
        return pay_auth;
    }

    public void setPay_auth(PayTypeControl pay_auth) {
        this.pay_auth = pay_auth;
    }
}
