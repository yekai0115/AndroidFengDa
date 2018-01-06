package com.fengda.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.fengda.app.R;
import com.fengda.app.alipay.PayResult;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.AssemblingPayBean;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.PayMethod;
import com.fengda.app.bean.PayReturnInfo;
import com.fengda.app.bean.WxPayResult;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.dialog.DialogWidget;
import com.fengda.app.eventbus.AliPayMsg;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.WeiXinPayMsg;
import com.fengda.app.eventbus.YinLianPayMsg;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.widget.TopNvgBar5;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by 24448 on 2017/10/20.
 */

public class AssemblingOrderActivity extends BaseActivity {

    private final String mMode = "00";
    @ViewInject(R.id.tv_order_money)
    private TextView tv_order_money;
    @ViewInject(R.id.cb_weixin)
    private CheckBox cb_weixin;
    @ViewInject(R.id.cb_zhifubao)
    private CheckBox cb_zhifubao;
    @ViewInject(R.id.cb_card)
    private CheckBox cb_card;
    @ViewInject(R.id.rl_yinlian)
    private RelativeLayout rl_yinlian;

    @ViewInject(R.id.rl_zhifubao)
    private RelativeLayout rl_zhifubao;

    @ViewInject(R.id.rl_weixin)
    private RelativeLayout rl_weixin;


