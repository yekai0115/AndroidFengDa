package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent16;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;
import com.fengda.app.view.watcher.PasswordTextWatcher;
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
 * 描述 ：忘记密码页面
 */
public class ForgetPwdNextActivity extends BaseActivity {







    @ViewInject(R.id.et_pwd)
    private EditText et_pwd;

    @ViewInject(R.id.et_repwd)
    private EditText et_repwd;
    @ViewInject(R.id.btn_set)
    private Button btn_set;
    /**
     * 上下文
     **/
    private Context mContext;
    private Intent intent;
    private String phone;
    private String code;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_next_pwd);
        EventBus.getDefault().register(this);
        x.view().inject(this);
        mContext = this;
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
        code=intent.getStringExtra("code");
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setTitle(title);
        phone = intent.getStringExtra("phone");
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        et_pwd.addTextChangedListener(new PasswordTextWatcher(et_pwd) {

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



    }

    private void changeBg() {
            String pwd = et_pwd.getText().toString().trim();
            String repwd = et_repwd.getText().toString().trim();
            if (StringUtils.isBlank(phone) || pwd.length() < 6|| StringUtils.isBlank(repwd) || repwd.length() < 6) {
                btn_set.setBackgroundResource(R.drawable.bg_login_defaultstyle);
            } else {
                btn_set.setBackgroundResource(R.drawable.bg_login_style);
            }
    }




    @Event({R.id.btn_set,R.id.tv_yzm})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_set://
                if(checkParm()){
                    reSetPwd();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkParm() {
        String pwd = et_pwd.getText().toString();
        String repwd = et_repwd.getText().toString();
        if (StringUtils.isBlank(pwd)||pwd.length()<6) {
            ToastUtil.showToast(mContext, "密码长度最少六位");
            return false;
        }
        if (StringUtils.isBlank(repwd)||!pwd.equals(repwd)) {
            ToastUtil.showToast(mContext, "密码不一致");
            return false;
        }
        return true;
    }






    private void reSetPwd() {
        dialog.show();
        String pwd = et_pwd.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.resetPwd(phone,code,pwd);
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
                        ToastUtil.showToast(mContext, "密码修改成功,请妥善保存");
                        finish();
                        EventBus.getDefault().post(new MsgEvent16());//地址选择页面数据更新
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
    public void onMessageEventMain(LoginMsgEvent messageEvent) {

        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            reSetPwd();
        } else {// 未登录
            MyConstant.HASLOGIN = false;
        }
    }


}