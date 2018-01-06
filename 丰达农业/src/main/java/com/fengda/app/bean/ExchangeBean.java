package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/21.
 */

public class ExchangeBean {
    private String bank_card;
    private String bankname;
    private String status;
    private String mid;
    private String add_time;
    private String money;
    private String type;
    private String remark;
    private String mobile;
    private String fees;
    private String num;
    private String sendee_mobile;

    public String getSendee_mobile() {
        return sendee_mobile;
    }

    public String getFees() {
        return fees;
    }

    public String getNum() {
        return num;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
