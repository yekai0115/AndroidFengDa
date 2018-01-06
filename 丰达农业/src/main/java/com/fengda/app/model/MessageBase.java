package com.fengda.app.model;

import java.util.List;

/**
 * Created by Administrator on 2017/7/3 0003.
 */

public class MessageBase {
    private String pic_uri;
    private List<Message> msg_list;

    public String getPic_uri() {
        return pic_uri;
    }

    public void setPic_uri(String pic_uri) {
        this.pic_uri = pic_uri;
    }

    public List<Message> getMsg_list() {
        return msg_list;
    }

    public void setMsg_list(List<Message> msg_list) {
        this.msg_list = msg_list;
    }
}
