package com.fengda.app.model;

import com.fengda.app.bean.BindCard;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class BandCardBase implements Serializable {
    private List<BindCard> banklist;
    private String token;
    private Userinfo userinfo;

    public List<BindCard> getBanklist() {
        return banklist;
    }

    public void setBanklist(List<BindCard> banklist) {
        this.banklist = banklist;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Userinfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(Userinfo userinfo) {
        this.userinfo = userinfo;
    }
}
