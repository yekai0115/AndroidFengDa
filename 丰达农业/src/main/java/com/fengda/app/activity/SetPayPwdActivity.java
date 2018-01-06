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

import static com.fengda.app.R.id.ed_pwd;
import static com.fengda.app.R.id.ed_repwd;

/**
 * 设置支付密码、忘记支付密码、修改支付密码
 */

public class SetPayPwdActivity extends BaseActivity {
    @ViewInject(R.id.tv_phone)
    private TextView tv_phone;

    @ViewInject(R.id.ed_code)
    private EditText ed_code;

    /**
     * 获取验证码
     */
    @ViewInject(R.id.tv_yzm)
    private TextView tv_yzm;


//    @ViewInject(R.id.ed_pwd)
//    private EditText ed_pwd;
//
//    @ViewInject(R.id.ed_repwd)
//    private EditText ed_repwd;
    @ViewInject(R.id.btn_set)
    private Button btn_set;
    private Context mContext;
    private String token;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd);
        x.view().inject(this);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
        tv_phone.setText(phone);
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

        ed_code.addTextChangedListener(new TextWatcher() {
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


        /*ed_pwd.addTextChangedListener(new TextWatcher() {
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
        });*/

        /*ed_repwd.addTextChangedListener(new TextWatcher() {
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
        });*/


    }


    @Event({R.id.btn_set, R.id.tv_yzm})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_yzm://获取验证码
                if (StringUtils.isBlank(phone)) {
                    ToastUtil.showToast(mContext, "手机号为空,请重新登陆");
                    return;
                }
                getCheckNum(phone);
                break;
            case R.id.btn_set://
                if (checkParm()) {
                      checkCode();
//                    setNewPwd();
                }
                break;
            default:
                break;
        }
    }


    private boolean checkParm() {
        String code = ed_code.getText().toString();
//        String pwd = ed_pwd.getText().toString();
//        String repwd = ed_repwd.getText().toString();
        if (StringUtils.isBlank(phone)) {
            ToastUtil.showToast(mContext, "手机号为空,请重新登陆");
            return false;
        }
        if (StringUtils.isBlank(code) || code.length() < 6) {
            ToastUtil.showToast(mContext, "请输入正确验证码");
            return false;
        }
//        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
//            ToastUtil.showToast(mContext, "密码长度最少六位");
//            return false;
//        }
//        if (StringUtils.isBlank(repwd) || !pwd.equals(repwd)) {
//            ToastUtil.showToast(mContext, "密码不一致");
//            return false;
//        }
        return true;
    }


    private void checkCode() {
        dialog.show();
        final String code = ed_code.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.checkCode(phone,"4",code);
        call.enqueue(new Callback<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        Intent intent = new Intent();
                        intent.setClass(SetPayPwdActivity.this,SetPayPwdNextActivity.class);
                        intent.putExtra("code", code);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                } else {
                    String error = response.errorBody().toString();
                    error = response.raw().toString();
                    int code = response.code();
                    error = response.toString();

                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }
    private void changeBg() {
        String code = ed_code.getText().toString().trim();
//        String pwd = ed_pwd.getText().toString().trim();
//        String repwd = ed_repwd.getText().toString().trim();
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code) || code.length() < 6) {
            btn_set.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            btn_set.setBackgroundResource(R.drawable.bg_login_style);
        }
    }


    /**
     * 获取验证码
     */
    private void getCheckNum(String phone) {
        tv_yzm.setClickable(false);
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.sendSMSForPwd(token, phone);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0,response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        // 60秒内不能重复发送两次
                        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tv_yzm.setText(millisUntilFinished / 1000 + "s" + "可重发");

                            }

                            @Override
                            public void onFinish() {
                                tv_yzm.setText("验证码");
                                tv_yzm.setClickable(true);

                            }
                        }.start();
                    } else {
                        ToastUtil.showToast(mContext, desc);
                        tv_yzm.setText("验证码");
                        tv_yzm.setClickable(true);
                    }
                } else {
                    String error = response.errorBody().toString();
                    error = response.raw().toString();
                    int code = response.code();
                    error = response.toString();
                    tv_yzm.setText("验证码");
                    tv_yzm.setClickable(true);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                tv_yzm.setClickable(true);
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    /*private void setNewPwd() {
        dialog.show();
        String code = ed_code.getText().toString();
//        String pwd = ed_pwd.getText().toString();
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
    }*/

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