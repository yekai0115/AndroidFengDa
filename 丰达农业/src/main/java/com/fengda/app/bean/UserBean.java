package com.fengda.app.bean;

import com.fengda.app.model.ControlAuth;

/**
 * Created by yuguang on 2017/10/21.
 */

public class UserBean {

    private String mobile;//手机号码
    private String nick;
    private String name;
    private String sale;//
    private String rank;//会员等级
    private int verified;//会员实名认证0未提交,1认证失败,2审核中,3认证通过
    private String pwd;//
    private String sum_self;//待产果
    private String sum_return;//产果
    private String sum_jiyoumie;//消费果
    private String sun_consumption;//吉优美
    private String remarks;//认证状态
    private String headurl;//前缀
    private String head;//后缀
    private int derail;
    private String pay_num;
    private String delivered_num;
    private String received_num;
    private String done_num;
    private String team_auth;//团队开关（0关闭；1开启）
    private String declaration_auth;//报单开关（0关闭；1开启）
    private ControlAuth control_auth;//开关
    private String level_name;
    private String level_id;
    private String msg_num;

    public String getMsg_num() {
        return msg_num;
    }

    public void setMsg_num(String msg_num) {
        this.msg_num = msg_num;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    public ControlAuth getControl_auth() {
        return control_auth;
    }

    public void setControl_auth(ControlAuth control_auth) {
        this.control_auth = control_auth;
    }

    public String getTeam_auth() {
        return team_auth;
    }

    public void setTeam_auth(String team_auth) {
        this.team_auth = team_auth;
    }

    public String getDeclaration_auth() {
        return declaration_auth;
    }

    public void setDeclaration_auth(String declaration_auth) {
        this.declaration_auth = declaration_auth;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSum_self() {
        return sum_self;
    }

    public void setSum_self(String sum_self) {
        this.sum_self = sum_self;
    }

    public String getSum_return() {
        return sum_return;
    }

    public void setSum_return(String sum_return) {
        this.sum_return = sum_return;
    }

    public String getSum_jiyoumie() {
        return sum_jiyoumie;
    }

    public void setSum_jiyoumie(String sum_jiyoumie) {
        this.sum_jiyoumie = sum_jiyoumie;
    }

    public String getSun_consumption() {
        return sun_consumption;
    }

    public void setSun_consumption(String sun_consumption) {
        this.sun_consumption = sun_consumption;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getDerail() {
        return derail;
    }

    public void setDerail(int derail) {
        this.derail = derail;
    }

    public String getPay_num() {
        return pay_num;
    }

    public void setPay_num(String pay_num) {
        this.pay_num = pay_num;
    }

    public String getDelivered_num() {
        return delivered_num;
    }

    public void setDelivered_num(String delivered_num) {
        this.delivered_num = delivered_num;
    }

    public String getReceived_num() {
        return received_num;
    }

    public void setReceived_num(String received_num) {
        this.received_num = received_num;
    }

    public String getDone_num() {
        return done_num;
    }

    public void setDone_num(String done_num) {
        this.done_num = done_num;
    }
}
