package com.fengda.app.model;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class Convertibility {

    private String can_money;
    private String bank_id;
    private String bankname;
    private String bank_card;
    private String up_password;

    public String getUp_password() {
        return up_password;
    }

    public void setUp_password(String up_password) {
        this.up_password = up_password;
    }

    public String getCan_money() {
        return can_money;
    }

    public void setCan_money(String can_money) {
        this.can_money = can_money;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }
}
