package com.fengda.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.fengda.app.R;
import com.fengda.app.adapter.OrderDetalLogisticsAdapter;
import com.fengda.app.adapter.OrderDetalsAdapter;
import com.fengda.app.alipay.PayResult;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.Address;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.GoodsOrderInfo;
import com.fengda.app.bean.LogisticsBase;
import com.fengda.app.bean.OrderDetals;
import com.fengda.app.bean.OrderGoodsInfo;
import com.fengda.app.bean.PayData;
import com.fengda.app.bean.PayReturnInfo;
import com.fengda.app.bean.PayTypeControl;
import com.fengda.app.bean.WxPayResult;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.eventbus.AliPayMsg;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.OrderUpdateEvent;
import com.fengda.app.eventbus.WeiXinPayMsg;
import com.fengda.app.eventbus.YinLianPayMsg;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.DateUtil;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableRefreshScrollView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.MyListView;
import com.fengda.app.widget.TopNvgBar5;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 描述 ：订单详情页面
 */
public class OrderDetalActivity extends BaseActivity implements PullLayout.OnRefreshListener {


    /**
     * 实付款
     */
    @ViewInject(R.id.tv_shifukuan)
    private TextView tv_shifukuan;

    /**
     * 订单状态
     */
    @ViewInject(R.id.tv_order_state)
    private TextView tv_order_state;


    /**
     * 收件人姓名
     */
    @ViewInject(R.id.tv_sh_name)
    private TextView tv_sh_name;
    /**
     * 收件人电话
     */
    @ViewInject(R.id.tv_phone)
    private TextView tv_phone;
    /**
     * 收件人地址
     */
    @ViewInject(R.id.tv_address)
    private TextView tv_address;


    /**
     * 实付金额
     */
    @ViewInject(R.id.tv_shifu)
    private TextView tv_shifu;
    /**
     * 订单号
     */
    @ViewInject(R.id.tv_order_id)
    private TextView tv_order_id;
    /**
     * 下单时间
     */
    @ViewInject(R.id.tv_buy_time)
    private TextView tv_buy_time;
    /**
     * 支付时间
     */
    @ViewInject(R.id.tv_pay_time)
    private TextView tv_pay_time;
    /**
     * 发货时间
     */
    @ViewInject(R.id.tv_fahuo_time)
    private TextView tv_fahuo_time;

    /**
     * 发货时间
     */
    @ViewInject(R.id.tv_shouhuo_time)
    private TextView tv_shouhuo_time;

    /***/
    @ViewInject(R.id.tv_use_point)
    private TextView tv_use_point;

