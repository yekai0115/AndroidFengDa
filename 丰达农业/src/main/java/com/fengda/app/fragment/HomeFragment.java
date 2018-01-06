package com.fengda.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fengda.app.R;
import com.fengda.app.activity.CategoryGoodsListActivity;
import com.fengda.app.activity.GoodsDetalActivity;
import com.fengda.app.activity.MyRecommendActivity;
import com.fengda.app.activity.MyWebViewActivity;
import com.fengda.app.activity.SearchActivity;
import com.fengda.app.adapter.BannerAdapter;
import com.fengda.app.adapter.GridViewAdapter;
import com.fengda.app.adapter.ProductsListAdapter;
import com.fengda.app.adapter.ViewPagerAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BannerBase;
import com.fengda.app.bean.BannerBean;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent17;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableRefreshScrollView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.widget.MyListView;
import com.fengda.app.widget.viewpager.LoopViewPager;
import com.fengda.app.widget.viewpager.MaterialIndicator;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@ContentView(R.layout.main_fragment_home)
public class HomeFragment extends Fragment implements PullLayout.OnRefreshListener, PullableRefreshScrollView.OnScrollChangeListener {


    /**
     * 轮播
     */
    @ViewInject(R.id.ve_pager)
    private LoopViewPager pager;

    @ViewInject(R.id.bannerIndicator)
    private MaterialIndicator bannerIndicator;

    /**
     *
     */
    @ViewInject(R.id.lv_recommend)
    private MyListView lv_recommend;


    @ViewInject(R.id.refresh_view)
    private PullLayout refresh_view;


    @ViewInject(R.id.searcher_linear)
    private LinearLayout searcher_linear;

    @ViewInject(R.id.searcher_title_linear)
    private LinearLayout searcher_title_linear;

    @ViewInject(R.id.mScrollView)
    private PullableRefreshScrollView mScrollView;

    @ViewInject(R.id.introduce_banner1)
    private ImageView introduce_banner1;

    @ViewInject(R.id.introduce_banner2)
    private ImageView introduce_banner2;

    @ViewInject(R.id.introduce_banner3)
    private ImageView introduce_banner3;


    @ViewInject(R.id.viewpager)
    private ViewPager mPager;

    @ViewInject(R.id.ll_dot)
    private LinearLayout mLlDot;

    @ViewInject(R.id.iv_invite_friends_banner)
    private ImageView iv_invite_friends_banner;

    @ViewInject(R.id.iv_jump)
    private ImageView iv_jump;

    private View mRootView;


    private String token;


    /**
     * 上下文
     **/
    private Context mContext;
    private BannerAdapter bannerAdapter;
    private ProductsListAdapter adapter;
    private int fadingHeight = 500; // 当ScrollView滑动到什么位置时渐变消失（根据需要进行调整）
    private Drawable drawable; // 顶部渐变布局需设置的Drawable
    private static final int START_ALPHA = 0;//scrollview滑动开始位置
    private static final int END_ALPHA = 255;//scrollview滑动结束位置
    private List<GoodsBean> recommendBeanList = new ArrayList<>();


    private List<View> mPagerList;
    private List<BannerBean> mDatas;
    private LayoutInflater inflater;
    /**
     * 总的页数
     */
    private int pageCount;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 4;
    /**
     * 当前显示的是第几页
     */
    private int curIndex = 0;

    public HomeFragment() {
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
        // 注册事件
              EventBus.getDefault().register(this);
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
        refresh_view.setOnRefreshListener(this);
        setWidget();
//        searcher_linear.getBackground().setAlpha(150);
        return mRootView;
    }

    private void setWidget() {
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        getBanner(1);
        adapter = new ProductsListAdapter(mContext, recommendBeanList);
        lv_recommend.setAdapter(adapter);
        lv_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GoodsBean goodsBean = recommendBeanList.get(i);
                String id = goodsBean.getGoods_id();
                Intent intent = new Intent();
                intent.putExtra("productId", id);
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
            }
        });
