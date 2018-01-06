package com.fengda.app.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.fengda.app.R;
import com.fengda.app.adapter.SubmitCarOrderAdapter;
import com.fengda.app.alipay.PayResult;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.Address;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.GoodsInfo;
import com.fengda.app.bean.OrderCartBase;
import com.fengda.app.bean.OrderGoods;
import com.fengda.app.bean.OrderPay;
import com.fengda.app.bean.PayMethod;
import com.fengda.app.bean.PayReturnInfo;
import com.fengda.app.bean.PayTypeControl;
import com.fengda.app.bean.Shipping;
import com.fengda.app.bean.WxPayResult;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.eventbus.AliPayMsg;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.ToCarMsgEvent;
import com.fengda.app.eventbus.WeiXinPayMsg;
import com.fengda.app.eventbus.YinLianPayMsg;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.widget.TopNvgBar5;
import com.fengda.app.widget.swichbutton.ToggleButton;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 描述 ：购物车商品确认订单页面
 */
public class SubmitCarOrderActivity extends BaseActivity implements
        ListItemClickHelp {


    /**
     * 无收货人
     */
    @ViewInject(R.id.ll_no_address)
    private LinearLayout ll_no_address;
    /**
     * 有收货人地址
     */
    @ViewInject(R.id.ll_address)
    private LinearLayout ll_address;
    /**
     * 收货人名字
     */
    @ViewInject(R.id.tv_sh_name)
    private TextView tv_sh_name;
    /**
     * 收货人手机号
     */
    @ViewInject(R.id.tv_phone)
    private TextView tv_sh_phone;

    /**
     * 收货人地址
     */
    @ViewInject(R.id.tv_address)
    private TextView tv_sh_des;


    /**
     * 商品总数
     */
    @ViewInject(R.id.tv_all_num)
    private TextView tv_all_num;

    /**
     * 商品小计金额
     */
    @ViewInject(R.id.tv_xiaoji)
    private TextView tv_xiaoji;

    /**
     * 实付款：商品小计+运费
     */
    @ViewInject(R.id.tv_shifukuan)
    private TextView tv_shifukuan;


    /**
     * 提交订单
     */
    @ViewInject(R.id.btn_pay)
    private Button btn_pay;

    /**
     * 可用积分
     */
    @ViewInject(R.id.tv_goods_point_need)
    private TextView tv_goods_point_need;


    @ViewInject(R.id.switch_ziti)
    private ToggleButton switch_ziti;


    /**
     * 商品总数量
     */
    private int totlaGoodsNum;

    /**
     * 地址id
     */
    private String fdeliveryAddrId;
    /**
     * 邮费模板id
     */
    private String shipping_id;
    /**
     * 收货人
     */
    private String accept_name;
    /**
     * 收货人详细地址
     */
    private String address;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 省市区
     */
    private String cr_id;
    private String mProinceName;
    private final String mMode = "00";

    /**
     * 商品总额
     */
    private String all_goods_price;
    /**
     * 总运费
     */
    private String all_feight;
    /**
     * 实付款金额：all_goods_price+all_feight
     */
    private String total_fee;
    /**
     * 商品需要的总积分
     */
    private String all_gooods_point;

    private Address destination;
    private Intent intent;

    /**
     * 上下文
     **/
    private Context mContext;
    private String token;
    private String phone;

    @ViewInject(R.id.cb_weixin)
    private CheckBox cb_weixin;
    @ViewInject(R.id.cb_zhifubao)
    private CheckBox cb_zhifubao;
    @ViewInject(R.id.cb_card)
    private CheckBox cb_card;
    private String from_type;
    private int payType;

    @ViewInject(R.id.rl_yinlian)
    private RelativeLayout rl_yinlian;

    @ViewInject(R.id.rl_zhifubao)
    private RelativeLayout rl_zhifubao;

    @ViewInject(R.id.rl_weixin)
    private RelativeLayout rl_weixin;

    @ViewInject(R.id.rl_point)
    private RelativeLayout rl_point;

    @ViewInject(R.id.lv_carOrder)
    private ListView lv_carOrder;
    private List<GoodsInfo> groupList = new ArrayList<GoodsInfo>();
    private SubmitCarOrderAdapter adapter;
    private OrderCartBase orderCartBase;
    private String goods_attr_id;
    private String queryId;
    private int since_order;
    //微信支付
    private PayReq req;
    private IWXAPI api;
    private String type;
    private int useConsumption = 0;
    private String deduction;//抵扣价格
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_queren);
        x.view().inject(this);
        mContext = this;
        EventBus.getDefault().register(this);//订阅事件：
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
        setWidget();
        initViews();
        initDialog();
        intent = getIntent();
        String data = intent.getStringExtra("data");
        orderCartBase = GsonUtil.GsonToBean(data, OrderCartBase.class);
        goods_attr_id = intent.getStringExtra("goods_attr_id");
        queryId = intent.getStringExtra("queryId");
        from_type = intent.getStringExtra("from_type");
        adapter = new SubmitCarOrderAdapter(mContext, groupList, this);
        lv_carOrder.setAdapter(adapter);
        updateWidget(1);
        switch_ziti.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    useConsumption = 1;
                    tv_shifukuan.setText(CompuUtils.subtract(total_fee,deduction).toString());
                }else {
                    tv_shifukuan.setText(total_fee);
                    useConsumption = 0;
                }
            }
        });
    }

    @Override
    protected void initViews() {
        req = new PayReq();
        api = WXAPIFactory.createWXAPI(mContext, WxConstants.APP_ID);
        api.registerApp(WxConstants.APP_ID);// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
    }

    @Override
    protected void initEvents() {

    }
    /**
     * @param type 1:第一次进来 2：重新选择了地址，更新商品列表信息和
     */
    private void updateWidget(int type) {
        try {
            groupList = orderCartBase.getGoods_info();
            destination = orderCartBase.getAddr_info();
//            Shipping shipping = orderCartBase.getShipping();
//            if (null == shipping) {
//                shipping_id = "";
//            } else {
//                shipping_id = shipping.getShipping_id();
//            }
            if (null == groupList || groupList.isEmpty()) {
                ToastUtil.showToast(mContext, "商品不存在，无法购买");
                finish();
                return;
            }
            totlaGoodsNum = 0;
            all_feight = "0";
            all_goods_price = "0";
            total_fee = "0";
            all_gooods_point = "0";
            for (GoodsInfo goodsInfo : groupList) {
                String postprice = goodsInfo.getPostprice();//邮费
                all_feight = CompuUtils.add(all_feight, postprice).toString();//总邮费
                String attr_price = goodsInfo.getAttr_price();
                int number = goodsInfo.getNumber();
                attr_price = CompuUtils.multiply(attr_price, number + "").toString();
                all_goods_price = CompuUtils.add(all_goods_price, attr_price).toString();//商品总额
                totlaGoodsNum = CompuUtils.add(totlaGoodsNum, number);//商品总数量
                String point = goodsInfo.getAttr_point();
                point = CompuUtils.multiply(point, number + "").toString();
                all_gooods_point = CompuUtils.add(all_gooods_point, point).toString();//商品需要的总积分
            }
            total_fee = CompuUtils.add(all_feight, all_goods_price).toString();//实付款
            tv_shifukuan.setText(total_fee);
            tv_xiaoji.setText(all_goods_price);
            tv_all_num.setText(totlaGoodsNum + "");
            if (Float.valueOf(orderCartBase.getSun_consumption()) <= 0){
                rl_point.setVisibility(View.GONE);
            }
            if (Float.valueOf(all_gooods_point) > 0) {//用户积分大于等于商品需要的总积分
                if (CompuUtils.compareTo(orderCartBase.getSun_consumption(),all_gooods_point) >= 0){
                    deduction = all_gooods_point;
                    tv_goods_point_need.setText("可使用" +all_gooods_point+"消费果"+"抵扣"+all_gooods_point+"元" );
                }else {
                    deduction = orderCartBase.getSun_consumption();
                    tv_goods_point_need.setText("可使用" +orderCartBase.getSun_consumption()+"消费果"+"抵扣"+orderCartBase.getSun_consumption()+"元" );
                }
            } else {
                rl_point.setVisibility(View.GONE);
//                btn_pay.setBackgroundColor(mContext.getResources().getColor(R.color.bg_main_bottom50));
            }
            if (useConsumption ==1){
                tv_shifukuan.setText(CompuUtils.subtract(total_fee,deduction).toString());
            }
            PayTypeControl payMethod = orderCartBase.getPay_auth();
            String alipay = payMethod.getAlipay_auth();
            String wxpay = payMethod.getWx_auth();
            String cardpay = payMethod.getCard_pay();
            if (alipay.equals("0")) {//支付宝
                rl_zhifubao.setVisibility(View.GONE);
            } else {
                rl_zhifubao.setVisibility(View.VISIBLE);
            }
            if (wxpay.equals("0")) {//微信
                rl_weixin.setVisibility(View.GONE);
            } else {
                rl_weixin.setVisibility(View.VISIBLE);
            }
            if (cardpay.equals("0")) {//银联
                rl_yinlian.setVisibility(View.GONE);
            } else {
                rl_yinlian.setVisibility(View.VISIBLE);
            }
            updateAddress();
            adapter.updateListview(groupList);
            setListviewHeight(lv_carOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void updateAddress() {
        if (null != destination && !StringUtils.isBlank(destination.getAddr_id())) {
            fdeliveryAddrId = destination.getAddr_id();
            tv_sh_name.setText(destination.getAddr_name());
            tv_sh_phone.setText(destination.getAddr_mobile());
            mProinceName = destination.getAddr_province();
            tv_sh_des.setText("收货地址:" + destination.getAddr_province() + " " + destination.getAddr_city() + " " + destination.getAddr_county() + " " + destination.getAddr_detail());
            ll_no_address.setVisibility(View.GONE);
            ll_address.setVisibility(View.VISIBLE);
            btn_pay.setBackgroundColor(mContext.getResources().getColor(R.color.bg_main_bottom));
        } else {
            ll_no_address.setVisibility(View.VISIBLE);
            ll_address.setVisibility(View.GONE);
            btn_pay.setBackgroundColor(mContext.getResources().getColor(R.color.bg_main_bottom50));
        }
    }


    /**
     * 初始化
     */
    private void setWidget() {
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
    }
    @Event({R.id.ll_chose_dizhi, R.id.btn_pay,R.id.rl_weixin,
            R.id.rl_zhifubao, R.id.rl_yinlian, R.id.rl_point,R.id.btn_pay})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ll_chose_dizhi:// 选择收货人地址
                intent = new Intent(mContext, AddressChooseActivity.class);
                startActivityForResult(intent, 1);
                break;
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
            case R.id.btn_pay://
                if (StringUtils.isBlank(fdeliveryAddrId)) {
                    ToastUtil.showToast(mContext, "请先选择收货地址");
                    return;
                }else if(payType==0){
                    ToastUtil.showToast(mContext, "请选择支付方式");
                    return;
                }else{//生成支付订单
                    submitOrder();
                }
                break;
            default:
                break;
        }
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
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (null != destination) {
            destination = null;
        }
        EventBus.getDefault().unregister(this);//取消订阅
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            String mPhone = (String) SPUtils.get(mContext, "phone", "");
            if (!phone.equals(mPhone)) {//切换了登陆用户
                finish();
            }
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }


    /**
     * 1:地址选择回调
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        Bundle bundle = data.getExtras();
        switch (requestCode) {
            case 1:// 地址回调
                try {
                    int resultType = bundle.getInt("result");// 从收货地址列表重新选择一个地址地址回调(2)、没有地址新增一个地址回调(1)
                    if (resultType == 0) {
                        btn_pay.setBackgroundColor(mContext.getResources().getColor(R.color.bg_main_bottom50));
                        ll_address.setVisibility(View.GONE);
                        ll_no_address.setVisibility(View.VISIBLE);
                    } else {
                        btn_pay.setBackgroundColor(mContext.getResources().getColor(R.color.bg_main_bottom));
                        ll_no_address.setVisibility(View.GONE);
                        ll_address.setVisibility(View.VISIBLE);
                        destination = bundle.getParcelable("place");
                        accept_name = destination.getAddr_name();
                        tv_sh_name.setText(accept_name);
                        fdeliveryAddrId = destination.getAddr_id();
                        mobile = destination.getAddr_mobile();
                        tv_sh_phone.setText(mobile);
                        address = destination.getAddr_detail();
                        String area = destination.getAddr_county();
                        String prvinceName = destination.getAddr_province_name();
                        if (StringUtils.isBlank(area)) {
                            cr_id = prvinceName + " " + destination.getAddr_city_name();
                        } else {
                            cr_id = prvinceName + " " + destination.getAddr_city_name() + " " + destination.getAddr_county_name();
                        }
                        tv_sh_des.setText("收货地址:" + cr_id + " " + address);
                        if (StringUtils.isBlank(mProinceName) || (!mProinceName.equalsIgnoreCase(prvinceName))) {
                            generateOrder();//重新生成确认订单
                        }
                        mProinceName = prvinceName;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void generateOrder() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<OrderCartBase>>> call = userBiz.generateOrder(token,goods_attr_id, queryId, fdeliveryAddrId);
        call.enqueue(new HttpCallBack<BaseResponse<List<OrderCartBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<OrderCartBase>>> arg0,
                                   Response<BaseResponse<List<OrderCartBase>>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<List<OrderCartBase>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        if (baseResponse.getData().size() == 0)
                            return;
                        orderCartBase = baseResponse.getData().get(0);
                        updateWidget(2);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<OrderCartBase>>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "服务器错误");
            }
        });
    }


    @Override
    public void onClick(View item, View widget, int position, int which) {
        GoodsInfo goodsInfo = groupList.get(position);
        int number = goodsInfo.getNumber();
        switch (which) {
            case R.id.tv_num_jian://减
                if (number == 1) {
                    number = 1;
                    ToastUtil.showToast(mContext, "不能再减了");
                    return;
                } else {
                    number--;
                    goodsInfo.setNumber(number);
                    getParms();
                }
                break;
            case R.id.tv_num_jia://加
                number++;
                goodsInfo.setNumber(number);
                getParms();
                break;
            default:
                break;
        }
    }


    /**
     * 生成支付订单
     */
    private void submitOrder() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<PayReturnInfo>>> call = userBiz.submitOrder(token,fdeliveryAddrId,goods_attr_id,queryId,from_type,payType,useConsumption);
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
                        if (from_type.equals("2")) {//从购物车获取
                            EventBus.getDefault().post(new ToCarMsgEvent());
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
                PayTask alipay = new PayTask(SubmitCarOrderActivity.this);
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
     *
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SubmitCarOrderActivity.this);
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
            UPPayAssistEx.startPay(SubmitCarOrderActivity.this, null, null, msg, mMode);
        }
    }

    /**
     * 支付成功
     */
    private void payScucess() {
        Intent intent = new Intent(mContext, PaySuccessActivity.class);
        intent.putExtra("pay_money", tv_shifukuan.getText().toString());
        startActivity(intent);
        MyApplication.finishSingleActivityByClass(GoodsDetalActivity.class);
        finish();
    }
    private void getParms() {

        ArrayList<OrderGoods> orderList = new ArrayList<OrderGoods>();
        OrderGoods orderGoods;
        StringBuilder sb = new StringBuilder();
        for (GoodsInfo goodsInfo1 : groupList) {
            sb.append(goodsInfo1.getGoods_attr_id()).append(",");
            orderGoods = new OrderGoods(goodsInfo1.getGoods_attr_id(), goodsInfo1.getNumber() + "");
            orderList.add(orderGoods);
        }
        goods_attr_id = sb.deleteCharAt(sb.length() - 1).toString();
        queryId = GsonUtil.toJsonString(orderList);

        generateOrder();
    }


    /**
     */
    private void setListviewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listitemView = listAdapter.getView(i, null, listView);
            listitemView.measure(0, 0);
            totalHeight += listitemView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 30;
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
    }


}
