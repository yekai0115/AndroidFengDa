package com.fengda.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.adapter.LeaderboardListAdapter;
import com.fengda.app.adapter.TeamListAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.ExchangeBean;
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
import com.fengda.app.widget.GlideCircleTransform;

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

import static android.R.attr.type;

/**
 * Created by 24448 on 2017/10/20.
 */

public class MyLeaderboardActivity extends BaseActivity implements PullLayout.OnRefreshListener {

    private LeaderboardListAdapter adapter;
    private List<ExchangeBean> messageList = new ArrayList<>();
    @ViewInject(R.id.list_team_leaderboard)
    private PullableListView listLeaderboard;
    @ViewInject(R.id.product_refresh_view)
    private PullLayout pullLayout;
    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.tv_my_ranking)
    private TextView tv_my_ranking;

    @ViewInject(R.id.img_mine_logo)
    private ImageView img_mine_logo;

    @ViewInject(R.id.tv_team_mine_name)
    private TextView tv_team_mine_name;

    @ViewInject(R.id.tv_team_mine_leavel)
    private TextView tv_team_mine_leavel;

    @ViewInject(R.id.tv_team_mine_performance)
    private TextView tv_team_mine_performance;

    @ViewInject(R.id.img_back)
    private ImageView img_back;

    @ViewInject(R.id.img_my_ranking)
    private ImageView img_my_ranking;


    /**
     * 上下文
     **/
    private Context mContext;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_team_leaderboard);
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
        pullLayout.setOnRefreshListener(this);
        adapter = new LeaderboardListAdapter(mContext, messageList);
        listLeaderboard.setAdapter(adapter);
        listLeaderboard.canPullUp=false;
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getExchangeList(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getExchangeList(1);
            }
        });
        Glide.with(mContext).load(R.drawable.img_default_head)
                .fitCenter()
                .transform(new GlideCircleTransform(mContext))
                .placeholder(R.drawable.img_default_head)
                .error(R.drawable.img_default_head)
                .into(img_mine_logo);
        tv_my_ranking.setVisibility(View.GONE);
        img_my_ranking.setVisibility(View.VISIBLE);
        img_my_ranking.setImageResource(R.drawable.one);
    }

    @Override
    protected void initEvents() {
//        getExchangeList(1);
    }

    @Event({ R.id.img_back })
    private void click(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                defaultFinish();
                break;
            default:
                break;
        }

    }
    public void getExchangeList(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<ExchangeBean>>> call;
        if(type==MyConstant.EXCHANGE_CASH){//提现
            call = userBiz.getExchangeList(token);
        }else if(type==MyConstant.EXCHANGE_TRANSFEN){//转让
            call = userBiz.getExchangeList(token);
        }else{//复投
            call = userBiz.getExchangeList(token);
        }
        call.enqueue(new HttpCallBack<BaseResponse<List<ExchangeBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<ExchangeBean>>> arg0,
                                   Response<BaseResponse<List<ExchangeBean>>> response) {
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
                BaseResponse<List<ExchangeBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<ExchangeBean> data = baseResponse.getData();
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ExchangeBean>>> arg0, Throwable arg1) {
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
        getExchangeList(2);
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
            getExchangeList(1);
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
