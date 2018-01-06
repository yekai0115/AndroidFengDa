package com.fengda.app.model;

import java.io.Serializable;

/**
 * 智品积分对象
 */

public class IntegralPoint implements Serializable {
    private String point;
    private String type;
    private String remarks;
    private String add_time;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
}
