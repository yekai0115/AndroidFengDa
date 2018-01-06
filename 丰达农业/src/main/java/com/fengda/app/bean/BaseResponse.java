package com.fengda.app.bean;

import java.io.Serializable;

/**
 * 网络返回基类 支持泛型
 *
 * @param <T>
 * @author yg
 */
public class BaseResponse<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6453824795394683688L;

    private int code;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
