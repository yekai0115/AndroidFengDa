package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class BindCard implements Serializable {
    private String id;
    private String bankname;
    private boolean isChecked=false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }
}
