package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/28.
 */

public class GoodsOrderInfo {

    private String goods_info;

    private String nocan_points;
    private String totalprice;
    private String buildtime;
    private String postage;
    private String order_consumption;
    private String subtotal;

    public String getOrder_consumption() {
        return order_consumption;
    }

    public String getSubtotal() {
        return subtotal;
    }
    public String getGoods_info() {
        return goods_info;
    }

    public void setGoods_info(String goods_info) {
        this.goods_info = goods_info;
    }

    public String getNocan_points() {
        return nocan_points;
    }

    public void setNocan_points(String nocan_points) {
        this.nocan_points = nocan_points;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getBuildtime() {
        return buildtime;
    }

    public void setBuildtime(String buildtime) {
        this.buildtime = buildtime;
    }

    public String getPostage() {
        return postage;
    }

    public void setPostage(String postage) {
        this.postage = postage;
    }
}
