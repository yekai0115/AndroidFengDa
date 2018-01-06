package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.adapter.ShoppingCarAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.CartGoodsBase;
import com.fengda.app.bean.OrderCartBase;
import com.fengda.app.bean.OrderGoods;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.ToCarMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.swipe.SwipeMenu;
import com.fengda.app.view.pullableview.swipe.SwipeMenuCreator;
import com.fengda.app.view.pullableview.swipe.SwipeMenuItem;
import com.fengda.app.view.pullableview.swipe.SwipeMenuListView;
import com.fengda.app.view.pullableview.swipe.SwipeMenuView;
import com.fengda.app.view.pulltorefresh.PullLayout;
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
import retrofit2.Callback;
import retrofit2.Response;




public class ShoppingCarActivity extends BaseActivity implements PullLayout.OnRefreshListener, ListItemClickHelp {
    @ViewInject(R.id.ll_top)
    private TopNvgBar5 topNvgBar;
    @ViewInject(R.id.cb_car_all)
    private CheckBox cb_car_all;
    /**
     * 小计
     */
    @ViewInject(R.id.tv_car_Allprice)
    private TextView tv_car_Allprice;
    /**
     * 结算、删除
     */
    @ViewInject(R.id.tv_cart_buy_or_del)
    private TextView tv_cart_buy_or_del;
    /**
     * 小计数量
     */
    @ViewInject(R.id.tv_num)
    private TextView tv_num;
    @ViewInject(R.id.ll_money)
    private LinearLayout ll_money;

    @ViewInject(R.id.stateLayout)
    private StateLayout stateLayout;

    @ViewInject(R.id.car_refresh_view)
    private PullLayout ptrl;

    @ViewInject(R.id.listView_shopping)
    private SwipeMenuListView listView_shopping;

    private String token;

    private boolean isEdit = true;
    /**
     * 商品被选中的总数
     */
    private int itemCheckNumber = 0;

    /**
     * 上下文
     **/
    private Context mContext;
    private ShoppingCarAdapter adapter;

    /**
     * 判断是否全选
     */
    private boolean isSelectAll;


    /**
     * 购物车数据
     */
    private List<CartGoodsBase> groupList = new ArrayList<CartGoodsBase>();
    /**
     * 选中的购物车数据:带分类头部
     */
    private ArrayList<CartGoodsBase> checkList = new ArrayList<CartGoodsBase>();

    /**
     * 购物车选中商品总价
     */
    private String totalMoney = "0.00";

