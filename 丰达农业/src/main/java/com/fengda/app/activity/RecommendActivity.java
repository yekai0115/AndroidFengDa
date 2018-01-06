package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.adapter.RecommendAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.RecomFriend;
import com.fengda.app.bean.RecomFriendBase;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar5;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 好友管理页面
 */
public class RecommendActivity extends BaseActivity implements PullLayout.OnRefreshListener {


    // 上下文
    private Context context;
    // intent
    private Intent intent;

    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.tv_friends_num)
    private TextView tv_friends_num;

    @ViewInject(R.id.lv_friends)
    private PullableListView lv_friends;
    @ViewInject(R.id.refresh_view)
    private PullLayout ptrl;
    private RecommendAdapter adapter;
    private List<RecomFriend> friendList = new ArrayList<RecomFriend>();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        MyApplication.addActivity(this);
        x.view().inject(this);
        context = this;
        intent = getIntent();
        initDialog();
        initViews();
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
        token = (String) SPUtils.get(context, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        adapter = new RecommendAdapter(this, friendList);
        lv_friends.setAdapter(adapter);
        ptrl.setOnRefreshListener(this);
    }

    @Override
    protected void initEvents() {
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriendsList(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriendsList(1);
            }
        });
        getFriendsList(1);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 监听返回键
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @param state
     */
    private void getFriendsList(final int state) {
//        stateLayout.showProgressView();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<RecomFriendBase>>> call = userBiz.getFriends(token, 1);
        call.enqueue(new HttpCallBack<BaseResponse<List<RecomFriendBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<RecomFriendBase>>> arg0,
                                   Response<BaseResponse<List<RecomFriendBase>>> response) {
                dialog.dismiss();
                if (state == 2) {
                    if (friendList != null) {
                        friendList.clear();
                    }
                    ptrl.refreshFinish(PullLayout.SUCCEED);
                }
                super.onResponse(arg0,response);
                BaseResponse<List<RecomFriendBase>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<RecomFriendBase> data = baseResponse.getData();
                        if (null == data || data.isEmpty()) {
                            stateLayout.showEmptyView("暂无数据");
                            ptrl.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            stateLayout.showContentView();
                            ptrl.setVisibility(View.VISIBLE);
                            RecomFriendBase recomFriendBase=data.get(0);
                            List<RecomFriend>  items =recomFriendBase.getItems();
                            adapter.updateListView(items,recomFriendBase.getHeadurl());
                        }
                    } else {
                        ToastUtil.showToast(context, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<RecomFriendBase>>> arg0,
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
     * 下拉
     */
    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        getFriendsList(2);

    }

    /**
     * 上拉
     */
    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {


    }
}


