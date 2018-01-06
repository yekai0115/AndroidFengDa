package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.fengda.app.R;
import com.fengda.app.adapter.AddressMangeAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.Address;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent12;
import com.fengda.app.eventbus.MsgEvent6;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar5;

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
 * 描述 ：收货人地址列表管理
 */
public class AddressMangeActivity extends BaseActivity implements
        ListItemClickHelp, PullToRefreshLayout.OnRefreshListener {


    @ViewInject(R.id.des_refresh_view)
    private PullToRefreshLayout des_refresh_view;
    /**
     * 收货列表
     */
    @ViewInject(R.id.des_listview)
    private PullableListView des_listview;
    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;
    /**
     * 上下文
     **/
    private Context mContext;
    private LoadingDialog dialog;
    /**
     * 收货地址数据
     **/
    private List<Address> addressList = new ArrayList<Address>();
    private AddressMangeAdapter adapter;
    private Intent intent;

    /**
     * 1：我的页面-个人中心页面（点击地址管理）-进来
     * 2:从确认订单页面——选择地址页面（点击地址管理）-进来；
     */
    private int fromType;

    /**
     * 1：删除地址、改变了默认地址、编辑了地址，新增了地址，返回选择地址页面要刷新
     */
    private int resultType = 0;

    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_mange);
        mContext = this;
        x.view().inject(this);
        setWidget();
        // 注册事件
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        initDialog();
        adapter = new AddressMangeAdapter(mContext, addressList, this);
        des_listview.setAdapter(adapter);
        des_listview.canPullUp = false;
        des_refresh_view.setOnRefreshListener(this);
        intent = getIntent();
        fromType = intent.getIntExtra("fromType", 1);
        getAcceptAddressList(1);
    }

    protected void initDialog() {
        dialog = new LoadingDialog(this, R.style.dialog, "请稍候...");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

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
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  stateLayout.showProgressView();
                getAcceptAddressList(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  stateLayout.showProgressView();
                getAcceptAddressList(1);
            }
        });
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initViews() {

    }

    @Event({R.id.tv_add})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.tv_add:// 添加新地址
                intent = new Intent(mContext, AddressEditActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("fromType", fromType);
                startActivityForResult(intent, 1);
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
        super.onDestroy();
        if (null != addressList) {
            addressList.clear();
            addressList = null;
        }
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View item, View widget, int position, int which) {
        Address bean = addressList.get(position);
        int check = bean.getAddr_primary();
        String id = bean.getAddr_id();
        switch (which) {
            case R.id.cb_chose://选项框
                for (int i = 0; i < addressList.size(); i++) {
                    if (i == position) {
                        bean.setAddr_primary(1);
                    } else {
                        addressList.get(i).setAddr_primary(0);
                    }
                }
                deliveryAdrDefault(id);
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_bianji://编辑
                intent = new Intent(mContext, AddressEditActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("fromType", fromType);
                intent.putExtra("place", bean);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_delete://删除
                showDeleteMessage("确定要删除吗", id);
                break;
            default:
                break;
        }

    }

    /**
     * 删除地址对话框
     *
     * @param string
     * @param id
     */
    private void showDeleteMessage(String string, final String id) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {
                    dialog.show();
                    deleteAdd(id);
                }

            }
        });
        alert.showDialog(AddressMangeActivity.this, string, "确定", "取消");

    }


    /**
     * 删除地址
     *
     * @param id
     */
    private void deleteAdd(String id) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.deliveryAdrDel(token, id);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0,response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        resultType = 1;
                        EventBus.getDefault().post(new MsgEvent12());//地址选择页面数据更新
                        getAcceptAddressList(2);//重新请求地址列表
                    }else {
                        	ToastUtil.showToast(mContext, baseResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    /**
     * 获取收货地址列表
     *
     * @param state
     */
    private void getAcceptAddressList(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<Address>>> call = userBiz.getAcceptAddressRepo(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<Address>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Address>>> arg0,
                                   Response<BaseResponse<List<Address>>> response) {
                dialog.dismiss();
                des_listview.setVisibility(View.VISIBLE);
                if (state == 2) {
                    if (addressList != null) {
                        addressList.clear();
                    }
                    des_refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                super.onResponse(arg0,response);
                BaseResponse<List<Address>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        addressList = baseResponse.getData();
                        if (null == addressList || addressList.isEmpty()) {
                            stateLayout.showEmptyView();
                            stateLayout.showEmptyView("暂无数据");
                        } else {
                            stateLayout.showContentView();
                        }
                        adapter.updateListView(addressList);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Address>>> arg0, Throwable arg1) {
                dialog.dismiss();
                stateLayout.showErrorView();
                stateLayout.showErrorView("网络错误");
                if (state == 2) {
                    // 刷新完成调用
                    des_refresh_view.refreshFinish(PullToRefreshLayout.FAIL);
                }

                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    /**
     * 设置默认收货地址
     *
     * @param id
     */
    private void deliveryAdrDefault(String id) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.deliveryAdrDefault(token, id);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0,response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        EventBus.getDefault().post(new MsgEvent12());
                        getAcceptAddressList(2);//重新请求地址列表
                    } else if (retCode == MyConstant.ERR_AUTH) {//token过期
                        //ToastUtil.showReLogin(mContext);
                    } else {
                        //	ToastUtil.showToast(mContext, desc);
                    }
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
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

        getAcceptAddressList(2);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
            String login = (String) SPUtils.get(mContext, "login", "");
            if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
                MyConstant.HASLOGIN = true;
                token = (String) SPUtils.get(mContext, "token", "");
                token = EncodeUtils.base64Decode2String(token);
                getAcceptAddressList(2);
            } else {// 未登录
				finish();
				MyApplication.finishSingleActivityByClass(SettingActivity.class);
				MyConstant.HASLOGIN = false;
            }
    }

    //地址编辑页面有操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent6 messageEvent) {
        getAcceptAddressList(2);//重新请求地址列表
    }

}
