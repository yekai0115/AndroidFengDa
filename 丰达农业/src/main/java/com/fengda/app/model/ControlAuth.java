package com.fengda.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class ControlAuth implements Serializable {
    private String recast_auth;//复投开关（0关闭；1开启）
    private String return_auth;//产果开关（0关闭；1开启）
    private String jiyoumei_auth;//吉优美开关（0关闭；1开启）
    private String transfer_auth;//转让开关（0关闭；1开启）
    private String consumption_auth;//消费果开关（0关闭；1开启）

    public String getRecast_auth() {
        return recast_auth;
    }

    public void setRecast_auth(String recast_auth) {
        this.recast_auth = recast_auth;
    }

    public String getReturn_auth() {
        return return_auth;
    }

    public void setReturn_auth(String return_auth) {
        this.return_auth = return_auth;
    }

    public String getJiyoumei_auth() {
        return jiyoumei_auth;
    }

    public void setJiyoumei_auth(String jiyoumei_auth) {
        this.jiyoumei_auth = jiyoumei_auth;
    }

    public String getTransfer_auth() {
        return transfer_auth;
    }

    public void setTransfer_auth(String transfer_auth) {
        this.transfer_auth = transfer_auth;
    }

    public String getConsumption_auth() {
        return consumption_auth;
    }

    public void setConsumption_auth(String consumption_auth) {
        this.consumption_auth = consumption_auth;
    }
}
