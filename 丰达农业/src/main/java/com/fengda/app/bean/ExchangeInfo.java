package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/21.
 */

public class ExchangeInfo {
    private int status;
    private String sum_return;
    private String sum_jiyoumie;
    private String recast_left;
    private int recast_limit;
    private String bankname;
    private String remarks;
    private String exchange_ratio;
    private String exchange_jiyoumei_ratio;
    private String recast_ratio;

    public String getRecast_ratio() {
        return recast_ratio;
    }

    public String getExchange_ratio() {
        return exchange_ratio;
    }

    public String getExchange_jiyoumei_ratio() {
        return exchange_jiyoumei_ratio;
    }

    public int getRecast_limit() {
        return recast_limit;
    }

    public int getStatus() {
        return status;
    }

    public String getSum_return() {
        return sum_return;
    }

    public String getSum_jiyoumie() {
        return sum_jiyoumie;
    }

    public String getRecast_left() {
        return recast_left;
    }

    public String getBankname() {
        return bankname;
    }

    public String getRemarks() {
        return remarks;
    }

}
