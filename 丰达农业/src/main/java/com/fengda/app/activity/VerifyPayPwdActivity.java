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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent18;
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

/**
 * 设置支付密码、忘记支付密码、修改支付密码
 */

public class VerifyPayPwdActivity extends BaseActivity {
    @ViewInject(R.id.ed_pwd)
    private EditText ed_pwd;

    @ViewInject(R.id.ll_confirm_pay_pwd)
    private LinearLayout ll_confirm_pay_pwd;

    /**
     * 获取验证码
     */

    @ViewInject(R.id.btn_set)
    private Button btn_set;
    private Context mContext;
    private String token;
    private String phone;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd_next);
        x.view().inject(this);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        ll_confirm_pay_pwd.setVisibility(View.GONE);
        type = getIntent().getIntExtra("type",0);
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
        topNvgBar.setTitle("验证支付密码");
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




    }


    @Event({R.id.btn_set})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_set://
                if (checkParm()) {
                    verifyPayPassword();
                }
                break;
            default:
                break;
        }
    }
    private void verifyPayPassword() {
        dialog.show();
        final String pwd = ed_pwd.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.verifyPayPassword(token,pwd);
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
//                        ToastUtil.showToast(getApplicationContext(), "验证成功");
                        if (type == 2){
                            Intent intent = new Intent(mContext, VerifyBankCardActivity.class);
                            intent.putExtra("title","修改银行卡");
                            intent.putExtra("state", 2);
                            intent.putExtra("pwd", pwd);
                            startActivity(intent);
                        }else {
                            defaultFinish();
                            MsgEvent18 msgEvent18 = new MsgEvent18();
                            msgEvent18.setType(type);
                            EventBus.getDefault().post(msgEvent18);//通知个人信息页面验证二级密码成功
                        }
                    }else if (retCode == MyConstant.FAILED) {
                        ToastUtil.showToast(getApplicationContext(), "未设置支付密码");
                    }else {
                        ToastUtil.showToast(getApplicationContext(), "支付密码错误,请重试");
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private boolean checkParm() {
        String pwd = ed_pwd.getText().toString();
        if (StringUtils.isBlank(pwd)) {
            ToastUtil.showToast(mContext, "请输入支付密码");
            return false;
        }
        return true;
    }


    private void changeBg() {
        String pwd = ed_pwd.getText().toString();
        if (StringUtils.isBlank(pwd)) {
            btn_set.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            btn_set.setBackgroundResource(R.drawable.bg_login_style);
        }
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent2 messageEvent) {
        finish();
    }
}