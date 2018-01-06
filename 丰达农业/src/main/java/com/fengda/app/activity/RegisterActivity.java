package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.PhoneUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.watcher.PasswordTextWatcher;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {


    @ViewInject(R.id.ed_phone)
    private EditText ed_phone;
    @ViewInject(R.id.tv_phone_err)
    private TextView tv_phone_err;


    /***/
    @ViewInject(R.id.ed_pwd)
    private EditText ed_pwd;
    @ViewInject(R.id.tv_pwd_err)
    private TextView tv_pwd_err;


    /***/
    @ViewInject(R.id.ed_repwd)
    private EditText ed_repwd;
    @ViewInject(R.id.tv_repwd_err)
    private TextView tv_repwd_err;

    @ViewInject(R.id.ed_yzm)
    private EditText ed_yzm;
    /**
     * 获取验证码
     */
    @ViewInject(R.id.tv_yzm)
    private TextView tv_yzm;

    @ViewInject(R.id.ed_invite)
    private EditText ed_invite;
    @ViewInject(R.id.tv_invite_err)
    private TextView tv_invite_err;

    @ViewInject(R.id.cb_license)
    private CheckBox cb_license;
    @ViewInject(R.id.btn_register)
    private Button btn_register;



    /**
     * 上下文
     **/
    private Context mContext;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String vcode;
    /**
     * 密码
     */
    private String pwd;
    private String rePwd;
    /**
     * 邀请人
     */
    private String referer;

    /**
     * 是否点击了获取验证吗按钮 true 为已经点击过
     */
    private boolean isClickCheckBtn = false;


    public RegisterActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        x.view().inject(this);
        mContext = this;
        initDialog();
        initViews();
    }

    @Override
    protected void initViews() {
        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mobile = ed_phone.getText().toString().trim();
                changeBg();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ed_pwd.addTextChangedListener(new PasswordTextWatcher(ed_pwd) {

            @Override
            public void afterTextChanged(Editable s) {
                pwd = ed_pwd.getText().toString().trim();
                rePwd = ed_repwd.getText().toString().trim();
                changeBg();
                tv_pwd_err.setVisibility(View.GONE);
                if (rePwd.equals(pwd)) {
                    tv_repwd_err.setVisibility(View.GONE);
                }
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

        ed_repwd.addTextChangedListener(new PasswordTextWatcher(ed_repwd) {

            @Override
            public void afterTextChanged(Editable s) {
                rePwd = ed_repwd.getText().toString().trim();
                pwd = ed_pwd.getText().toString().trim();
                changeBg();
                tv_repwd_err.setVisibility(View.GONE);
                if (rePwd.equals(pwd)) {
                    tv_pwd_err.setVisibility(View.GONE);
                }
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

        ed_yzm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                vcode = ed_yzm.getText().toString().trim();
                changeBg();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ed_invite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                referer = ed_invite.getText().toString().trim();
                tv_invite_err.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cb_license.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeBg();
            }
        });
    }

    @Override
    protected void initEvents() {

    }

    private void changeBg() {
        if (StringUtils.isBlank(mobile) || mobile.length() < 11
                || StringUtils.isBlank(pwd) || StringUtils.isBlank(rePwd) || pwd.length() < 6
                || StringUtils.isBlank(vcode) || vcode.length() < 6
                || !cb_license.isChecked()) {
            btn_register.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            btn_register.setBackgroundResource(R.drawable.bg_login_style);
        }
    }


    private boolean checkParm() {
        if (StringUtils.isBlank(mobile) || mobile.length() < 11) {
            tv_phone_err.setVisibility(View.VISIBLE);
            return false;
        } else {
            tv_phone_err.setVisibility(View.GONE);
        }

        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
            tv_pwd_err.setVisibility(View.VISIBLE);
            return false;
        } else {
            tv_pwd_err.setVisibility(View.GONE);
        }

        if (!rePwd.equals(pwd)) {
            tv_repwd_err.setVisibility(View.VISIBLE);
            return false;
        } else {
            tv_repwd_err.setVisibility(View.GONE);
        }

        if (StringUtils.isBlank(vcode) || vcode.length() < 6) {
            ToastUtil.showToast(mContext, "请输入正确的验证码");
            return false;
        }


        if (!StringUtils.isBlank(referer) && referer.length() < 11) {
            tv_invite_err.setVisibility(View.VISIBLE);
            return false;
        } else {
            tv_invite_err.setVisibility(View.GONE);
        }

        if (!cb_license.isChecked()) {
            ToastUtil.showToast(mContext, "请同意服务条款");
            return false;
        }
        return true;
    }


    @Event({R.id.tv_yzm, R.id.btn_register, R.id.tv_license,R.id.img_close})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_yzm://获取验证码
                mobile = ed_phone.getText().toString().trim();
                if (StringUtils.isBlank(mobile)) {
                    ToastUtil.showToast(mContext, "请输入手机号");
                    return;
                } else if (!PhoneUtils.isMobileNO(mobile)) {
                    ToastUtil.showToast(mContext, "手机号码格式不正确");
                    return;
                }
                getCheckNum();
                break;
            case R.id.btn_register://注册
                if (checkParm()) {
                    register();
                }
                break;
            case R.id.tv_license://查看服务协议
                intent=new Intent(mContext,MyWebViewActivity.class);
                intent.putExtra("site_url",MyConstant.COM_SERVICE);
                intent.putExtra("name","服务条款");
                startActivity(intent);
                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCheckNum() {
        isClickCheckBtn = true;
        tv_yzm.setClickable(false);
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<String>> call = userBiz.sendSMS(mobile);
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


    private void register() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<String>> call = userBiz.registerRepo(mobile, vcode, pwd, referer);
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
                        String token = baseResponse.getData();
                        String encodeToken = EncodeUtils.base64Encode2String(token.getBytes());
                        SPUtils.put(mContext, "token", encodeToken);
                        SPUtils.put(mContext, "phone", mobile);
                        MyConstant.HASLOGIN = true;
                        SPUtils.put(mContext, "login", MyConstant.SUC_RESULT);
                        MyApplication.finishSingleActivityByClass(LoginActivity.class);
                        finish();
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                } else {
                    ToastUtil.showToast(mContext, "服务器连接失败,请稍后再试");
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
