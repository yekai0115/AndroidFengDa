package com.fengda.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class Userinfo implements Serializable {
    private String legal_name;

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }
}
