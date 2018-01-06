package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengda.app.R;
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
import com.fengda.app.widget.MyListView;
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

import static android.R.attr.type;

/**
 * Created by 24448 on 2017/10/20.
 */

public class MyTeamListActivity extends BaseActivity implements PullLayout.OnRefreshListener {

    private TeamListAdapter adapter;
    private List<ExchangeBean> messageList = new ArrayList<>();
    @ViewInject(R.id.list_team)
    private MyListView listTeam;
    @ViewInject(R.id.product_refresh_view)
    private PullLayout pullLayout;
    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.tv_agent_level)
    private TextView tv_agent_level;

    @ViewInject(R.id.tv_agent_performance)
    private TextView tv_agent_performance;

    @ViewInject(R.id.tv_first_member_num)
    private TextView tv_first_member_num;

    @ViewInject(R.id.tv_second_member_num)
    private TextView tv_second_member_num;

    @ViewInject(R.id.tv_third_member_num)
    private TextView tv_third_member_num;

    @ViewInject(R.id.ll_first_member)
    private LinearLayout ll_first_member;

    @ViewInject(R.id.ll_second_member)
    private LinearLayout ll_second_member;

    @ViewInject(R.id.ll_third_member)
    private LinearLayout ll_third_member;

    @ViewInject(R.id.ll_phone_number)
    private LinearLayout ll_phone_number;


    @ViewInject(R.id.ll_team_to_fruit)
    private LinearLayout ll_team_to_fruit;

    @ViewInject(R.id.ll_team_fruit)
    private LinearLayout ll_team_fruit;

    @ViewInject(R.id.tv_team_fruit)
    private TextView tv_team_fruit;

    @ViewInject(R.id.tv_team_to_fruit)
    private TextView tv_team_to_fruit;

    @ViewInject(R.id.tv_phone_number)
    private TextView tv_phone_number;

    /**
     * 上下文
     **/
    private Context mContext;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_my_team_list);
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
        pullLayout.setOnRefreshListener(this);
        adapter = new TeamListAdapter(mContext, messageList);
        listTeam.setAdapter(adapter);
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
        ll_first_member.setGravity(Gravity.LEFT|Gravity.CENTER);
    }

    @Override
    protected void initEvents() {
//        getExchangeList(1);
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

    @Event({R.id.ll_team_to_fruit,R.id.ll_team_fruit})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_team_to_fruit://待产果
                    if (MyConstant.HASLOGIN) {
                        intent = new Intent(mContext, TeamFruitListActivity.class);
                        intent.putExtra("state", 1);
                    } else {
                        intent = new Intent(mContext, LoginActivity.class);

                    }
                break;
            case R.id.ll_team_fruit://产果
                    if (MyConstant.HASLOGIN) {
                        intent = new Intent(mContext, TeamFruitListActivity.class);
                        intent.putExtra("state", 2);
                    } else {
                        intent = new Intent(mContext, LoginActivity.class);

                    }
                break;
        }
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
