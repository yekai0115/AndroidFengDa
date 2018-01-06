package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/21.
 */

public class MenuBean {

    private String text;//名称
    private String auth;//开关
    private int logoDrable;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLogoDrable() {
        return logoDrable;
    }

    public void setLogoDrable(int logoDrable) {
        this.logoDrable = logoDrable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
