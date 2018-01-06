package com.fengda.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24448 on 2017/12/21.
 */

public class AssemblingBase implements Serializable{

    private String fronturl;
    private List<AssemblingPlan> activities;

    public String getFronturl() {
        return fronturl;
    }

    public void setFronturl(String fronturl) {
        this.fronturl = fronturl;
    }

    public List<AssemblingPlan> getActivities() {
        return activities;
    }

    public void setActivities(List<AssemblingPlan> activities) {
        this.activities = activities;
    }
}
