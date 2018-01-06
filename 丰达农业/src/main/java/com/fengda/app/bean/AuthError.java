package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by yuguang on 2017/10/21.
 */

public class AuthError implements Serializable{

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
    private String remarks;
    private String province;
    private String city;
    private String county;


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

    public String getRemarks() {
        return remarks;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }
}
