package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by yuguang on 2017/10/21.
 */

public class PersonAuth implements Serializable{

    private int status;
    private String name;
    private String card_number;
    private String hand_logo;
    private String front_card;
    private String rear_card;
    private int bank_id;
    private String bank_card;
    private String branch;
    private String bank_logo;
    private String bankname;
    private String add_time;
    private String pic_uri;
    private AuthError   remarks;
    private String province_id;
    private String city_id;
    private String county_id;
    private String province_name;
    private String city_name;
    private String county_name;

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getHand_logo() {
        return hand_logo;
    }

    public String getFront_card() {
        return front_card;
    }

    public String getRear_card() {
        return rear_card;
    }

    public int getBank_id() {
        return bank_id;
    }

    public String getBank_card() {
        return bank_card;
    }

    public String getBranch() {
        return branch;
    }

    public String getBank_logo() {
        return bank_logo;
    }

    public String getBankname() {
        return bankname;
    }

    public String getAdd_time() {
        return add_time;
    }

    public String getPic_uri() {
        return pic_uri;
    }

    public AuthError getRemarks() {
        return remarks;
    }

    public String getProvince_id() {
        return province_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getCounty_id() {
        return county_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setCounty_id(String county_id) {
        this.county_id = county_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public void setHand_logo(String hand_logo) {
        this.hand_logo = hand_logo;
    }

    public void setFront_card(String front_card) {
        this.front_card = front_card;
    }

    public void setRear_card(String rear_card) {
        this.rear_card = rear_card;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setBank_logo(String bank_logo) {
        this.bank_logo = bank_logo;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public void setPic_uri(String pic_uri) {
        this.pic_uri = pic_uri;
    }

    public void setRemarks(AuthError remarks) {
        this.remarks = remarks;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }
}
