package com.fengda.app.model;

import com.fengda.app.bean.Specifications;

import java.util.List;

/**
 * Created by Administrator on 2017/7/3 0003.
 */

public class GoodsBean {

    private String goods_id;
    private String gname;
    private String goods_logo;
    private String price;
    private String price_cost;//原价
    private String  goods_sold;//销售量
    private String goods_attr;
    private String detail;
    private String goods_number;
    private String status;
    private int goods_type;
    private int collected;
    private String point;
    private int return_point;//1返待产果

    public int getReturn_point() {
        return return_point;
    }

    private List<Specifications> attr_detail;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getCollected() {
        return collected;
    }



    public List<Specifications> getAttr_detail() {
        return attr_detail;
    }

    public void setAttr_detail(List<Specifications> attr_detail) {
        this.attr_detail = attr_detail;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public void setPrice_cost(String price_cost) {
        this.price_cost = price_cost;
    }

    public void setGoods_sold(String goods_sold) {
        this.goods_sold = goods_sold;
    }

    public void setGoods_type(int goods_type) {
        this.goods_type = goods_type;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
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

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoods_attr() {
        return goods_attr;
    }

    public void setGoods_attr(String goods_attr) {
        this.goods_attr = goods_attr;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGoods_sold() {
        return goods_sold;
    }

    public int getGoods_type() {
        return goods_type;
    }

    public String getPrice_cost() {
        return price_cost;
    }
}
