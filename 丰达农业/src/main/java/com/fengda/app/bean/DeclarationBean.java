package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/21.
 */

public class DeclarationBean {
    private String declaration_left;
    private int declaration_auth;
    private int declaration_limit;

    public int getDeclaration_limit() {
        return declaration_limit;
    }

    public void setDeclaration_limit(int declaration_limit) {
        this.declaration_limit = declaration_limit;
    }

    public String getDeclaration_left() {
        return declaration_left;
    }

    public void setDeclaration_left(String declaration_left) {
        this.declaration_left = declaration_left;
    }

    public int getDeclaration_auth() {
        return declaration_auth;
    }

    public void setDeclaration_auth(int declaration_auth) {
        this.declaration_auth = declaration_auth;
    }
}
