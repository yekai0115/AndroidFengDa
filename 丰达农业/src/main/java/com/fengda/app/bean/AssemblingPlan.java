package com.fengda.app.bean;

import java.io.Serializable;

/**
 * Created by 24448 on 2017/12/21.
 */

public class AssemblingPlan implements Serializable{

    private  String id;
    private  String name;
    private  String logo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
