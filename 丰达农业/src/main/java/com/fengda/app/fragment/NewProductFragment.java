package com.fengda.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fengda.app.R;
import com.fengda.app.activity.CategoryGoodsListActivity;
import com.fengda.app.activity.GoodsDetalActivity;
import com.fengda.app.activity.SearchActivity;
import com.fengda.app.adapter.ProductsListAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.ParentCatergory;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.utils.ACache;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar6;

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
 * 新品列表
 */

@ContentView(R.layout.fragment_new_product)
public class NewProductFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.top_nvg_bar)
    private TopNvgBar6 topNvgBar;
    @ViewInject(R.id.category_refresh_view)
    private PullToRefreshLayout ptrl;
    @ViewInject(R.id.lv_goods)
    private PullableListView lv_goods;
    private Context mContext;
    private View mRootView;
    private ProductsListAdapter adapter;

    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;
    /**
     * 分类数据第一级
     */
    private List<GoodsBean> goodsBeanList = new ArrayList<GoodsBean>();



    private ACache mCache;


    public NewProductFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);

    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = x.view().inject(this, inflater, container);

        }
        ViewGroup mViewGroup = (ViewGroup) mRootView.getParent();
        if (mViewGroup != null) {
            mViewGroup.removeView(mRootView);
        }
        setWidget();
        return mRootView;
    }

    private void setWidget() {
        mCache = ACache.get(mContext);

        ptrl.setOnRefreshListener(this);
        adapter = new ProductsListAdapter(mContext, goodsBeanList);
        lv_goods.setAdapter(adapter);
        lv_goods.canPullUp = false;
//        lv_goods.setFocusable(false);
        // ptrl.autoRefresh();//自动刷新
        getNewProductlist(1);
        topNvgBar.setMyOnClickListener(new TopNvgBar6.MyOnClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(mContext,SearchActivity.class);
                mContext.startActivity(intent);
            }
        });
        lv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GoodsBean goodsBean = goodsBeanList.get(i);
                String id = goodsBean.getGoods_id();
                Intent intent = new Intent();
                intent.putExtra("productId", id);
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
            }
        });

        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewProductlist(1);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }





    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }


    /**
     * 描述 ： 取得商品
     *
     * @param
     * @param
     */
    public void getNewProductlist(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<GoodsBean>>> call = userBiz.getRecommend();
        call.enqueue(new Callback<BaseResponse<List<GoodsBean>>>() {

            @Override
            public void onResponse(
                    Call<BaseResponse<List<GoodsBean>>> arg0,
                    Response<BaseResponse<List<GoodsBean>>> response) {
                if (state == 2) {
                    if (goodsBeanList != null) {
                        goodsBeanList.clear();
                    }
                    ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                BaseResponse<List<GoodsBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    int status = baseResponse.getCode();
                    if (status==(MyConstant.SUCCESS)) {
                        goodsBeanList = baseResponse.getData();
                        if (null == goodsBeanList || goodsBeanList.isEmpty()) {
                            stateLayout.showEmptyView();
                            stateLayout.showEmptyView("暂无数据");
                        } else {
                            stateLayout.showContentView();
                        }
                        adapter.updateListview(goodsBeanList);
                    } else {
                        String msg=baseResponse.getMsg();
                        ToastUtil.showToast(mContext, msg);
                        adapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<GoodsBean>>> arg0, Throwable arg1) {
                stateLayout.showErrorView();
                stateLayout.showErrorView("网络错误");
                if (state == 2) {
                    // 刷新完成调用
                    ptrl.refreshFinish(PullToRefreshLayout.FAIL);
                }

                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getNewProductlist(2);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

}
