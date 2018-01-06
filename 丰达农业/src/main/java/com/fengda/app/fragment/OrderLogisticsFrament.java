package com.fengda.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fengda.app.R;

import com.fengda.app.adapter.OrderWuliuAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.Logistics;
import com.fengda.app.bean.LogisticsBase;
import com.fengda.app.bean.LogisticsCompany;
import com.fengda.app.bean.OrderGoodsInfo;
import com.fengda.app.bean.OrderMailInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ExpandListItemClickHelp;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.FileUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;

import com.fengda.app.view.statelayout.StateLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 物流
 */
@ContentView(R.layout.fragment_order_logistics)
public class OrderLogisticsFrament extends BaseFragment {

    private View mainView;
    private Context mContext;
    private int position;


    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.lv_wuliu)
    private ListView lv_wuliu;

    @ViewInject(R.id.tv_company)
    private TextView tv_company;

    @ViewInject(R.id.tv_wl_num)
    private TextView tv_wl_num;

    @ViewInject(R.id.tv_phone)
    private TextView tv_phone;

    private LoadingDialog dialog;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;

    private OrderWuliuAdapter adapter;
    private List<Logistics> wlList = new ArrayList<Logistics>();
    private String token;
    private String delivery_sn;
    private String order_sn;

    public OrderLogisticsFrament() {
        super();
    }

    public static OrderLogisticsFrament newInstance(int position, String delivery_sn, String order_sn) {
        OrderLogisticsFrament orderFrament = new OrderLogisticsFrament();
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putString("delivery_sn", delivery_sn);
        b.putString("order_sn", order_sn);
        orderFrament.setArguments(b);
        return orderFrament;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        position = getArguments().getInt("position");
        delivery_sn = getArguments().getString("delivery_sn");
        order_sn = getArguments().getString("order_sn");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = x.view().inject(this, inflater, container);
            setWidget();
            mContext = getActivity();
            dialog = new LoadingDialog(mContext, R.style.dialog, "加载中...");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            adapter = new OrderWuliuAdapter(getActivity(), wlList);
            lv_wuliu.setAdapter(adapter);
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
                getOrderWl(2);
            }
        });

        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderWl(2);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }

        dialog.show();
        getOrderWl(position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();// 这个必须这里初始化，要不会空指针


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
     * 查询物流
     *
     * @param state
     */
    private void getOrderWl(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<Object> call = userBiz.getExpressInfomation(order_sn,delivery_sn);
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> arg0,
                                   Response<Object> response) {
                dialog.dismiss();
                Object baseResponse = response.body();
                dialog.dismiss();
                if (state == 2) {
                    if (wlList != null) {
                        wlList.clear();
                    }
                }
                if (null == baseResponse || StringUtils.isBlank(baseResponse.toString())) {
                    stateLayout.showEmptyView("暂无物流信息");
                } else {
                    stateLayout.showContentView();
                    try {
                        String json = GsonUtil.GsonString(baseResponse);
                        LogisticsBase logisticsBase = GsonUtil.GsonToBean(json, LogisticsBase.class);
						updateState(logisticsBase);
                        if (state == 2) {
                            if (wlList != null) {
                                wlList.clear();
                            }
                        }
                        wlList = logisticsBase.getList();
						adapter.updatelistview(wlList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> arg0, Throwable arg1) {
                dialog.dismiss();
                stateLayout.showErrorView("");
                if (state == 1) {
                    dialog.dismiss();
                }
                ToastUtil.showToast(mContext, "请检查你的网络设置");
                stateLayout.showErrorView("请检查你的网络设置");
            }
        });

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getOrderWl(2);
        } else {// 未登录
            getActivity().finish();
            MyConstant.HASLOGIN = false;
        }
    }


    protected void updateState(LogisticsBase logisticsBase) {
        try{
            String  type=logisticsBase.getType();
            String json = FileUtils.readAssets(mContext, "wl.json");
            List<LogisticsCompany> logisticsCompanyList=GsonUtil.jsonToList(json,LogisticsCompany.class);
            for(LogisticsCompany logisticsCompany:logisticsCompanyList){

                String compType=logisticsCompany.getType();
                if(type.equalsIgnoreCase(compType)){
                    String fcompanyTel=logisticsCompany.getTel();
                    String name=logisticsCompany.getName();
                    tv_company.setText(name);
                    tv_phone.setText(fcompanyTel);
                    tv_wl_num.setText(delivery_sn);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }


}
