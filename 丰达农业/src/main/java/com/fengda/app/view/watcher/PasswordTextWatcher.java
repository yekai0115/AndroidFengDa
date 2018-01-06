/**
 * Project Name:TestAndroid File Name: PasswordTextWatcher.java Package
 * Name:com. .yanbb.testandroid Date:2015-5-27 Copyright (c) 2015, www. .com All
 * Rights Reserved.
 */

package com.fengda.app.view.watcher;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码输入监听
 * @author Administrator
 *
 */
public abstract class PasswordTextWatcher implements TextWatcher {
    // password match rule
    private static final String PASSWORD_REGEX = "[A-Z0-9a-z!@#$%^&*.~/\\{\\}|()'\"?><,.`\\+-=_\\[\\]:;]+";

    private boolean mIsMatch;
    private CharSequence mResult;
    private int mSelectionStart;
    private int mSelectionEnd;
    private EditText mPswEditText;

    public PasswordTextWatcher() {};

    public PasswordTextWatcher(EditText editText) {
        mPswEditText = editText;
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        mSelectionStart = mPswEditText.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        CharSequence charSequence = "";
        if ((mSelectionStart + count) <= s.length()) {
            charSequence = s.subSequence(mSelectionStart, mSelectionStart
                    + count);
        }
        mIsMatch = pswFilter(charSequence);
        if (!mIsMatch) {
            String temp = s.toString();
            mResult = temp.replace(charSequence, "");
            mSelectionEnd = start;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!mIsMatch) {
            mPswEditText.setText(mResult);
            mPswEditText.setSelection(mSelectionEnd);
        }
    }

    /**
     * pswFilter: if the param folow the password match rule<br/>
     * 
     * @author .yanbb
     * @param s
     * @return
     * @since MT 1.0
     */
    private boolean pswFilter(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
