package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.fengda.app.R;
import com.fengda.app.adapter.CatergoryGoodslistAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar5;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 描述 ：商品分类商品列表
 */
public class CategoryGoodsListActivity extends BaseActivity implements
        PullToRefreshLayout.OnRefreshListener {


    @ViewInject(R.id.top_nvg_bar)
    private TopNvgBar5 topNvgBar;


    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;

    /**
     * 商品列表
     */
    @ViewInject(R.id.lv_goods)
    private PullableListView lv_goods;

    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;



    /**
     * 上下文
     **/
    private Context mContext;
    /**
     * 商品数据
     **/
    private List<GoodsBean> catergoryGoodsList = new ArrayList<GoodsBean>();

    /*商品列表适配器*/
    private CatergoryGoodslistAdapter adapter;
    /**
     * 小分类id
     */
    private String jump_id;
    /**
     * 大分类id
     */
    private String parent_id;
    private String name;
    private int type;
    private String jump_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_goods_list);
        mContext = this;
        x.view().inject(this);
        setWidget();
        initDialog();
        initViews();
        adapter = new CatergoryGoodslistAdapter(mContext, catergoryGoodsList);
        lv_goods.setAdapter(adapter);
        lv_goods.canPullUp = false;
        refresh_view.setOnRefreshListener(this);

    }


    private void setWidget() {
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
                    goodCategaryGoodsList(1, jump_id);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    goodCategaryGoodsList(1, jump_id);
            }
        });

        lv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GoodsBean goodsBean=(GoodsBean)adapter.getItem(i);
                String gid=goodsBean.getGoods_id();
                Intent intent = new Intent();
                intent.putExtra("productId", gid);
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
            }
        });


    }

    private ArrayList<String> scrolListName;

    @Override
    protected void initViews() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 1);
        name = intent.getStringExtra("name");
        jump_id  = intent.getStringExtra("jump_id");
        topNvgBar.setTitle(name);
        goodCategaryGoodsList(1, jump_id);
    }

    @Override
    protected void initEvents() {

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


    }


    /**
     * 获取商品列表
     *
     * @param state
     */
    private void goodCategaryGoodsList(final int state, String category_id) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<GoodsBean>>> call = userBiz.goodCategaryList(category_id);
        call.enqueue(new HttpCallBack<BaseResponse<List<GoodsBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<GoodsBean>>> arg0,
                                   Response<BaseResponse<List<GoodsBean>>> response) {
                dialog.dismiss();
                if (state == 2) {
                    if (catergoryGoodsList != null) {
                        catergoryGoodsList.clear();
                    }
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                if (state == 3) {
                    if (catergoryGoodsList != null) {
                        catergoryGoodsList.clear();
                    }
                }
                super.onResponse(arg0, response);
                BaseResponse<List<GoodsBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        catergoryGoodsList = baseResponse.getData();
                        if (null == catergoryGoodsList || catergoryGoodsList.isEmpty()) {
                            stateLayout.showEmptyView();
                            stateLayout.showEmptyView("暂无数据");
                        } else {
                            stateLayout.showContentView();
                        }
                        adapter.updateListView(catergoryGoodsList);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<GoodsBean>>> arg0, Throwable arg1) {
                dialog.dismiss();
                stateLayout.showErrorView();
                stateLayout.showErrorView("网络错误");
                if (state == 2) {
                    // 刷新完成调用
                    refresh_view.refreshFinish(PullToRefreshLayout.FAIL);
                }

                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
            goodCategaryGoodsList(2, jump_id);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
