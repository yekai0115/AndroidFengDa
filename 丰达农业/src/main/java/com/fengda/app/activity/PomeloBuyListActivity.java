package com.fengda.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.fengda.app.R;
import com.fengda.app.adapter.PomeloListAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.PomeloBuyBean;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by 24448 on 2017/10/20.
 */

public class PomeloBuyListActivity extends BaseActivity implements PullLayout.OnRefreshListener {

    private PomeloListAdapter adapter;
    private List<PomeloBuyBean> messageList = new ArrayList<>();
    @ViewInject(R.id.list_exchanges)
    private PullableListView listExchange;
    @ViewInject(R.id.product_refresh_view)
    private PullLayout pullLayout;
    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;
    /**
     * 上下文
     **/
    private Context mContext;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_exchange_list);
        mContext = this;
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        x.view().inject(this);
        // 注册事件
        EventBus.getDefault().register(this);
        initViews();
        initDialog();
        initEvents();
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
        topNvgBar.setTitle(getResources().getString(R.string.pomelo_record));
        pullLayout.setOnRefreshListener(this);
        adapter = new PomeloListAdapter(mContext, messageList);
        listExchange.setAdapter(adapter);
        listExchange.canPullUp=false;
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPomeloBuyList(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPomeloBuyList(1);
            }
        });
    }

    @Override
    protected void initEvents() {
        getPomeloBuyList(1);
    }


    public void getPomeloBuyList(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<PomeloBuyBean>>> call = userBiz.jinYouLogList(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<PomeloBuyBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<PomeloBuyBean>>> arg0,
                                   Response<BaseResponse<List<PomeloBuyBean>>> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if(state==2){
                    if(null!=messageList){
                        messageList.clear();
                    }
                    pullLayout.refreshFinish(PullLayout.SUCCEED);
                }
                super.onResponse(arg0, response);
                BaseResponse<List<PomeloBuyBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<PomeloBuyBean> data = baseResponse.getData();
                        if (data != null && data.size() > 0) {
                            messageList.addAll(data);
                            stateLayout.showContentView();
                        } else {
                            stateLayout.showEmptyView("暂无数据");
                        }
                    }else{
                        String desc = baseResponse.getMsg();
                        ToastUtil.showToast(mContext,desc);
                    }
                    adapter.updateListview(messageList);
//                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<PomeloBuyBean>>> arg0, Throwable arg1) {
                dialog.dismiss();
                stateLayout.showErrorView("服务器连接失败,请检查您的网络状态");
                if(state==2){
                    pullLayout.refreshFinish(PullLayout.FAIL);
                }
            }
        });


    }

    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        // 下拉刷新操作
        getPomeloBuyList(2);
    }

    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {


    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {

        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getPomeloBuyList(1);
        } else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
