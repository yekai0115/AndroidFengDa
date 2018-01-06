package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent2;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fengda.app.R.id.ed_code;
import static com.fengda.app.R.id.tv_phone;

/**
 * 设置支付密码、忘记支付密码、修改支付密码
 */

public class SetPayPwdNextActivity extends BaseActivity {


    @ViewInject(R.id.ed_pwd)
    private EditText ed_pwd;

    @ViewInject(R.id.ed_repwd)
    private EditText ed_repwd;
    @ViewInject(R.id.btn_set)
    private Button btn_set;
    private Context mContext;
    private String token;
    private String phone;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd_next);
        x.view().inject(this);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
        initViews();
        initDialog();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews() {
        code=getIntent().getStringExtra("code");
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });



        ed_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeBg();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ed_repwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeBg();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    @Event({R.id.btn_set, R.id.tv_yzm})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_set://
                if (checkParm()) {
                    setNewPwd();
                }
                break;
            default:
                break;
        }
    }


    private boolean checkParm() {
        String pwd = ed_pwd.getText().toString();
        String repwd = ed_repwd.getText().toString();
        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
            ToastUtil.showToast(mContext, "密码长度最少六位");
            return false;
        }
        if (StringUtils.isBlank(repwd) || !pwd.equals(repwd)) {
            ToastUtil.showToast(mContext, "密码不一致");
            return false;
        }
        return true;
    }


    private void changeBg() {
        String pwd = ed_pwd.getText().toString().trim();
        String repwd = ed_repwd.getText().toString().trim();
        if (StringUtils.isBlank(phone)  || StringUtils.isBlank(pwd) || pwd.length() < 6 || StringUtils.isBlank(repwd) || repwd.length() < 6) {
            btn_set.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            btn_set.setBackgroundResource(R.drawable.bg_login_style);
        }
    }


    private void setNewPwd() {
        dialog.show();
        String pwd = ed_pwd.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.resetPayPwd(token, phone, code, pwd);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), "支付密码设置成功");
                        defaultFinish();
                        EventBus.getDefault().post(new MsgEvent2());//通知兑换页面更新支付密码状态
                    } else {
                        ToastUtil.showToast(getApplicationContext(), desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }

}