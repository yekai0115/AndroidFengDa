package com.fengda.app.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.eventbus.WeiXinPayMsg;

import org.greenrobot.eventbus.EventBus;


/**
 * @author 微信支付结果回调
 *
 */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WxConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    /**
     * 支付完成后，微信APP会返回到商户APP并回调onResp函数
     */
    @Override
    public void onResp(BaseResp resp) {
        //根据返回码判断，0是支付成功，-2是取消操作，其他是支付失败。返回0的话，再次请求一次接口，把上一个页面获取
        //的订单号拿去请求
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            EventBus.getDefault().post(new WeiXinPayMsg(resp.errCode));
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}