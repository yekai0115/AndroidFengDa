package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by yuguang on 2017/10/28.
 */

public class OrderGoodsInfo implements Serializable{
    private String gname;
    private String  pic;
    private String  attr_value;
    private String  price;
    private int  number;
    private String attr_point;
    private String attr_price;
    private String return_point;
    public String getReturn_point() {
        return return_point;
    }
    public String getAttr_price() {
        return attr_price;
    }

    public String getGname() {
        return gname;
    }

    public String getPic() {
        return pic;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public String getPrice() {
        return price;
    }

    public int getNumber() {
        return number;
    }

    public String getAttr_point() {
        return attr_point;
    }
}
