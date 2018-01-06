package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class ConfirmPayPwdActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private Button confirmPwdButton;
    private EditText acpPwdEdit;
    private String pwd;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pwd);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        initViews();
        initDialog();
    }

    @Override
    protected void initEvents() {

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
        confirmPwdButton = (Button) findViewById(R.id.confirm_pwd_button);
        acpPwdEdit = (EditText) findViewById(R.id.acp_pwd);
        confirmPwdButton.setOnClickListener(this);

        acpPwdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pwd = acpPwdEdit.getText().toString();
                changeBg();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_pwd_button:
                validate();
                break;

        }
    }

    private void validate() {
        String pwd = acpPwdEdit.getText().toString();
        if (StringUtils.isBlank(pwd)) {
            ToastUtil.showToast(mContext, getResources().getString(R.string.asp_input_incomplete));
            return;
        }
        confirmPwd();
    }

    /**
     * 验证支付密码
     */
    private void confirmPwd() {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.verifyPayPassword(token, pwd);
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
                    int code = baseResponse.getCode();
                    if (code == MyConstant.SUCCESS) {
                        Intent intent=new Intent(mContext,UpdateBindBankActivity.class);
                        intent.putExtra("pwd",pwd);
                        startActivity(intent);
                    } else {
                        String msg = baseResponse.getMsg();
                        ToastUtil.showToast(mContext, msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void changeBg() {
        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
            confirmPwdButton.setBackgroundResource(R.drawable.bg_login_defaultstyle);
        } else {
            confirmPwdButton.setBackgroundResource(R.drawable.bg_login_style);
        }
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
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }

}
