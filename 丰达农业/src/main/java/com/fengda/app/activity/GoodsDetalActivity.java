package com.fengda.app.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.fengda.app.R;
import com.fengda.app.adapter.ProductImageListrAdapter;
import com.fengda.app.adapter.ScaleImgAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.OrderCartBase;
import com.fengda.app.bean.OrderGoods;
import com.fengda.app.bean.Specifications;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent4;
import com.fengda.app.eventbus.ToCarMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.popwindow.ActionItem;
import com.fengda.app.view.popwindow.BuyPopup;
import com.fengda.app.view.popwindow.TitlePopAdapter;
import com.fengda.app.view.popwindow.TitlePopWindow;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.MyScrollView;
import com.fengda.app.widget.viewpager.LoopViewPager;
import com.fengda.app.widget.viewpager.MaterialIndicator;

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
 * Created by 24448 on 2017/10/20.
 */

public class GoodsDetalActivity extends BaseActivity implements MyScrollView.OnScrollChangeListener {


    @ViewInject(R.id.tv_goods)
    private TextView tv_goods;

    @ViewInject(R.id.view_goods)
    private View view_goods;

    @ViewInject(R.id.tv_detals)
    private TextView tv_detals;

    @ViewInject(R.id.view_detals)
    private View view_detals;

    @ViewInject(R.id.img_pop)
    private ImageView img_pop;

    @ViewInject(R.id.tv_guige)
    private TextView tv_guige;

    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;


    @ViewInject(R.id.tv_price)
    private TextView tv_price;
    @ViewInject(R.id.tv_1)
    private TextView tv_1;
    @ViewInject(R.id.tv_2)
    private TextView tv_2;
    @ViewInject(R.id.tv_member_price)
    private   TextView tv_member_price;



    @ViewInject(R.id.lv_img)
    private ListView listImg;
    @ViewInject(R.id.pager)
    private LoopViewPager pager;
    @ViewInject(R.id.goodsIndicator)
    private MaterialIndicator goodsIndicator;


    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;
    @ViewInject(R.id.sv_goods)
    private MyScrollView sv_goods;

    @ViewInject(R.id.ll_detal)
    private LinearLayout ll_detal;
    @ViewInject(R.id.tv_add_car)
    private TextView tv_add_car;
    @ViewInject(R.id.tv_collection)
    private CheckBox tv_collection;

