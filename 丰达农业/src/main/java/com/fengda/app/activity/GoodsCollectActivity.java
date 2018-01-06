package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.adapter.CollectAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.FavoriteShop;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.AnimatorUtils;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.swipe.SwipeMenu;
import com.fengda.app.view.pullableview.swipe.SwipeMenuCreator;
import com.fengda.app.view.pullableview.swipe.SwipeMenuItem;
import com.fengda.app.view.pullableview.swipe.SwipeMenuListView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;
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
 * 商品收藏列表页面
 */
public class GoodsCollectActivity extends BaseActivity implements
        PullLayout.OnRefreshListener, ListItemClickHelp {

    private Context mContext;

    @ViewInject(R.id.top_nvg_bar)
    private TopNvgBar5 topNvgBar;
    @ViewInject(R.id.rl_delete)
    private RelativeLayout rl_delete;

    @ViewInject(R.id.cb_all)
    private CheckBox cb_all;

    @ViewInject(R.id.tv_delete)
    private TextView tv_delete;
    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.refresh_view)
    private PullLayout refresh_view;

    @ViewInject(R.id.lv_goods)
    private SwipeMenuListView lv_goods;
    private boolean isEdit = true;
    /**
     * 被选中的数量
     */
    private int checkNumber = 0;
    private CollectAdapter adapter;

    /**
     * 判断是否全选
     */
    private boolean isSelectAll;

    /**
     * 收藏数据
     */
    private List<FavoriteShop> collectList = new ArrayList<FavoriteShop>();
    /**
     * 选中的数据
     */
    private ArrayList<FavoriteShop> checkList = new ArrayList<FavoriteShop>();
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_collect);
        x.view().inject(this);
        // 注册事件
        EventBus.getDefault().register(this);
        mContext = this;
        initViews();
        initDialog();
        getCollectList(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void setSlidr() {
        int primary = getResources().getColor(R.color.toming);
        int secondary = getResources().getColor(R.color.accent);
        SlidrConfig config = new SlidrConfig.Builder().primaryColor(primary)
                .secondaryColor(secondary).position(SlidrPosition.LEFT)
                .touchSize(DimenUtils.dip2px(mContext, 60)).build();
        Slidr.attach(this, config);
    }

    @Override
    protected void initViews() {
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        adapter = new CollectAdapter(mContext, collectList, this);
        lv_goods.setAdapter(adapter);
        refresh_view.setOnRefreshListener(this);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

                if (isEdit) {
                    topNvgBar.setRight("完成");
                    isEdit = false;
                    rl_delete.setVisibility(View.VISIBLE);
                    rl_delete.setAnimation(AnimatorUtils.moveToViewLocation());
                } else if (!isEdit) {
                    isEdit = true;
                    topNvgBar.setRight("编辑");
                    rl_delete.setVisibility(View.GONE);
                    rl_delete.setAnimation(AnimatorUtils.moveToViewBottom());
                }
                for (int i = 0; i < collectList.size(); i++) {
                    collectList.get(i).setVisibility(!isEdit);
                }
                adapter.notifyDataSetChanged();
            }
        });
        // 侧滑
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(mContext);
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(255,91,41)));
                // set item width
                openItem.setWidth(DimenUtils.px2dip(mContext, 340));
                // set item title
                openItem.setTitle("删除");
                // // set item title fontsize
                openItem.setTitleSize(15);
                // // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // openItem.setIcon(R.drawable.collect);
                // add to menu
                menu.addMenuItem(openItem);


            }
        };
        lv_goods.setMenuCreator(creator);
        // 侧滑监听
        lv_goods.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position,
                                           SwipeMenu menu, int index) {
                FavoriteShop xyGoods = (FavoriteShop) adapter.getItem(position);
                String carId = xyGoods.getGoods_id();
                showDeleteDialog(carId);
                return false;
            }
        });
        lv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("productId", collectList.get(i).getGoods_id());
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
            }
        });
    }


    @Event({R.id.cb_all, R.id.tv_delete})
    private void click(View view) {

        switch (view.getId()) {
            case R.id.cb_all://
                checkList.clear();
                isSelectAll = !isSelectAll;
                if (isSelectAll) {//全选未选中，设置全选
                    for (FavoriteShop bean : collectList) {
                        bean.setChecked(true);
                        checkList.add(bean);
                    }
                } else {//全选已选中，取消全选
                    for (FavoriteShop bean : collectList) {
                        bean.setChecked(false);
                    }
                }
                cb_all.setChecked(isSelectAll);
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete://
                if (null == checkList || checkList.isEmpty()) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (FavoriteShop bean : checkList) {
                    sb.append(bean.getGoods_id()).append(",");
                }
                String result = sb.deleteCharAt(sb.length() - 1).toString();
                showDeleteDialog(result);
                break;
            default:
                break;
        }
    }

    /**
     * 删除提示
     *
     * @param goodsIds
     */
    private void showDeleteDialog(final String goodsIds) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {//
                    deleteCollect(goodsIds);
                }

            }
        });
        alert.showDialog(this, getResources().getString(R.string.tishi_78), "确定", "取消");
    }


    /**
     * 删除收藏数据
     */
    private void deleteCollect(String favoriteShopIds) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.deleteFavorite(token, favoriteShopIds);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    int status = baseResponse.getCode();
                    if (status == (MyConstant.SUCCESS)) {// 删除成功
                        checkList.clear();
                        getCollectList(2);// 刷新
                    } else {
                        String msg = baseResponse.getMsg();
                        ToastUtil.showToast(mContext, msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    private void getCollectList(final int state) {
        cb_all.setChecked(false);
        isSelectAll = false;
        checkList.clear();
        checkNumber = 0;
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<FavoriteShop>>> call = userBiz.getFavoriteList(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<FavoriteShop>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<FavoriteShop>>> arg0,
                                   Response<BaseResponse<List<FavoriteShop>>> response) {
                if (state == 2) {
                    if (collectList != null) {
                        collectList.clear();
                    }
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                BaseResponse<List<FavoriteShop>> baseResponse = response.body();
                if (null != baseResponse) {
                    int status = baseResponse.getCode();
                    if (status == (MyConstant.SUCCESS)) {
                        collectList = baseResponse.getData();
                        if (null == collectList || collectList.size() == 0) {// 无数据
                            stateLayout.showEmptyView("暂无数据");
                            topNvgBar.setRightClickable(false);
                            rl_delete.setVisibility(View.GONE);
                        } else {// 有数据
                            stateLayout.showContentView();
                            topNvgBar.setRightClickable(true);
                            adapter.updateListView(collectList);
                        }
                    }

                } else {
//                    ToastUtil.showToast(mContext, baseResponse.getMsg());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<FavoriteShop>>> arg0,
                    Throwable arg1) {
                dialog.dismiss();

                if (state == 1) {
                    dialog.dismiss();
                }
                if (state == 2) {
                    // 刷新完成调用
                    refresh_view.refreshFinish(PullToRefreshLayout.FAIL);
                }
                ToastUtil.showToast(mContext, "网络状态不佳,请检查您的网络设置");
            }
        });
    }


    @Override
    protected void initEvents() {

    }

    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        getCollectList(2);
    }

    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {


    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
        FavoriteShop  bean=collectList.get(position);
        boolean isChecked = bean.isChecked();
        bean.setChecked(!isChecked);
        adapter.notifyDataSetChanged();
        reFlashView();
    }

    public void reFlashView() {
        checkNumber = 0;
        checkList.clear();
        for (FavoriteShop bean : collectList) {
            boolean isChecked = bean.isChecked();
            if(isChecked){
                checkNumber++;
                checkList.add(bean);
            }
        }
        if(checkNumber==collectList.size()){
            isSelectAll = true;
        }else{
            isSelectAll = false;
        }
        cb_all.setChecked(isSelectAll);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
//        int tage = messageEvent.getTage();
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getCollectList(1);
        }else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