    @ViewInject(R.id.refresh_view)
    private PullLayout refresh_view;
    @ViewInject(R.id.mScrollView)
    private PullableRefreshScrollView mScrollView;


    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.lv_Order)
    private MyListView lv_Order;


    @ViewInject(R.id.lv_wl)
    private ListView lv_wl;

    @ViewInject(R.id.view_bland)
    private View view_bland;



    @ViewInject(R.id.btn_cance)
    private Button btn_cance;

    @ViewInject(R.id.btn_operate)
    private TextView btn_operate;

    @ViewInject(R.id.tv_postage)
    private TextView tv_postage;

    @ViewInject(R.id.tv_subtotal)
    private TextView tv_subtotal;

    @ViewInject(R.id.tv_deduction)
    private TextView tv_deduction;

    @ViewInject(R.id.ll_deduction)
    private RelativeLayout ll_deduction;

    /**
     * 上下文
     **/
    private Context mContext;

    private Intent intent;
    /**
     * 订单号
     */
    private String order_sn;
    /**
     * 订单状态
     */
    private int status;

    @ViewInject(R.id.ll_paytime)
    private LinearLayout ll_paytime;

    @ViewInject(R.id.ll_fahuo_time)
    private LinearLayout ll_fahuo_time;

    @ViewInject(R.id.ll_shouhuo_time)
    private LinearLayout ll_shouhuo_time;

    @ViewInject(R.id.ll_botttom)
    private RelativeLayout ll_botttom;

    @ViewInject(R.id.ll_show_payTypr)
    private LinearLayout ll_show_payTypr;

    @ViewInject(R.id.rl_yinlian)
    private RelativeLayout rl_yinlian;

    @ViewInject(R.id.rl_zhifubao)
    private RelativeLayout rl_zhifubao;

    @ViewInject(R.id.rl_weixin)
    private RelativeLayout rl_weixin;

    private String delivery_sn;

    @ViewInject(R.id.cb_weixin)
    private CheckBox cb_weixin;
    @ViewInject(R.id.cb_zhifubao)
    private CheckBox cb_zhifubao;
    @ViewInject(R.id.cb_card)
    private CheckBox cb_card;
    private int payType;

    //微信支付
    private PayReq req;
    private IWXAPI api;
    private final String mMode = "00";
    /**
     * 物流单号
     */
    private String token;
    private List<OrderGoodsInfo> goodsInfoList = new ArrayList<>();
    private OrderDetalsAdapter adapter;
    private TopNvgBar5 topNvgBar;

    private OrderDetalLogisticsAdapter logisticsAdapter;
    private List<LogisticsBase> logisticsBaseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detal);
        mContext = this;
        x.view().inject(this);
        EventBus.getDefault().register(this);
        initDialog();
        intent = getIntent();
        status = Integer.valueOf(intent.getStringExtra("status"));
        order_sn = intent.getStringExtra("order_sn");
        delivery_sn = intent.getStringExtra("delivery_sn");
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        refresh_view.setOnRefreshListener(this);
        adapter = new OrderDetalsAdapter(mContext, goodsInfoList);
        lv_Order.setAdapter(adapter);
        logisticsAdapter = new OrderDetalLogisticsAdapter(mContext, logisticsBaseList);
        lv_wl.setAdapter(logisticsAdapter);
        initViews();
        getOrderDetail(1);
        mScrollView.smoothScrollTo(0, 0);//避免
        tv_order_id.setText(order_sn);
    }


    @Override
    protected void initEvents() {

    }


    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        getOrderDetail(2);
    }

    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {

    }

    @Override
    protected void initViews() {
        topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                showConfirmReceiptOrCancleDialog(2, order_sn, getResources().getString(R.string.tishi91));
            }
        });
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderDetail(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderDetail(1);
            }
        });

        lv_wl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(mContext, OrderWuliuActivity.class);
                intent.putExtra("delivery_sn", delivery_sn);//物流单号
                intent.putExtra("order_sn", order_sn);//订单号
                intent.putExtra("position", i);//
                startActivity(intent);
            }
        });

        req = new PayReq();
        api = WXAPIFactory.createWXAPI(mContext, WxConstants.APP_ID);
        api.registerApp(WxConstants.APP_ID);// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
    }

    /**
     * 查询订单详细
     */
    private void getOrderDetail(final int state) {
        dialog.show();
        if (state == 1) {
            stateLayout.showProgressView();
        }
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<OrderDetals>>> call = userBiz.myOrderListDetail(token, order_sn);
        call.enqueue(new HttpCallBack<BaseResponse<List<OrderDetals>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<OrderDetals>>> arg0,
                                   Response<BaseResponse<List<OrderDetals>>> response) {
                super.onResponse(arg0, response);
                dialog.dismiss();
                if (state == 2) {
                    refresh_view.refreshFinish(PullLayout.SUCCEED);
                }
                BaseResponse<List<OrderDetals>> baseResponse = response.body();
                if (null != baseResponse) {
                    String msg = baseResponse.getMsg();
                    int code = baseResponse.getCode();
                    if (code == MyConstant.SUCCESS) {
                        if (baseResponse.getData().size() > 0) {
                            OrderDetals orderDetals = baseResponse.getData().get(0);
                            Address addr_info = orderDetals.getAddr_info();
                            GoodsOrderInfo goods_info = orderDetals.getGoods_info();
                            PayData payData = orderDetals.getPayData();
                            tv_sh_name.setText(addr_info.getAddr_name());
                            tv_phone.setText(addr_info.getAddr_mobile());
                            tv_address.setText(addr_info.getAddr_province() + addr_info.getAddr_city() + addr_info.getAddr_county() + addr_info.getAddr_detail());
                            goodsInfoList = GsonUtil.jsonToList(goods_info.getGoods_info(), OrderGoodsInfo.class);
                            adapter.updateListview(goodsInfoList);
                            setListviewHeight(lv_Order);
                            delivery_sn = payData.getDelivery_sn();
                            tv_shifu.setText(goods_info.getTotalprice());
                            tv_shifukuan.setText(goods_info.getTotalprice());
                            tv_buy_time.setText(payData.getAdd_time());
                            tv_order_id.setText(payData.getOrder_sn());
                            tv_pay_time.setText(payData.getPaytime());
                            tv_fahuo_time.setText(payData.getBuildtime());
                            tv_shouhuo_time.setText(payData.getUpdate_time());
                            tv_use_point.setText(goods_info.getSubtotal());
                            tv_postage.setText(goods_info.getPostage());
                            tv_subtotal.setText(CompuUtils.add(goods_info.getSubtotal(), goods_info.getPostage()).toString());
                            if (StringUtils.isBlank(goods_info.getOrder_consumption())) {
                                ll_deduction.setVisibility(View.GONE);
                            } else {
                                tv_deduction.setText(goods_info.getOrder_consumption());
                            }
                            status = payData.getDelivery_status();
                            stateLayout.showContentView();
                            if (status == 0) {//待支付
                                tv_order_state.setText("等待付款");
                                topNvgBar.setRight("取消订单");
                                topNvgBar.setRightVisibility(View.VISIBLE);
                                lv_wl.setVisibility(View.GONE);
                                view_bland.setVisibility(View.GONE);
                                btn_cance.setVisibility(View.GONE);
                                btn_operate.setText("付款");
                                ll_show_payTypr.setVisibility(View.VISIBLE);
                                showPayType(orderDetals.getAuth());
                            } else if (status == 1) {//待发货
                                tv_order_state.setText("待发货");
                                topNvgBar.setRightVisibility(View.GONE);
                                ll_botttom.setVisibility(View.GONE);
                                btn_operate.setVisibility(View.GONE);
                                ll_paytime.setVisibility(View.VISIBLE);
                                ll_show_payTypr.setVisibility(View.GONE);
                                lv_wl.setVisibility(View.GONE);
                                view_bland.setVisibility(View.GONE);
                            } else if (status == 2) {//待收货
                                tv_order_state.setText("待收货");
                                topNvgBar.setRightVisibility(View.GONE);
                                lv_wl.setVisibility(View.VISIBLE);
                                view_bland.setVisibility(View.VISIBLE);
                                btn_cance.setText("查看物流");
                                btn_operate.setText("确认收货");
                                ll_paytime.setVisibility(View.VISIBLE);
                                ll_fahuo_time.setVisibility(View.VISIBLE);
                                ll_show_payTypr.setVisibility(View.GONE);
                            } else if (status == 3) {//已签收
                                tv_order_state.setText("已签收");
                                topNvgBar.setRightVisibility(View.GONE);
                                lv_wl.setVisibility(View.VISIBLE);
                                view_bland.setVisibility(View.VISIBLE);
                                btn_cance.setVisibility(View.GONE);
                                ll_paytime.setVisibility(View.VISIBLE);
                                ll_fahuo_time.setVisibility(View.VISIBLE);
                                ll_shouhuo_time.setVisibility(View.VISIBLE);
                                btn_operate.setVisibility(View.VISIBLE);
                                ll_show_payTypr.setVisibility(View.GONE);
                                btn_operate.setText("查看物流");
                            } else {
                                tv_order_state.setText("已取消");
                                lv_wl.setVisibility(View.GONE);
                                view_bland.setVisibility(View.GONE);
                                topNvgBar.setRightVisibility(View.GONE);
                                ll_botttom.setVisibility(View.GONE);
                                btn_cance.setVisibility(View.GONE);
                                btn_operate.setVisibility(View.GONE);
                                ll_show_payTypr.setVisibility(View.GONE);
                            }
                            logisticsBaseList = orderDetals.getDeliver_list();
                            if (null == logisticsBaseList || logisticsBaseList.isEmpty()) {
                                LogisticsBase logisticsBase = new LogisticsBase("", DateUtil.getStandardTime(System.currentTimeMillis()), "处理中");
                                logisticsBaseList.add(logisticsBase);
                            }
                            logisticsAdapter.updateListView(logisticsBaseList);
                            setListviewHeight(lv_wl);
                        } else {
                            ToastUtil.showToast(mContext, baseResponse.getMsg());
                        }
                    } else {
                        stateLayout.showEmptyView(msg);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<OrderDetals>>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "请检查你的网络设置");
                stateLayout.showErrorView("请检查你的网络设置");
                if (state == 2) {
                    refresh_view.refreshFinish(PullLayout.FAIL);
                }
            }
        });

    }

    private void showPayType(PayTypeControl payTypeControl) {
        String alipay = payTypeControl.getAlipay_auth();
        String wxpay = payTypeControl.getWx_auth();
        String cardpay = payTypeControl.getCard_pay();
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
    }

    @Event({R.id.ll_wl, R.id.btn_cance, R.id.btn_operate, R.id.rl_weixin,
            R.id.rl_zhifubao, R.id.rl_yinlian})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ll_wl:
                if (status == 2 || status == 3) {//待收货，已签收
                    intent = new Intent(mContext, OrderWuliuActivity.class);
                    intent.putExtra("delivery_sn", delivery_sn);//物流单号
                    intent.putExtra("order_sn", order_sn);//订单号
                    startActivity(intent);
                }
                break;
            case R.id.btn_cance:
                if (status == 0) {//待支付，点击取消订单
                    showConfirmReceiptOrCancleDialog(2, order_sn, getResources().getString(R.string.tishi91));
                } else if (status == 2) {//待收货,点击查看物流
                    queryDelivery();
                }
                break;
            case R.id.btn_operate:
                if (status == 0) {//待支付，点击立即支付
                    if(payType==0){
                        ToastUtil.showToast(mContext, "请选择支付方式");
                        return;
                    }else{//支付订单
                        payOrder();
                    }
                    break;
                } else if (status == 2) {//待收货,点击确认收货
                    showConfirmReceiptOrCancleDialog(1, order_sn, getResources().getString(R.string.tishi_66));
                } else if (status == 3) {//已签收,点击查看物流
                    queryDelivery();
                }
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
        }
    }

    private void queryDelivery() {
        if (StringUtils.isBlank(delivery_sn) || delivery_sn.equals("0")) {
            ToastUtil.showToast(mContext, "暂无物流信息");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("delivery_sn", delivery_sn);//物流单号
        intent.putExtra("order_sn", order_sn);
        intent.setClass(OrderDetalActivity.this, OrderWuliuActivity.class);
        startActivity(intent);
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
            getOrderDetail(1);
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
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


    /**
     * 确认收货,取消订单对话框
     */
    private void showConfirmReceiptOrCancleDialog(final int type, final String order_sn, String tile) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {
                    dialog.show();
                    if (type == 1) {
                        confirmReceipt(order_sn);
                    } else if (type == 2) {
                        cancelOrder(order_sn);
                    }

                }

            }
        });
        alert.showDialog(OrderDetalActivity.this, tile, "确定", "取消");
    }


    /**
     * 确认收货
     *
     * @param order_sn
     */
    private void confirmReceipt(String order_sn) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.confirmReceipt(token, order_sn);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {

                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    ToastUtil.showToast(mContext, desc);
                    if (retCode == MyConstant.SUCCESS) {

                        getOrderDetail(2);
                        EventBus.getDefault().post(new OrderUpdateEvent());//通知个人页面更新数据
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请检查您的网络设置");
            }
        });
    }


    /**
     * 取消订单
     *
     * @param order_sn
     */
    private void cancelOrder(String order_sn) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.cancelOrder(token, order_sn);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {

                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    ToastUtil.showToast(mContext, desc);
                    if (retCode == MyConstant.SUCCESS) {
                        getOrderDetail(2);
                        EventBus.getDefault().post(new OrderUpdateEvent());//通知个人页面更新数据
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请检查您的网络设置");
            }
        });
    }

    /**
     * 支付待支付订单
     */
    private void payOrder() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<PayReturnInfo>>> call = userBiz.payOrder(token,order_sn,payType);
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
                PayTask alipay = new PayTask(OrderDetalActivity.this);
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
            getOrderDetail(2);
            EventBus.getDefault().post(new OrderUpdateEvent());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetalActivity.this);
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
            UPPayAssistEx.startPay(OrderDetalActivity.this, null, null, msg, mMode);
        }
    }

    /**
     * 支付成功
     */
    private void payScucess() {
        Intent intent = new Intent(mContext, PaySuccessActivity.class);
        intent.putExtra("pay_money", tv_shifukuan.getText().toString());
        startActivity(intent);
        finish();
    }
}
