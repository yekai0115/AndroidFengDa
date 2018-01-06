package com.fengda.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fengda.app.eventbus.OrderUpdateEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.fengda.app.R;
import com.fengda.app.activity.OrderDetalActivity;
import com.fengda.app.activity.OrderWuliuActivity;
import com.fengda.app.adapter.MailOrderAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.OrderGoodsInfo;
import com.fengda.app.bean.OrderMailInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ExpandListItemClickHelp;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableExpandableListView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.statelayout.StateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.POST;


/**
 * 商城订单
 */
@ContentView(R.layout.fragment_mail_order)
public class MailOrderFrament extends BaseFragment implements PullLayout.OnRefreshListener, ExpandListItemClickHelp {

    private View mainView;
    private Context context;
    private int position;


    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.lv_order)
    private PullableExpandableListView lv_order;
    @ViewInject(R.id.refresh_view)
    private PullLayout ptrl;

    private LoadingDialog dialog;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private MailOrderAdapter adapter;
    private List<OrderMailInfo> orderInfoList = new ArrayList<OrderMailInfo>();
    private String token;

    public MailOrderFrament() {
        super();
    }

    public static MailOrderFrament newInstance(int position) {
        MailOrderFrament orderFrament = new MailOrderFrament();
        Bundle b = new Bundle();
        b.putInt("position", position);
        orderFrament.setArguments(b);
        return orderFrament;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(context, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = x.view().inject(this, inflater, container);
            setWidget();
            ptrl.setOnRefreshListener(this);
            // ptrl.autoRefresh(); //去掉自动刷新
            context = getActivity();
            dialog = new LoadingDialog(context, R.style.dialog, "加载中...");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            adapter = new MailOrderAdapter(getActivity(), orderInfoList, this);
            lv_order.setAdapter(adapter);
            lv_order.needPullUp = false;
            isPrepared = true;
            lazyLoad();
        }
        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeView(mainView);
        }
        return mainView;
    }

    private void setWidget() {
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderList(2);
            }
        });

        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderList(2);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }

        dialog.show();
        getOrderList(1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();// 这个必须这里初始化，要不会空指针


    }

    // 进入采购详情返回后刷新
    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    /**
     * 订单列表
     *
     * @param state
     */
    private void getOrderList(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<OrderMailInfo>>> call = userBiz.getMainOrderList(token, position);
        call.enqueue(new HttpCallBack<BaseResponse<List<OrderMailInfo>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<OrderMailInfo>>> arg0,
                                   Response<BaseResponse<List<OrderMailInfo>>> response) {
                mHasLoadedOnce = true;   //没有这个每次滑动的时候都会刷新
                dialog.dismiss();
                if (state == 2) {
                    if (orderInfoList != null) {
                        orderInfoList.clear();
                    }
                    ptrl.refreshFinish(PullLayout.SUCCEED);
                }
                super.onResponse(arg0, response);
                BaseResponse<List<OrderMailInfo>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<OrderMailInfo> data = baseResponse.getData();
                        Gson gson = new Gson();
                        for (int i = 0; i < data.size(); i++) {
                            List<OrderGoodsInfo> goodsInfo = gson.fromJson(data.get(i).getGoods_info(), new TypeToken<List<OrderGoodsInfo>>() {
                            }.getType());
//                            List<OrderGoodsInfo> goodsInfo = GsonUtil.GsonToList(data.get(i).getGoods_info(), OrderGoodsInfo.class);
                            data.get(i).setGoods_info_list(goodsInfo);
                        }
                        if (null == data || data.isEmpty()) {
                            stateLayout.showEmptyView("暂无数据");
                            adapter.notifyDataSetChanged();
                        } else {
                            orderInfoList.addAll(data);
                            stateLayout.showContentView();
                            adapter.updateListView(orderInfoList);
                            int groupCount = adapter.getGroupCount();
                            for (int i = 0; i < groupCount; i++) {
                                lv_order.expandGroup(i);
                            }
                        }
                    } else {
                        adapter.notifyDataSetChanged();
                        ToastUtil.showToast(context, desc);
                    }
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<OrderMailInfo>>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                stateLayout.showErrorView("");
                if (state == 1) {
                    dialog.dismiss();
                }
                if (state == 2) {
                    // 刷新完成调用
                    ptrl.refreshFinish(PullLayout.FAIL);
                }
                ToastUtil.showToast(context, "网络状态不佳,请检查您的网络设置");
            }
        });
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
                    ToastUtil.showToast(context, desc);
                    if (retCode == MyConstant.SUCCESS) {
                        ptrl.autoRefresh();
                        getOrderList(2);
                        EventBus.getDefault().post(new OrderUpdateEvent());//通知个人页面更新数据
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(context, "网络状态不佳,请检查您的网络设置");
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
                    ToastUtil.showToast(context, desc);
                    if (retCode == MyConstant.SUCCESS) {
                        ptrl.autoRefresh();
                        getOrderList(2);
                        EventBus.getDefault().post(new OrderUpdateEvent());//通知个人页面更新数据
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(context, "网络状态不佳,请检查您的网络设置");
            }
        });
    }



    /**
     * 下拉
     */
    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        getOrderList(2);

    }

    /**
     * 上拉
     */
    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 自动匹配listview的高度
     *
     * @param
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


    @Override
    public void onClick(String id) {

    }

    @Override
    public void onCheckClick(View item, View widget, int groupPosition, int position, int which) {
        OrderMailInfo orderInfo = orderInfoList.get(groupPosition);
        int status = Integer.valueOf(orderInfoList.get(groupPosition).getDelivery_status());//0待支付,1待发货,2待收货,3已签收；4已取消(作废)
        String order_sn = orderInfo.getOrder_sn();
        OrderGoodsInfo goodsInfo = orderInfo.getGoods_info_list().get(position);
        String goods_logo = goodsInfo.getPic();
        String[] arr = goods_logo.split(",");
        goods_logo = arr == null || arr.length == 0 ? "" : arr[0];
        Intent intent;
        switch (which) {
            case R.id.tv_wuliu://查看物流 取消订单
                if (status == 0) {//待支付，点击取消订单
                    showConfirmReceiptOrCancleDialog(2, order_sn, getResources().getString(R.string.tishi91));
                } else if (status == 2) {//待收货,点击查看物流
                    intent = new Intent(context, OrderWuliuActivity.class);
                    intent.putExtra("delivery_sn", orderInfo.getDelivery_sn());//物流单号
                    intent.putExtra("order_sn", order_sn);//订单号
                    startActivity(intent);
                }
                break;
            case R.id.tv_shouhuo://立即收货
                if (status == 0) {//待支付，点击立即支付
                    intent = new Intent(context, OrderDetalActivity.class);
                    intent.putExtra("order_sn", order_sn);
                    intent.putExtra("delivery_sn", orderInfo.getDelivery_sn());//物流单号
                    intent.putExtra("status", orderInfo.getDelivery_status());//订单状态
                    intent.putExtra("goods_logo", goods_logo);
                    startActivity(intent);
                } else if (status == 2) {//待收货,点击确认收货
                    showConfirmReceiptOrCancleDialog(1, order_sn, getResources().getString(R.string.tishi_66));
                }
                break;
            case R.id.ll_detal://查看详情
                intent = new Intent(context, OrderDetalActivity.class);
                intent.putExtra("order_sn", order_sn);
                intent.putExtra("delivery_sn", orderInfo.getDelivery_sn());//物流单号
                intent.putExtra("status", orderInfo.getDelivery_status());//订单状态
                intent.putExtra("goods_logo", goods_logo);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateData() {

    }

    //订单有操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(OrderUpdateEvent orderUpdateEvent) {
        getOrderList(2);    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(context, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(context, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getOrderList(2);
        } else {// 未登录
            getActivity().finish();
            MyConstant.HASLOGIN = false;
        }
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
                    if(type==1){
                        confirmReceipt(order_sn);
                    }else if(type==2){
                        cancelOrder(order_sn);
                    }

                }

            }
        });
        alert.showDialog(getActivity(), tile, "确定", "取消");
    }

}
