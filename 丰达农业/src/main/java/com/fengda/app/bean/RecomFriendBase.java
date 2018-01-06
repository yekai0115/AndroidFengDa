package com.fengda.app.bean;

import java.util.List;

/**
 * Created by yuguang on 2017/10/21.
 */

public class RecomFriendBase {


    private int refreNum;
    private String headurl;
    private List<RecomFriend>  items;

    public int getRefreNum() {
        return refreNum;
    }

    public List<RecomFriend> getItems() {
        return items;
    }

    public String getHeadurl() {
        return headurl;
    }
}