//        mScrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        // 下拉刷新操作
        getBanner(2);
    }

    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {
        // 加载操作
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消事件注册
            EventBus.getDefault().unregister(this);
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
     * 查询
     */
    private void getBanner(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<BannerBase>>> call = userBiz.getBanner();
        call.enqueue(new Callback<BaseResponse<List<BannerBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<BannerBase>>> arg0,
                                   Response<BaseResponse<List<BannerBase>>> response) {
                if (state == 2) {
                    refresh_view.refreshFinish(PullLayout.SUCCEED);
                }
                BaseResponse<List<BannerBase>> baseResponse = response.body();
                if (baseResponse != null) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                            List<BannerBase> data = baseResponse.getData();
                            if (data == null||data.isEmpty()) return;
                            BannerBase bannerBase=data.get(0);
                            List<BannerBean> banner = bannerBase.getBanner();
                            if (null == banner || banner.size() == 0) return;
                            //头部banner
                            bannerAdapter = new BannerAdapter(mContext, banner,bannerBase.getFronturl());
                            pager.setAdapter(bannerAdapter);
                            if (banner.size() > 1) {
                                pager.setLooperPic(true);
                                pager.addOnPageChangeListener(bannerIndicator);
                                bannerIndicator.setAdapter(pager.getAdapter());
                            } else {
                                pager.setLooperPic(false);
                            }
                            //积分推荐
                            List<BannerBean> activities = bannerBase.getActivities();
                            if (null == activities || activities.isEmpty()) return;
                            showIntroduce(activities,bannerBase.getFronturl());
                            //分类菜单
                            showclassification(bannerBase);
                            //优选商品
                            recommendBeanList = bannerBase.getGoods();
                            if (recommendBeanList == null) return;
                            adapter.updateListview(recommendBeanList);
                            setListviewHeight(lv_recommend);
                            //广告1
                            Picasso.with(mContext).load(bannerBase.getFronturl()+bannerBase.getBanner2().get(0).getLogo()+ MyConstant.PIC_DPI2)
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.drawable.pic_nomal_loading_style)
                                    .error(R.drawable.pic_nomal_loading_style)
                                    .into(iv_invite_friends_banner);
                            //广告2
                            Picasso.with(mContext).load(bannerBase.getFronturl()+bannerBase.getBanner2().get(1).getLogo()+ MyConstant.PIC_DPI2)
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.drawable.pic_nomal_loading_style)
                                    .error(R.drawable.pic_nomal_loading_style)
                                    .into(iv_jump);


                    }
                } else {//返回为空

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<BannerBase>>> arg0, Throwable arg1) {
                ToastUtil.showToast(mContext, "服务器连接失败,请检查您的网络设置");
                if (state == 2) {
                    refresh_view.refreshFinish(PullLayout.FAIL);
                }
            }
        });
    }
    //分类菜单
    private void showclassification(BannerBase data) {
        mDatas = data.getCategory();
        if (null == mDatas || mDatas.isEmpty()) return;
        inflater = LayoutInflater.from(mContext);
        //总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
        mPagerList = new ArrayList<View>();
        for (int i = 0; i < pageCount; i++) {
            //每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.gridview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(mContext, mDatas, i, pageSize,data.getFronturl()));
            mPagerList.add(gridView);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position + curIndex * pageSize;
                    BannerBean bannerBean = mDatas.get(position);
                    view.setTag(bannerBean);
                    imgClickAction(view);
                }
            });
        }
        //设置适配器
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        //设置圆点
        if (pageCount > 1) {
            setOvalLayout();
        }
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollTo(0, 0);
            }
        });

    }
    //积分推荐
    private void showIntroduce(List<BannerBean> activities, String frontUrl) {
        for (int i = 0; i < activities.size(); i++) {
            BannerBean bannerBean = activities.get(i);
            String bannerImg = (frontUrl + bannerBean.getLogo() + MyConstant.PIC_DPI2).trim();
            switch (i) {
                case 0:
                    Picasso.with(mContext).load(bannerImg)
                            //    .centerInside()
                            .config(Bitmap.Config.RGB_565)
                            .placeholder(R.drawable.pic_nomal_loading_style)
                            .error(R.drawable.pic_nomal_loading_style)
                            .into(introduce_banner1);

                    introduce_banner1.setTag(bannerBean);
                    break;
                case 1:

                    Picasso.with(mContext).load(bannerImg)
                            //    .centerInside()
                            .config(Bitmap.Config.RGB_565)
                            .placeholder(R.drawable.pic_nomal_loading_style)
                            .error(R.drawable.pic_nomal_loading_style)
                            .into(introduce_banner2);


                    introduce_banner2.setTag(bannerBean);
                    break;
                case 2:
                    Picasso.with(mContext).load(bannerImg)
                            //    .centerInside()
                            .config(Bitmap.Config.RGB_565)
                            .placeholder(R.drawable.pic_nomal_loading_style)
                            .error(R.drawable.pic_nomal_loading_style)
                            .into(introduce_banner3);

                    introduce_banner3.setTag(bannerBean);
                    break;
            }

        }
    }

    @Override
    public void onScrollChange(PullableRefreshScrollView view, int x, int y, int oldx, int oldy) {
        if (y > fadingHeight) {
            y = fadingHeight; // 当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可
            drawable = getResources().getDrawable(R.color.white);
            drawable.setAlpha(END_ALPHA);
            searcher_title_linear.setBackgroundDrawable(drawable);
//                my_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_news1));
        } else if (y < 0) {
            y = 0;
        } else {
            drawable = getResources().getDrawable(R.color.toming);
            drawable.setAlpha(y * (END_ALPHA - START_ALPHA) / fadingHeight
                    + START_ALPHA);
//                drawable.setAlpha(START_ALPHA);

            searcher_title_linear.setBackgroundDrawable(drawable);
//                my_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_news));

        }


    }

    @Event({R.id.searcher_linear, R.id.introduce_banner1, R.id.introduce_banner2, R.id.introduce_banner3,
            R.id.iv_invite_friends_banner,R.id.iv_jump})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.searcher_linear://搜索
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.introduce_banner1://
                imgClickAction(introduce_banner1);
                break;
            case R.id.introduce_banner2://
                imgClickAction(introduce_banner2);
                break;
            case R.id.introduce_banner3://
                imgClickAction(introduce_banner3);
                break;
            case R.id.iv_invite_friends_banner://
                intent = new Intent(getActivity(), MyRecommendActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_jump://
                EventBus.getDefault().post(new MsgEvent17());//跳转个人中心
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent msgEvent5) {

    }
    private void imgClickAction(View view) {
        BannerBean bannerBean = (BannerBean) view.getTag();
        int fast_way = bannerBean.getFast_way();
        String site_url = bannerBean.getSite_url();
        String jump_id = bannerBean.getJump_id();
        String name = bannerBean.getName();
        if (fast_way == 1) {
            return;
        } else if (fast_way == 2) {//商品详情
            if (StringUtils.isBlank(jump_id)) {
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("productId", jump_id);
            intent.setClass(mContext, GoodsDetalActivity.class);
            startActivity(intent);
        } else if (fast_way == 3) {//跳转html
            Intent intent = new Intent();
            intent.putExtra("site_url", site_url);
            intent.putExtra("name", name);
            intent.setClass(mContext, MyWebViewActivity.class);
            startActivity(intent);
        } else if (fast_way == 4) {//商品列表
            Intent intent = new Intent();
            intent.putExtra("jump_id", jump_id);
            intent.putExtra("name", name);
            intent.putExtra("type", 1);
            intent.setClass(mContext, CategoryGoodsListActivity.class);
            startActivity(intent);
        } else if (fast_way == 5) {

        }


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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
    }

    /**
     * 设置圆点
     */
    public void setOvalLayout() {
        if (mLlDot.getChildCount() == pageCount) {
            mLlDot.removeAllViews();
        }
        for (int i = 0; i < pageCount; i++) {
            mLlDot.addView(inflater.inflate(R.layout.dot, null));
        }
        // 默认显示第一页
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.dot_selected);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                // 取消圆点选中
                mLlDot.getChildAt(curIndex)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal);
                // 圆点选中
                mLlDot.getChildAt(position)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected);
                curIndex = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
}