    private int payType;
    private Context mContext;
    private String token;
    private String pay_money;
    /**
     * 1:商品支付
     * 2：提货支付
     */
    private String type;
    private String activityId;
    //微信支付
    private PayReq req;
    private IWXAPI api;
    private AssemblingPayBean assemblingPayBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembling_pay_order);
        x.view().inject(this);
        EventBus.getDefault().register(this);
        mContext = this;
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        initViews();
        initEvents();
        initDialog();
        activityId = getIntent().getStringExtra("activityId");
        assemblingPayBean = (AssemblingPayBean) getIntent().getSerializableExtra("assemblingPayBean");
        if (assemblingPayBean!=null){
            pay_money=assemblingPayBean.getMoney_need();
            tv_order_money.setText(pay_money);
            if (assemblingPayBean.getPay_auth().getWx_auth().equals("0")){
                rl_weixin.setVisibility(View.GONE);
            }
            if (assemblingPayBean.getPay_auth().getAlipay_auth().equals("0")){
                rl_zhifubao.setVisibility(View.GONE);
            }
            if (assemblingPayBean.getPay_auth().getCard_pay().equals("0")){
                rl_yinlian.setVisibility(View.GONE);
            }
        }
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
        req = new PayReq();
        api = WXAPIFactory.createWXAPI(mContext, WxConstants.APP_ID);
        api.registerApp(WxConstants.APP_ID);// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
    }

    @Override
    protected void initEvents() {
    }

    @Event({R.id.rl_weixin, R.id.rl_zhifubao, R.id.rl_yinlian, R.id.rl_point,R.id.tv_buy})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.rl_weixin://
                payType = 2;
                cb_zhifubao.setChecked(false);
                cb_card.setChecked(false);
                cb_weixin.setChecked(true);
                break;
            case R.id.rl_zhifubao://
                payType = 1;
                cb_weixin.setChecked(false);
                cb_card.setChecked(false);
                cb_zhifubao.setChecked(true);
                break;
            case R.id.rl_yinlian:
                payType = 3;
                cb_weixin.setChecked(false);
                cb_zhifubao.setChecked(false);
                cb_card.setChecked(true);
                break;
            case R.id.tv_buy://
                if(payType==0){
                    ToastUtil.showToast(mContext, "请选择支付方式");
                    return;
                }else{//生成支付订单
                    submitJinYouActivity();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 生成金柚子计划支付订单
     */
    private void submitJinYouActivity() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<PayReturnInfo>>> call = userBiz.submitJinYouActivity(token,activityId,payType);
        call.enqueue(new HttpCallBack<BaseResponse<List<PayReturnInfo>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<PayReturnInfo>>> arg0,
                                   Response<BaseResponse<List<PayReturnInfo>>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<List<PayReturnInfo>> baseResponse = response.body();
                if (null != baseResponse) {
                    String msg = baseResponse.getMsg();
                    int code = baseResponse.getCode();
                    if (code == MyConstant.SUCCESS) {
                        List<PayReturnInfo>  payReturnInfoList=baseResponse.getData();
                        if(null==payReturnInfoList||payReturnInfoList.isEmpty()){
                            return;
                        }
                        PayReturnInfo payOrder = payReturnInfoList.get(0);
                        if (null == payOrder) {
                            return;
                        }
                        switch (payType) {
                            case 1:
                                if (StringUtils.isBlank(payOrder.getZhifubao())) {
                                    return;
                                }
                                dialog.show();
                                zhifubaoPay(payOrder.getZhifubao().toString());
                                break;
                            case 2:
                                dialog.show();
                                weiXinPay(payOrder);
                                break;
                            case 3:
                                if (StringUtils.isBlank(payOrder.getYinlian())) {
                                    return;
                                }
                                dialog.show();
                                EventBus.getDefault().post(new YinLianPayMsg(payOrder.getYinlian()));//发送事件：
                                break;
                        }
                    } else if (code == (MyConstant.ERR_AUTH)) {//token过期

                    } else {
                        ToastUtil.showToast(mContext, msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<PayReturnInfo>>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    /**
     * 请求支付宝客户端
     */
    private void zhifubaoPay(final String payInfo) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(AssemblingOrderActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                EventBus.getDefault().post(new AliPayMsg(result));//发送事件：
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }



    /**
     * 打开微信客户端支付
     * @param data
     */
    private void weiXinPay(PayReturnInfo data) {

        try {
            String appid = data.getAppid();//应用ID
            String partnerid = data.getPartnerid();//商户号
            String prepayid = data.getPrepayid();//预支付交易会话ID
            String packageValue = data.getPackageValue();//扩展字段
            String noncestr = data.getNoncestr();//随机字符串
            String timestamp = data.getTimestamp();//时间戳
            String sign = data.getSign();//签名
            packageValue = "Sign=WXPay";
            req.appId = appid;
            req.partnerId = partnerid;
            req.prepayId = prepayid;
            req.nonceStr = noncestr;
            req.timeStamp = timestamp;
            req.packageValue = packageValue;
            req.sign = sign;
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    api.sendReq(req);
                }
            };
            //异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (Exception e) {

        }
    }


    //支付宝支付回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(AliPayMsg aliPayMsg) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        PayResult payResult = new PayResult((String) aliPayMsg.getMessage());
        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
        String resultInfo = payResult.getResult();
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000")) {
            ToastUtil.showToast(mContext, "支付成功");
            payScucess();
        } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                ToastUtil.showToast(mContext, "支付结果确认中");
            } else {// resultStatus={6002};memo={网络连接异常};result={}
                // resultStatus={6001};memo={用户取消};result={}
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                ToastUtil.showToast(mContext, "支付失败");

            }
        }
    }


    //微信支付回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(WeiXinPayMsg weiXinPayMsg) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        int error_code = weiXinPayMsg.getTage();
        // 根据返回码判断，0是支付成功，-2是取消操作，其他是支付失败。返回0的话，再次请求一次接口，把上一个页面获取
        if (error_code == 0) {
            dialog.show();
         //   queryWxOrder();
            payScucess();
        } else if (error_code == -2) {// 用户取消支付
            ToastUtil.showToast(mContext, "取消支付");
            //  canclePay();
        } else {// 支付失败
            // canclePay();
        }
    }


    //银联支付
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(YinLianPayMsg payMsg) {
        String msg = payMsg.getMsg();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (StringUtils.isBlank(msg)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AssemblingOrderActivity.this);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            /*************************************************
             * 步骤2：通过银联工具类启动支付插件
             ************************************************/
            //在调用支付控件的代码按以下方式调用支付控件
                /*参数说明：
                    activity —— 用于启动支付控件的活动对象
                    spId —— 保留使用，这里输入null
                    sysProvider —— 保留使用，这里输入null
                    orderInfo —— 订单信息为交易流水号，即TN，为商户后台从银联后台获取。
                    mode —— 银联后台环境标识，“00”将在银联正式环境发起交易,“01”将在银联测试环境发起交易
                    返回值：
                    UPPayAssistEx.PLUGIN_VALID —— 该终端已经安装控件，并启动控件
                    UPPayAssistEx.PLUGIN_NOT_FOUND — 手机终端尚未安装支付控件，需要先安装支付控件
                    */
            UPPayAssistEx.startPay(AssemblingOrderActivity.this, null, null, msg, mMode);
        }
    }

    //银联支付回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            payScucess();
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "取消支付";
        }
        showShortToast(msg);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            if(payType==4){//避免清除了支付密码状态
                SPUtils.put(mContext, "payPwd" + token, MyConstant.SUCCESS);
            }
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 查询微信支付结果
     */
    private void queryWxOrder() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Object>>> call = userBiz.queryWXOrder(token, type);
        call.enqueue(new HttpCallBack<BaseResponse<List<Object>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Object>>> arg0,
                                   Response<BaseResponse<List<Object>>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<List<Object>> baseResponse = response.body();
                if (null != baseResponse) {
                    int code = baseResponse.getCode();
                    if (code == MyConstant.SUCCESS) {
                        payScucess();
                    } else {
                        payScucess();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Object>>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                payScucess();
            }
        });
    }

    /**
     * 支付成功
     */
    private void payScucess() {
            Intent intent = new Intent(mContext, PaySuccessActivity.class);
            intent.putExtra("pay_money", pay_money);
            startActivity(intent);
            MyApplication.finishSingleActivityByClass(SubmitCarOrderActivity.class);
            MyApplication.finishSingleActivityByClass(GoodsDetalActivity.class);
        finish();
    }

}