    private Context mContext;
    private TitlePopWindow window;
    private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();
    private ArrayList<String> imageUrlLists = new ArrayList<>();
    /**
     * 商品介绍图片集合
     **/
    private ArrayList<String> imageDetalUrls = new ArrayList<String>();
    private ScaleImgAdapter adapter;
    private Intent intent;
    private int top2;
    private String productId;
    private ProductImageListrAdapter productImageListrAdapter;
    private BuyPopup buyPopup;
    private String token;
    private LayoutInflater mLayoutInflater;
    private GoodsBean goodsBean;
    private String attr_id;
    private String num;
    private int goods_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detals);
        x.view().inject(this);
        EventBus.getDefault().register(this);
        mContext = this;
        productId = getIntent().getStringExtra("productId");
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        tv_collection.setChecked(false);
        initDialog();
        initData();
        initViews();
        initEvents();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 给标题栏弹窗添加子类
        mActionItems.add(new ActionItem(this, "消息", R.drawable.search_news));
        mActionItems.add(new ActionItem(this, "首页", R.drawable.search_home));
    }


    @Override
    protected void initViews() {
        ViewTreeObserver vto = tv_goods.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                //	mImgOverlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                int height = mImgOverlay.getMeasuredHeight();
//                int width = mImgOverlay.getMeasuredWidth();
                top2=ll_detal.getTop();
                return true;
            }
        });
        sv_goods.setOnScrollChangeListener(this);
        sv_goods.smoothScrollTo(0, 0);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGoodDetail();
            }
        });
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGoodDetail();
            }
        });
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onScrollChange(MyScrollView view, int x, int y, int oldx, int oldy) {


//        if (oldy < y && ((y - oldy) > 15)) {// 向上
//         //   Log.e("wangly", "距离："+(oldy < y) +"---"+(y - oldy));
//         //   Log.d("TAG","向上滑动")
//       //     ToastUtil.showToast(mContext,"向上滑动");
//
//            if (!checkIsVisible(this, tv_guige)) {
//                view_goods.setVisibility(View.GONE);
//                view_detals.setVisibility(View.VISIBLE);
//            }
//
//        }  else if (oldy > y && (oldy - y) > 15) {// 向下
//          //  Log.e("wangly", "距离："+(oldy > y) +"---"+(oldy - y));
//        //    Log.d("TAG"," 向下滑动")
//        //    ToastUtil.showToast(mContext,"向下滑动");
//
//            if (checkIsVisible(this, tv_price)) {
//                view_goods.setVisibility(View.VISIBLE);
//                view_detals.setVisibility(View.GONE);
//            }
//        }

        if (y >= ll_detal.getTop()) {
            view_goods.setVisibility(View.GONE);
            view_detals.setVisibility(View.VISIBLE);
        } else {
            view_goods.setVisibility(View.VISIBLE);
            view_detals.setVisibility(View.GONE);
        }
    }


    @Override
    public void onScrollBottomListener() {
        // Log.e("slantech","滑动到底部");
    }


    @Override
    public void onScrollTopListener() {
        //  Log.e("slantech","滑动到顶部");
    }

    public static Boolean checkIsVisible(Context context, View view) {
        // 如果已经加载了，判断广告view是否显示出来，然后曝光
        int screenWidth = getScreenMetrics(context).x;
        int screenHeight = getScreenMetrics(context).y;
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            //view已不在屏幕可见区域;
            return false;
        }
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);
    }

    @Override
    protected void initEvents() {
        adapter = new ScaleImgAdapter(imageDetalUrls, mContext);//初始化图文详情
        listImg.setAdapter(adapter);
        getGoodDetail();
    }

    int currentPosition = 0;

    @Event({R.id.rl_back, R.id.tv_contact, R.id.tv_buy, R.id.img_pop, R.id.tv_goods, R.id.tv_detals, R.id.tv_myaddress, R.id.tv_guige,R.id.tv_add_car,R.id.tv_collection})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.rl_back://
                finish();
                break;
            case R.id.tv_contact://联系客服
                showDialDialog();
                break;
            case R.id.tv_add_car://加入购物车
                if (MyConstant.HASLOGIN) {
                    if (null == goodsBean) {
                        return;
                    } else if (StringUtils.isBlank(attr_id)) {
                        buyPopup = new BuyPopup(mContext, mLayoutInflater.inflate(R.layout.view_pop, null), goodsBean, currentPosition, attr_id, 3);
                        buyPopup.setOnDismissListener(new OnPopupDismissListener());
                        buyPopup.showAtLocation(sv_goods, Gravity.CENTER, 0, 0);
                    } else {
                        if (!StringUtils.isBlank(goodsBean.getGoods_number()) && !StringUtils.isBlank(num)){
                            if (addcarTotalNum > Integer.valueOf(goodsBean.getGoods_number()) || (addcarTotalNum+Integer.valueOf(num))> Integer.valueOf(goodsBean.getGoods_number())) {
                                ToastUtil.showToast(mContext, "超过最大库存了");
                                return;
                            }
                        }
                        if (buyPopup != null && buyPopup.isShowing()) {
                            buyPopup.dismiss();
                        }
                        addShoppingCart("0");
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_buy://立即购买
                if (MyConstant.HASLOGIN) {
                    if (null == goodsBean) {
                        return;
                    } else if (StringUtils.isBlank(attr_id)) {
                        buyPopup = new BuyPopup(mContext, mLayoutInflater.inflate(R.layout.view_pop, null), goodsBean, currentPosition, attr_id, 2);
                        buyPopup.setOnDismissListener(new OnPopupDismissListener());
                        buyPopup.showAtLocation(sv_goods, Gravity.CENTER, 0, 0);
                    } else {
                        if (buyPopup != null && buyPopup.isShowing()) {
                            buyPopup.dismiss();
                        }
                        generateOrder();
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_guige://选择规格
                if (null == goodsBean) {
                    return;
                }
                buyPopup = new BuyPopup(mContext, mLayoutInflater.inflate(R.layout.view_pop, null), goodsBean, currentPosition, attr_id, 1);
                buyPopup.setOnDismissListener(new OnPopupDismissListener());
                buyPopup.showAtLocation(sv_goods, Gravity.CENTER, 0, 0);
                break;
            case R.id.img_pop://跳转购物车
                if (MyConstant.HASLOGIN) {
                     intent = new Intent(mContext,ShoppingCarActivity.class);
                }else{
                     intent = new Intent(mContext,LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_collection://加入收藏
                if (MyConstant.HASLOGIN) {
                    if (tv_collection.isChecked()){
                        addFavorite();
                    }else{
                        deleteFavorite();
                    }
                }else{
                    tv_collection.setChecked(false);
                    intent = new Intent(mContext,LoginActivity.class);
                    startActivity(intent);

                }
                break;
            case R.id.tv_goods://商品
                sv_goods.smoothScrollTo(0, 0);
                view_goods.setVisibility(View.VISIBLE);
                view_detals.setVisibility(View.GONE);
                break;
            case R.id.tv_detals://详情
                sv_goods.smoothScrollTo(0, top2);
                view_goods.setVisibility(View.GONE);
                view_detals.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    private void showMorePopWindow() {
        window = new TitlePopWindow(this, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, mActionItems);
        if (Build.VERSION.SDK_INT == 24) {
            // 适配 android 7.0
            int[] location = new int[2];
            img_pop.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            window.showAtLocation(img_pop, Gravity.NO_GRAVITY, -100, y + img_pop.getHeight());
        } else {
            window.showAsDropDown(img_pop, 30, 0);
        }
//            window.showAsDropDown(img_pop, 30, 0);
        // 实例化标题栏按钮并设置监听
        window.setOnItemClickListener(new TitlePopAdapter.onItemClickListener() {

            @Override
            public void click(int position, View view) {
                switch (position) {
                    case 0:// 消息
                        if (MyConstant.HASLOGIN) {
                            intent = new Intent(mContext, MyMessageActivity.class);
                        } else {
                            intent = new Intent(mContext, LoginActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case 1:// 首页
                        finish();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void showDialDialog() {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked)
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", MyConstant.COM_PHONE_NUM, null)));
            }
        });
        alert.showDialog(GoodsDetalActivity.this, getResources().getString(R.string.dialog_personal_phone), "呼叫", "取消");
    }


    private void getGoodDetail() {
        dialog.show();
        stateLayout.showProgressView();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<GoodsBean>>> call = userBiz.getGoodDetail(token,productId);
        call.enqueue(new HttpCallBack<BaseResponse<List<GoodsBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<GoodsBean>>> arg0,
                                   Response<BaseResponse<List<GoodsBean>>> response) {
                super.onResponse(arg0, response);
                dialog.dismiss();
                BaseResponse<List<GoodsBean>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        if (baseResponse.getData().isEmpty()) {
                            stateLayout.showEmptyView("暂无数据");
                            return;
                        }
                        stateLayout.showContentView();
                        goodsBean = baseResponse.getData().get(0);
                        if (goodsBean.getCollected()==1){
                            tv_collection.setChecked(true);
                        }
                        if (!StringUtils.isBlank(goodsBean.getPoint())&&Float.valueOf(goodsBean.getPoint())>0){
                            StringUtils.titleTipUtils(mContext,tv_goods_name,"消费果抵扣",goodsBean.getGname(),12,15,10,16);
                        }else if (goodsBean.getReturn_point()>0) {
                            StringUtils.titleTipUtils(mContext, tv_goods_name, "返分", goodsBean.getGname(), 12, 15, 4, 16);
                        }else {
                            tv_goods_name.setText(goodsBean.getGname());
                        }
                         goods_type=goodsBean.getGoods_type();
                        if(goods_type==1){
                            tv_price.setText(goodsBean.getPrice());
//                            tv_member_price.setText(goodsBean.getPrice());
                        }else {
//                            tv_add_car.setVisibility(View.GONE);
//                            tv_price.setText(goodsBean.getPrice());
                        }
                        String goods_logo = goodsBean.getGoods_logo();
                        String[] arr = goods_logo.split(",");
                        for (int i = 0; i < arr.length; i++) {
                            imageUrlLists.add(arr[i]);
                        }
                        productImageListrAdapter = new ProductImageListrAdapter(mContext, imageUrlLists);
                        pager.setAdapter(productImageListrAdapter);
                        if(imageUrlLists.size()>1){
                            pager.setLooperPic(true);
                            pager.addOnPageChangeListener(goodsIndicator);
                            goodsIndicator.setAdapter(pager.getAdapter());
                        }
                        updateProductImgUrl(goodsBean.getDetail());
                    } else {
                        stateLayout.showEmptyView("暂无数据");
                        ToastUtil.showToast(mContext, desc);
                    }
                }else{
//                    stateLayout.showContentView();
                    stateLayout.showEmptyView("网络连接错误");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<GoodsBean>>> arg0, Throwable arg1) {
                stateLayout.showEmptyView("网络连接错误");
                dialog.dismiss();
            }
        });
    }

    private void updateProductImgUrl(String detail) {
        imageDetalUrls.clear();
        String[] arr = detail.split(",");
        for (int i = 0; i < arr.length; i++) {
//            if(i>16){
//                break;
//            }
            imageDetalUrls.add(arr[i]);
        }
        adapter.notifyDataSetChanged();
  //      adapter.updataListView(imageDetalUrls);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent4 messageEvent) {
        currentPosition = messageEvent.getPosition();
        num = messageEvent.getNumber();
        if(StringUtils.isBlank(num)){
            num="1";
        }
        attr_id = messageEvent.getId();
        int type = messageEvent.getType();
        Specifications specifications = messageEvent.getSpecifications();
        if (type == 1) {//关闭规格弹窗回调
            buyPopup.dismiss();
            if (StringUtils.isBlank(attr_id)) {
                tv_guige.setText("请选择规格");
                tv_goods_name.setText(goodsBean.getGname());
//                if(goods_type==1){
//                    tv_price.setText(goodsBean.getPrice_cost());
//                }else {
                    tv_price.setText(goodsBean.getPrice());
//                }
//                String point = goodsBean.getPoint();//不显示积分
//                if (!StringUtils.isBlank(point) && !point.equals("0")) {
//                    tv_point.setText("+" + point + "积分");
//                    tv_point.setVisibility(View.VISIBLE);
//                } else {
//                    tv_point.setVisibility(View.GONE);
//                }
            } else {//选了规格，点击确定
                tv_guige.setText("已选:" + specifications.getAttr_value());
                tv_price.setText(specifications.getAttr_price());

//                String point = specifications.getAttr_point();//不显示积分
//                if (!StringUtils.isBlank(point) && !point.equals("0")) {
//                    tv_point.setText(point);
//                    tv_point.setVisibility(View.VISIBLE);
//                    tv_1.setVisibility(View.VISIBLE);
//                    tv_2.setVisibility(View.VISIBLE);
//                } else {
//                    tv_point.setVisibility(View.GONE);
//                    tv_1.setVisibility(View.GONE);
//                    tv_2.setVisibility(View.GONE);
//                }
            }
        }else{
            if (StringUtils.isBlank(attr_id)) {
                ToastUtil.showToast(mContext, "请选择商品规格");
                return;
            }
            tv_guige.setText("已选:" + specifications.getAttr_value());
            if (buyPopup != null && buyPopup.isShowing()) {
                buyPopup.dismiss();
            }
             if(type==2){//立即购买
                generateOrder();
            }else if(type==3){//加入购物车
                 addShoppingCart("0");
            }
        }
    }

    /**
     * popupwindow消失的回调
     */
    private class OnPopupDismissListener implements
            android.widget.PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // 标题和主页开始播放动画
            // mLlytTitle.startAnimation(mTranInAnimation);
            // mLlytTitle.setBackgroundResource(R.color.blueshit)
            //   currentPosition= messageEvent.getPosition();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消事件注册
        EventBus.getDefault().unregister(this);
        if (Util.isOnMainThread()) {
            Glide.with(getApplicationContext()).pauseRequests();
        }
    }


    private void generateOrder() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<OrderCartBase>>> call=null;
        int type=goodsBean.getGoods_type();
        OrderGoods orderGoods=new OrderGoods( attr_id, num);
        ArrayList<OrderGoods> orderList = new ArrayList<OrderGoods>();
        orderList.add(orderGoods);
        final String queryId=GsonUtil.toJsonString(orderList);
//        if(type==1){
            call = userBiz.generateOrder(token, attr_id, queryId, "");
//        }else if(type==2){
//            call = userBiz.xhGenerateOrder(token, goodsBean.getGoods_type(), attr_id, num, "");
//        }
        call.enqueue(new HttpCallBack<BaseResponse<List<OrderCartBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<OrderCartBase>>> arg0,
                                   Response<BaseResponse<List<OrderCartBase>>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<List<OrderCartBase>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        if (baseResponse.getData().size() == 0)
                            return;
                        OrderCartBase orderCartBase = baseResponse.getData().get(0);
                        String data = GsonUtil.toJsonString(orderCartBase);
                        intent = new Intent(mContext, SubmitCarOrderActivity.class);
                        intent.putExtra("data", data);
                        intent.putExtra("goods_attr_id", attr_id);
                        intent.putExtra("queryId", queryId);
                        intent.putExtra("from_type", "1");
                        intent.putExtra("goods_type", goods_type);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<OrderCartBase>>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "连接服务器失败,请检查您的网络状态");
            }
        });
    }

    private int addcarTotalNum = 0;
    /**
     * 加入购物车
     * type: 0 加入购物车 1加减
     */
    private void addShoppingCart(String type) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.addShoppingCart(token, attr_id, num,type);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        ToastUtil.showToast(mContext, "加入购物车成功");
                        addcarTotalNum += Integer.valueOf(num);
                        EventBus.getDefault().post(new ToCarMsgEvent());
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "连接服务器失败,请检查您的网络状态");
            }
        });
    }
    /**
     * 加入收藏
     */
    private void addFavorite() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.addFavorite(token, productId);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        ToastUtil.showToast(mContext, "加入收藏成功");
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "连接服务器失败,请检查您的网络状态");
            }
        });
    }
    /**
     * 移除收藏
     */
    private void deleteFavorite() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.deleteFavorite(token, productId);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        ToastUtil.showToast(mContext, "移除收藏成功");
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "连接服务器失败,请检查您的网络状态");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
        } else {// 未登录
            MyConstant.HASLOGIN = false;
        }

    }
}