    public ShoppingCarActivity() {
        super();
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.main_fragment_car);
        mContext = this;
        x.view().inject(this);
        super.onCreate(savedInstanceState);
        // 注册事件
        EventBus.getDefault().register(this);
        initDialog();
        initViews();
    }

    @Override
    protected void initViews() {
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        ptrl.setOnRefreshListener(this);
        listView_shopping.canLoad = false;// 设置不能上拉
        adapter = new ShoppingCarAdapter(mContext, groupList, this);
        listView_shopping.setAdapter(adapter);
        getShoppingCart(1);
        topNvgBar.setLeftVisibility(View.VISIBLE);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                if (isEdit) {
                    topNvgBar.setRight("完成");
                    tv_cart_buy_or_del.setText("删除");
                    tv_num.setVisibility(View.GONE);
                    isEdit = false;
                    ll_money.setVisibility(View.INVISIBLE);
                    //     setEditStatus(1);
                } else if (!isEdit) {
                    isEdit = true;
                    topNvgBar.setRight("编辑");
                    tv_cart_buy_or_del.setText("结算");
                    ll_money.setVisibility(View.VISIBLE);
                    if (itemCheckNumber != 0) {
                        tv_num.setText("(" + itemCheckNumber + ")");
                        tv_num.setVisibility(View.VISIBLE);
                    } else {
                        tv_num.setVisibility(View.GONE);
                    }
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
                openItem.setWidth(DimenUtils.px2dip(mContext, 720));
                // set item title
                openItem.setTitle("删除");
                // // set item title fontsize
                openItem.setTitleSize(16);
                // // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // openItem.setIcon(R.drawable.collect);
                // add to menu
                menu.addMenuItem(openItem);


            }
        };
        listView_shopping.setMenuCreator(creator);
        // 点击item
        listView_shopping.setOnSwipeItemClickListener(new SwipeMenuListView.OnSwipeItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CartGoodsBase xyGoods = (CartGoodsBase) adapter.getItem(position);
                String id = xyGoods.getGoods_id();
                Intent intent = new Intent();
                intent.putExtra("productId", id);
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
            }
        });
        // 侧滑监听
        listView_shopping.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position,
                                           SwipeMenu menu, int index) {
                CartGoodsBase xyGoods = (CartGoodsBase) adapter.getItem(position);
                String carId = xyGoods.getId();
                showDeleteCartDialog(carId);
                return false;
            }
        });
        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShoppingCart(1);
            }
        });
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShoppingCart(1);
            }
        });
    }


    @Override
    protected void initEvents() {

    }

    @Event({R.id.cb_car_all, R.id.ll_buy_or_del})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ll_buy_or_del://结算、删除
                if (null == groupList || groupList.isEmpty()) {// 如果购物车为空的
                    ToastUtil.showToast(mContext, "购物车空空如也");
                } else {// 购物车不为空的
                    if (checkList.size() == 0) {// 未选择商品
                        ToastUtil.showToast(mContext, "请先选择商品");
                    } else {// 选择了商品
                        if (isEdit) {//结算
                            generateOrder();
                        } else {// 删除
                            String goodsIds = getGoodsIds();
                            showDeleteCartDialog(goodsIds);
                        }
                    }
                }
                break;
            case R.id.cb_car_all://全选
                checkAll();
                break;
        }
    }


    /**
     * 全选
     */
    private void checkAll() {
        try {
            checkList.clear();
            totalMoney = "0.00";
            itemCheckNumber = 0;
            // 全选未选中：isSelectAll=false→→→→!isSelectAll=true→→→→isSelectAll=true
            isSelectAll = !isSelectAll;
            // 全选未选中，遍历列表将全选和单选框都选中
            if (isSelectAll) {
                for (int i = 0; i < groupList.size(); i++) {
                    CartGoodsBase bean = groupList.get(i);
                    bean.setChecked(true);
                    checkList.add(bean);
                    String singleTotalMoney = CompuUtils.multiply(bean.getPrice(),bean.getNumber()).toString();
                    totalMoney = CompuUtils.add(totalMoney, singleTotalMoney).toString();
                    totalMoney = CompuUtils.add(totalMoney, bean.getPrice()).toString();
                }
                // 返回的是字符串
                tv_car_Allprice.setText(totalMoney);
                cb_car_all.setChecked(true);
            } else {// 全选已选中，遍历列表将全选和单选框改为未选中
                for (int i = 0; i < groupList.size(); i++) {
                    CartGoodsBase bean = groupList.get(i);
                    bean.setChecked(false);
                }
                cb_car_all.setChecked(false);
            }
            // 通知viewList刷新(执行下面所有item被全选)
            adapter.notifyDataSetChanged();
            if (!MyConstant.HASLOGIN) {
                totalMoney = "0.00";
                tv_car_Allprice.setText(totalMoney);
                cb_car_all.setChecked(false);
            } else {
                tv_car_Allprice.setText(totalMoney);
            }
            itemCheckNumber = checkList.size();
            if (itemCheckNumber != 0) {
                tv_num.setText("(" + itemCheckNumber + ")");
                tv_num.setVisibility(View.VISIBLE);
            } else {
                tv_num.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getShoppingCart(final int state) {
        cb_car_all.setChecked(false);
        isSelectAll = false;
        checkList.clear();
        totalMoney = "0.00";
        itemCheckNumber = 0;
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<CartGoodsBase>>> call = userBiz.queryUserCart(token);
        call.enqueue(new Callback<BaseResponse<List<CartGoodsBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<CartGoodsBase>>> arg0,
                                   Response<BaseResponse<List<CartGoodsBase>>> response) {
                if (state == 1 || state == 3) {
                    if (groupList != null) {
                        groupList.clear();
                    }
                    ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                if (state == 2) {
                    if (groupList != null) {
                        groupList.clear();
                    }
                    ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                BaseResponse<List<CartGoodsBase>> baseResponse = response.body();
                if (null != baseResponse) {
                    int status = baseResponse.getCode();
                    if (status == (MyConstant.SUCCESS)) {
                        List<CartGoodsBase> cartGoodsBaseList = baseResponse.getData();
                        if (null == cartGoodsBaseList || cartGoodsBaseList.isEmpty()) {// 无数据
                            stateLayout.showEmptyView("暂无数据");
                        } else {// 有数据
                            stateLayout.showContentView();
                            for (int i = 0; i < cartGoodsBaseList.size(); i++) {
                                CartGoodsBase cartGoods = cartGoodsBaseList.get(i);
                                if (state == 3) {
                                    cartGoods.setChecked(true);
                                } else {
                                    cartGoods.setChecked(false);
                                }
                                cartGoods.setNumber(cartGoods.getNumber());
                                groupList.add(cartGoods);

                            }
                        }
                        totalMoney = "0.00";
                        tv_car_Allprice.setText(totalMoney);
                        tv_num.setVisibility(View.GONE);
                    } else {
                        ToastUtil.showToast(mContext, baseResponse.getMsg());
                    }
                    adapter.notifyDataSetChanged();
                } else {//
                    adapter.notifyDataSetChanged();
                    try {
                        String error = response.errorBody().string();
                        BaseResponse errorResponse = GsonUtil.GsonToBean(error, BaseResponse.class);
                        startLogin(errorResponse);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<CartGoodsBase>>> arg0,
                                  Throwable arg1) {
                dialog.dismiss();

                if (state == 1) {
                    dialog.dismiss();
                }
                if (state == 2) {
                    // 刷新完成调用
                    ptrl.refreshFinish(PullToRefreshLayout.FAIL);
                }
                ToastUtil.showToast(mContext, "网络状态不佳,请检查您的网络设置");
            }
        });
    }


    private String getGoodsIds() {
        StringBuilder sb = new StringBuilder();
        for (CartGoodsBase goods : checkList) {
            sb.append(goods.getId()).append(",");
        }
        String result = sb.deleteCharAt(sb.length() - 1).toString();
        return result;
    }

    /**
     * 删除提示
     *
     * @param goodsIds
     */
    private void showDeleteCartDialog(final String goodsIds) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {//
                    deleteCart(goodsIds);
                }

            }
        });
        alert.showDialog(this, getResources().getString(R.string.tishi_78), "确定", "取消");
    }


    /**
     * 删除购物车数据
     */
    private void deleteCart(String fgoodsShopcarIds) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.delCartGoodsBat(token, fgoodsShopcarIds);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    int status = baseResponse.getCode();
                    if (status == (MyConstant.SUCCESS)) {// 删除成功
                        totalMoney = "0.00";
                        tv_car_Allprice.setText(totalMoney);
                        checkList.clear();
                        EventBus.getDefault().post(new ToCarMsgEvent());
                        getShoppingCart(2);// 刷新
                    } else {
                        String msg = baseResponse.getMsg();
                        ToastUtil.showToast(mContext, msg);
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        BaseResponse errorResponse = GsonUtil.GsonToBean(error, BaseResponse.class);
                        startLogin(errorResponse);
                    } catch (Exception e) {

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


    private void startLogin(BaseResponse errorResponse) {
        try {
            if (errorResponse.getCode() == (MyConstant.T_ERR_AUTH) || errorResponse.getCode() == MyConstant.T_LOGIN_ERR) {//token过期
                MyConstant.HASLOGIN = false;
                SPUtils.remove(mContext, "login");
                SPUtils.remove(mContext, "token");
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                MyApplication.getInstance().showShortToast("服务器连接失败");
            }
        } catch (Exception e) {

        }
    }


    private void generateOrder() {
        StringBuilder sb = new StringBuilder();
        ArrayList<OrderGoods> orderList = new ArrayList<OrderGoods>();
        OrderGoods orderGoods;
        for (CartGoodsBase cartGoodsBase : checkList) {
            sb.append(cartGoodsBase.getGoods_attr_id()).append(",");
            orderGoods = new OrderGoods(cartGoodsBase.getGoods_attr_id(), cartGoodsBase.getGoods_number());
            orderList.add(orderGoods);
        }
        final String goods_attr_id = sb.deleteCharAt(sb.length() - 1).toString();
        final String queryId = GsonUtil.toJsonString(orderList);
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
//        final int goods_type = groupList.get(0).getGoods_type();
        Call<BaseResponse<List<OrderCartBase>>> call = userBiz.generateOrder(token,goods_attr_id, queryId, "");
        call.enqueue(new HttpCallBack<BaseResponse<List<OrderCartBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<OrderCartBase>>> arg0,
                                   Response<BaseResponse<List<OrderCartBase>>> response) {

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
                        Intent intent = new Intent(mContext, SubmitCarOrderActivity.class);
                        intent.putExtra("data", data);
                        intent.putExtra("goods_type", 0);
                        intent.putExtra("from_type", 1);
                        intent.putExtra("goods_attr_id", goods_attr_id);
                        intent.putExtra("queryId", queryId);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<OrderCartBase>>> arg0, Throwable arg1) {

                ToastUtil.showToast(mContext, "连接服务器失败,请检查您的网络状态");
            }
        });
    }


    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        // 下拉刷新操作
        getShoppingCart(2);

    }

    @Override
    public void onLoadMore(PullLayout pullToRefreshLayout) {

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {//已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
//            getShoppingCart(1);
        } else {//未登录
            MyConstant.HASLOGIN = false;
        }

    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(ToCarMsgEvent messageEvent) {
        getShoppingCart(1);

    }*/

    private void addShoppingCart(String attr_id, String num, String type) {
        dialog.show();
        dialog.setCancelable(false);
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
//                        ToastUtil.showToast(mContext, "加入购物车成功");
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
    @Override
    public void onClick(View item, View widget, int position, int which) {
        CartGoodsBase cartGoodsBase = groupList.get(position);
        TextView edit_num = (TextView) item.findViewById(R.id.tv_buy_num);
        String number = edit_num.getText().toString();
        int num;
        if (!StringUtils.isBlank(number)) {
            num = Integer.valueOf(number);
        } else {
            num = 1;
        }
        switch (which) {
            case R.id.tv_num_jian://减
                if (num == 1) {
                    num = 1;
                    ToastUtil.showToast(mContext, "不能再减了");
                } else {
                    num--;
                }
                cartGoodsBase.setNumber(num + "");
                addShoppingCart(cartGoodsBase.getGoods_attr_id(),String.valueOf(num),"1");
                adapter.notifyDataSetChanged();
                reFlashView();
                break;
            case R.id.tv_num_jia://加
                if (num >= Integer.valueOf(cartGoodsBase.getGoods_number())) {
                    num =  Integer.valueOf(cartGoodsBase.getGoods_number());
                    ToastUtil.showToast(mContext, "超过最大库存了");
                } else {
                    num++;
                    addShoppingCart(cartGoodsBase.getGoods_attr_id(),String.valueOf(num),"1");
                }
                cartGoodsBase.setNumber(num + "");
                adapter.notifyDataSetChanged();
                addShoppingCart(cartGoodsBase.getGoods_attr_id(),String.valueOf(num),"1");
                reFlashView();
                break;
            case R.id.cb_check://选择框
                boolean isChecked = cartGoodsBase.isChecked();
                cartGoodsBase.setChecked(!isChecked);
                adapter.notifyDataSetChanged();
                reFlashView();
                break;
            case R.id.ll_shop_info://
                String id = cartGoodsBase.getId();
                Intent intent = new Intent();
                intent.putExtra("productId", id);
                intent.setClass(mContext, GoodsDetalActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    public void reFlashView() {
        try {
            itemCheckNumber = 0;
            checkList.clear();
            totalMoney = "0.00";
            for (CartGoodsBase bean : groupList) {
                boolean isChecked = bean.isChecked();
                if (isChecked) {
                    itemCheckNumber++;
                    checkList.add(bean);
                    String singleTotalMoney = CompuUtils.multiply(bean.getPrice(),bean.getNumber()).toString();
                    totalMoney = CompuUtils.add(totalMoney, singleTotalMoney).toString();
                }
            }
            if (itemCheckNumber == groupList.size()) {
                isSelectAll = true;
            } else {
                isSelectAll = false;
            }
            tv_car_Allprice.setText(totalMoney);
            cb_car_all.setChecked(isSelectAll);
            itemCheckNumber = checkList.size();
            if (itemCheckNumber != 0) {
                tv_num.setText("(" + itemCheckNumber + ")");
                tv_num.setVisibility(View.VISIBLE);
            } else {
                tv_num.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

    }


}
