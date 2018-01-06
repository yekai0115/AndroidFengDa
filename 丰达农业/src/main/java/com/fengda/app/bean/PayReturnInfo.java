package com.fengda.app.bean;

/**
 * Created by 24448 on 2017/12/25.
 */

public class PayReturnInfo {
    //支付宝
    private  String  zhifubao;

    //微信
    private String packageValue;//扩展字段
    private String appid;//应用APPID
    private String partnerid;//商户号
    private String  sign;//签名
    private String prepayid;//预支付交易会话标识
    private String noncestr;//随机字符串
    private String timestamp;//时间戳
    private String trade_type;//交易类型

    //银联
    private  String  yinlian;


    public String getZhifubao() {
        return zhifubao;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public String getAppid() {
        return appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getSign() {
        return sign;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public String getYinlian() {
        return yinlian;
    }
}
