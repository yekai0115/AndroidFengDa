package com.fengda.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.fengda.app.eventbus.MsgEvent16;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.PhoneUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 描述 ：忘记密码页面
 */
public class ForgetPwdActivity extends BaseActivity {



    @ViewInject(R.id.et_phone)
    private EditText et_phone;

    @ViewInject(R.id.et_code)
    private EditText et_code;

    /**
     * 获取验证码
     */
    @ViewInject(R.id.tv_yzm)
    private TextView tv_yzm;


//    @ViewInject(R.id.et_pwd)
//    private EditText et_pwd;

//    @ViewInject(R.id.et_repwd)
//    private EditText et_repwd;
    @ViewInject(R.id.btn_set)
    private Button btn_set;
    /**
     * 上下文
     **/
    private Context mContext;
    private Intent intent;
    private String phone;
    private String token;
    private int type;
    private boolean isResiter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        x.view().inject(this);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        setWidget();
        initDialog();
        setSlidr();

    }


    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }


    private void setSlidr() {
        int primary = getResources().getColor(R.color.toming);
        int secondary = getResources().getColor(R.color.accent);
        SlidrConfig config = new SlidrConfig.Builder().primaryColor(primary)
                .secondaryColor(secondary).position(SlidrPosition.LEFT)
                .touchSize(DimenUtils.dip2px(mContext, 60)).build();
        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this, config);

    }


    private void setWidget() {
        intent=getIntent();
        String title=intent.getStringExtra("title");
        type = intent.getIntExtra("type",0);
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setTitle(title);
        phone = (String) SPUtils.get(mContext, "phone", "");
        if(StringUtils.isBlank(phone)){
            et_phone.setEnabled(true);
        }else{
            et_phone.setEnabled(false);
            et_phone.setText(phone);
        }

        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
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

        et_code.addTextChangedListener(new TextWatcher() {
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


        /*et_pwd.addTextChangedListener(new PasswordTextWatcher(et_pwd) {

            @Override
            public void afterTextChanged(Editable s) {
                changeBg();
                super.afterTextChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                super.onTextChanged(s, start, before, count);
            }
        });*/

/*
        et_repwd.addTextChangedListener(new PasswordTextWatcher(et_repwd) {

            @Override
            public void afterTextChanged(Editable s) {
                changeBg();
                super.afterTextChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                super.onTextChanged(s, start, before, count);
            }
        });
*/



    }
    private void changeBg() {
            String phone = et_phone.getText().toString().trim();
            String code = et_code.getText().toString().trim();
//            String pwd = et_pwd.getText().toString().trim();
//            String repwd = et_repwd.getText().toString().trim();
            if (StringUtils.isBlank(phone)|| StringUtils.isBlank(code) || code.length() < 6) {
                btn_set.setBackgroundResource(R.drawable.bg_login_defaultstyle);
            } else {
                btn_set.setBackgroundResource(R.drawable.bg_login_style);
            }
    }




    @Event({R.id.btn_set,R.id.tv_yzm})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.tv_yzm://获取验证码
                String phone = et_phone.getText().toString();
                if (StringUtils.isBlank(phone)) {
                    ToastUtil.showToast(mContext, "请输入手机号");
                    return;
                } else if (!PhoneUtils.isMobileNO(phone)) {
                    ToastUtil.showToast(mContext, "手机号码格式不正确");
                    return;
                }
                getCheckNum(phone);
                break;
            case R.id.btn_set://
                if (checkParm()){
                    //验证验证码
                    checkCode();
//                    reSetPwd();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkParm() {
        String phone = et_phone.getText().toString();
        String code = et_code.getText().toString();
//        String pwd = et_pwd.getText().toString();
//        String repwd = et_repwd.getText().toString();
        if (StringUtils.isBlank(phone)||!PhoneUtils.isMobileNO(phone)) {
            ToastUtil.showToast(mContext, "请输入正确手机号");
            return false;
        }
        if (StringUtils.isBlank(code)||code.length()<6) {
            ToastUtil.showToast(mContext, "请输入正确验证码");
            return false;
        }
//        if (StringUtils.isBlank(pwd)||pwd.length()<6) {
//            ToastUtil.showToast(mContext, "密码长度最少六位");
//            return false;
//        }
//        if (StringUtils.isBlank(repwd)||!pwd.equals(repwd)) {
//            ToastUtil.showToast(mContext, "密码不一致");
//            return false;
//        }
        return true;
    }




    /**
     * 获取验证码
     */
    private void getCheckNum(String phone) {
        tv_yzm.setClickable(false);
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<String>> call = userBiz.getCode(phone);
        call.enqueue(new Callback<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                dialog.dismiss();
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(),"短信已发送，请注意查收");
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
            public void onFailure(Call<BaseResponse<String>> arg0, Throwable arg1) {
                dialog.dismiss();
                tv_yzm.setClickable(true);
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }

    private void checkCode() {
        dialog.show();
        final String code = et_code.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call;
        if(type==1){//修改密码
            call = userBiz.checkCode(phone,"1",code);
        }else{//忘记密码
            String phone = et_phone.getText().toString();
            call = userBiz.checkForgetCode(phone,code);
        }
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
                        intent.setClass(ForgetPwdActivity.this,ForgetPwdNextActivity.class);
                        intent.putExtra("code", code);
                        intent.putExtra("phone", et_phone.getText().toString());
                        startActivity(intent);
//                        startActivity(ForgetPwdNextActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent16 messageEvent) {
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {

        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            checkCode();
        } else {// 未登录
            MyConstant.HASLOGIN = false;
        }
    }

}