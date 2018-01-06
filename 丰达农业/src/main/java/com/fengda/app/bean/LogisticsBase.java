package com.fengda.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 物流
 *
 * @author yg
 */
public class LogisticsBase implements Serializable {

    private String number;
    private String type;
    private String deliverystatus;
    private String issign;
    private String time;
    private String status;
    private List<Logistics> list;

    public LogisticsBase(String deliverystatus, String time, String status) {
        this.deliverystatus = deliverystatus;
        this.time = time;
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getDeliverystatus() {
        return deliverystatus;
    }

    public String getIssign() {
        return issign;
    }

    public List<Logistics> getList() {
        return list;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }


}
