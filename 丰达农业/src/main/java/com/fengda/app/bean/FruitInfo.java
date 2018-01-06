package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/21.
 */

public class FruitInfo {


    private String money;//金钱
    private int type;//类型
    private String remark;//备注
    private String add_time;//消费时间
    private int state;
    private int id;
    private int uid;

    private String mobile;

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRemarks(String remarks) {
        this.remark = remarks;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }

    public String getAdd_time() {
        return add_time;
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }
}
