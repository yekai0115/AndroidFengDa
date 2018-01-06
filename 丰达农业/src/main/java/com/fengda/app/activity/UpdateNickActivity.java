package com.fengda.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.widget.EditText;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.MsgEvent5;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 修改昵称
 */

public class UpdateNickActivity extends BaseActivity {


    @ViewInject(R.id.ed_nick)
    private EditText ed_nick;
    private Context mContext;
    private String nick;
    private String  token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        x.view().inject(this);
        mContext = this;
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        initViews();
        initDialog();
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
                String nick=ed_nick.getText().toString();
                if(StringUtils.isBlank(nick)){
                    ToastUtil.showToast(mContext,"昵称不能为空");
                    return;
                }
                updateNIck(nick);
            }
        });
        nick=getIntent().getStringExtra("nick");
        if(!StringUtils.isBlank(nick)){
            ed_nick.setText(nick);
            //光标置于文字右边
            CharSequence text = ed_nick.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable)text;
                Selection.setSelection(spanText, text.length());
            }
        }

    }

    @Override
    protected void initEvents() {

    }


    private void updateNIck(String nick) {
        dialog.show();
        String pwd = ed_nick.getText().toString();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.updateNick(token,nick);
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
                        ToastUtil.showToast(mContext, "昵称修改成功");
                        EventBus.getDefault().post(new MsgEvent5());
                        finish();
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


}
