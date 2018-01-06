package com.fengda.app.bean;

import com.fengda.app.model.GoodsBean;

import java.util.List;

/**
 * 首页banner、活动图片
 */

public class BannerBase {

    private List<BannerBean> banner;
    private List<BannerBean> activities;
    private List<BannerBean> category;
    private List<GoodsBean> goods;
    private List<BannerBean> banner2;
    private String fronturl;

    public List<BannerBean> getBanner2() {
        return banner2;
    }

    public void setBanner2(List<BannerBean> banner2) {
        this.banner2 = banner2;
    }

    public List<BannerBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
    }

    public List<BannerBean> getActivities() {
        return activities;
    }

    public void setActivities(List<BannerBean> activities) {
        this.activities = activities;
    }

    public List<BannerBean> getCategory() {
        return category;
    }

    public void setCategory(List<BannerBean> category) {
        this.category = category;
    }

    public List<GoodsBean> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsBean> goods) {
        this.goods = goods;
    }

    public String getFronturl() {
        return fronturl;
    }

    public void setFronturl(String fronturl) {
        this.fronturl = fronturl;
    }
}
