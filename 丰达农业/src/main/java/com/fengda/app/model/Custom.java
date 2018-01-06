package com.fengda.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class Custom implements Serializable {
    private String user_cate_name;
    private int goods_user_cat_id;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getUser_cate_name() {
        return user_cate_name;
    }

    public void setUser_cate_name(String user_cate_name) {
        this.user_cate_name = user_cate_name;
    }

    public int getGoods_user_cat_id() {
        return goods_user_cat_id;
    }

    public void setGoods_user_cat_id(int goods_user_cat_id) {
        this.goods_user_cat_id = goods_user_cat_id;
    }
}
