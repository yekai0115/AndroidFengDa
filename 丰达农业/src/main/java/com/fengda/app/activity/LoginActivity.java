package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;
import com.fengda.app.view.watcher.PasswordTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fengda.app.R.string.phone;


/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {




    // 上下文
    private Context mContext;
    // intent
    private Intent intent;

    /***/
    @ViewInject(R.id.ed_phone)
    private EditText ed_phone;
    /***/
    @ViewInject(R.id.ed_pwd)
    private EditText ed_pwd;

    @ViewInject(R.id.btn_login)
    private Button btn_login;

    private String phone;

    private String pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login);
        MyApplication.addActivity(this);
        x.view().inject(this);
        mContext = this;
        intent = getIntent();
        initDialog();
        initViews();
    }
    private void setSlidr() {
        int primary = getResources().getColor(R.color.toming);
        int secondary = getResources().getColor(R.color.accent);
        SlidrConfig config = new SlidrConfig.Builder().primaryColor(primary)
                .secondaryColor(secondary).position(SlidrPosition.LEFT)
                .touchSize(DimenUtils.dip2px(mContext, 60)).build();
        Slidr.attach(this, config);
    }

    @Override
    protected void initViews() {
        dialog.setCancelable(true);
        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone = ed_phone.getText().toString().trim();
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
    }

    @Override
    protected void initEvents() {

    }

    private void changeBg() {
        if (StringUtils.isBlank(phone) || phone.length() < 11 || StringUtils.isBlank(pwd) || pwd.length() < 6) {
            btn_login.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            btn_login.setBackgroundResource(R.drawable.bg_login_style);
        }
    }


    @Event({R.id.tv_forget_pwd, R.id.btn_login,R.id.img_close,R.id.tv_register_immediately})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_forget_pwd://
                intent = new Intent(mContext, ForgetPwdActivity.class);
                intent.putExtra("title", "找回密码");
                startActivity(intent);
                break;
            case R.id.btn_login://登录
                checkParms();
                break;
            case R.id.img_close:
                this.finish();
                break;
            case R.id.tv_register_immediately:
                startActivity(RegisterActivity.class);
                break;
        }
    }

    private void checkParms(){
        if (StringUtils.isBlank(phone) || phone.length() < 11) {
            ToastUtil.showToast(mContext,"请输入正确的手机号");
            return;
        }

        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
            ToastUtil.showToast(mContext,"密码位数不正确");
            return;
        }
        login();
    }





    private void login() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<String>> call = userBiz.loginRepo(phone + ":" + pwd);
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
                        SPUtils.put(mContext, "phone", phone);
                        MyConstant.HASLOGIN = true;
                        SPUtils.put(mContext, "login", MyConstant.SUC_RESULT);
                        finish();
                    } else {
                        ToastUtil.showToast(mContext, desc);

                    }
                } else {
                    String error = response.errorBody().toString();
                    error = response.raw().toString();
                    int code = response.code();
                    error = response.toString();
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new LoginMsgEvent());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 监听返回键
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}


