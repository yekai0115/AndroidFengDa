package com.fengda.app.bean;

/**
 * Created by 24448 on 2017/12/1.
 */

public class FavoriteShop {
    private String goods_id;
    private String gname;
    private String goods_logo;
    private String price;
    private boolean isChecked;
    private Boolean isVisibility=false;
    private String point;
    private String return_point;


    public String getReturn_point() {
        return return_point;
    }

    public String getPoint() {
        return point;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public Boolean getVisibility() {
        return isVisibility;
    }

    public void setVisibility(Boolean visibility) {
        isVisibility = visibility;
    }

    public String getGname() {
        return gname;
    }

    public String getGoods_logo() {
        return goods_logo;
    }

    public void setGoods_logo(String goods_logo) {
        this.goods_logo = goods_logo;
    }

    public String getPrice() {
        return price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
