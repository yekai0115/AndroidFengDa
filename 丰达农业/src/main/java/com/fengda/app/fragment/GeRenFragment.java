package com.fengda.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.activity.AssemblingPlanActivity;
import com.fengda.app.activity.DeclarationActivity;
import com.fengda.app.activity.ExchangeActivity;
import com.fengda.app.activity.GoodsCollectActivity;
import com.fengda.app.activity.LoginActivity;
import com.fengda.app.activity.MailOrderActivity;
import com.fengda.app.activity.MyMessageActivity;
import com.fengda.app.activity.MyRecommendActivity;
import com.fengda.app.activity.MyTeamListActivity;
import com.fengda.app.activity.PraiseListActivity;
import com.fengda.app.activity.SetPayPwdActivity;
import com.fengda.app.activity.SettingActivity;
import com.fengda.app.activity.VerifyBankCardActivity;
import com.fengda.app.activity.VerifyPayPwdActivity;
import com.fengda.app.adapter.UserMenuAdapter;
import com.fengda.app.aliutil.PutObjectSamples;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.AssemblingBase;
import com.fengda.app.bean.AuthError;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.MenuBean;
import com.fengda.app.bean.PersonAuth;
import com.fengda.app.bean.UserBean;
import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.MyOSSConfig;
import com.fengda.app.dialog.DialogChooseExchange;
import com.fengda.app.dialog.DialogChoosePlan;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.DialogSelPhoto;
import com.fengda.app.dialog.DialogSelectExchange;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent18;
import com.fengda.app.eventbus.MsgEvent2;
import com.fengda.app.eventbus.MsgEvent3;
import com.fengda.app.eventbus.MsgEvent5;
import com.fengda.app.eventbus.OrderUpdateEvent;
import com.fengda.app.eventbus.ToGeRenMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.model.ControlAuth;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.ImgSetUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.utils.luban.Luban;
import com.fengda.app.utils.luban.OnCompressListener;
import com.fengda.app.view.pullableview.PullableRefreshScrollView;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.widget.GlideCircleTransform;
import com.jauker.widget.BadgeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@ContentView(R.layout.main_fragment_wode)
public class GeRenFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener,
        PermissionListener, ActivityCompat.OnRequestPermissionsResultCallback,AdapterView.OnItemClickListener {


    /**
     * 头像
     */
    @ViewInject(R.id.img_login)
    private ImageView img_login;

    @ViewInject(R.id.ll_login)
    private LinearLayout ll_login;

    @ViewInject(R.id.tv_my_order)
    private TextView tv_my_order;
    /**
     * 未登录
     */
    @ViewInject(R.id.tv_login)
    private TextView tv_login;
    /**
     * 手机号
     */
    @ViewInject(R.id.tv_phone)
    private TextView tv_phone;
    /**
     * 会员等级
     */
    @ViewInject(R.id.tv_dengji)
    private TextView tv_dengji;

    /**
     * 待产果
     */
    @ViewInject(R.id.tv_wait_fruit)
    private TextView tv_wait_fruit;
    /**
     * 产果
     */
    @ViewInject(R.id.tv_produce_fruit)
    private TextView tv_produce_fruit;

    /**
     * 消费果
     */
    @ViewInject(R.id.tv_consumption_fruit)
    private TextView tv_consumption_fruit;

    /**
     * 吉优美
     */
    @ViewInject(R.id.tv_abeautiful_fruit)
    private TextView tv_abeautiful_fruit;

    @ViewInject(R.id.tv_verify_state)
    private TextView tv_verify_state;

    @ViewInject(R.id.gril_menu)
    private GridView gril_menu;


    @ViewInject(R.id.wisdom_integral)
    private LinearLayout wisdom_integral;

    @ViewInject(R.id.tv_news)
    private ImageView tv_news;

    @ViewInject(R.id.tv_paying)
    private TextView tv_paying;

    @ViewInject(R.id.tv_sending)
    private TextView tv_sending;

    @ViewInject(R.id.tv_receiving)
    private TextView tv_receiving;

    @ViewInject(R.id.tv_completed)
    private TextView tv_completed;

    /**
     * 兑换
     */
    @ViewInject(R.id.commodity_integral)
    private LinearLayout commodity_integral;

    @ViewInject(R.id.tv_news_linear)
    private LinearLayout tv_news_linear;

    @ViewInject(R.id.ll_huokuan)
    private LinearLayout ll_huokuan;

    @ViewInject(R.id.ll_pomelo)
    private LinearLayout ll_pomelo;


    @ViewInject(R.id.category_refresh_view)
    private PullToRefreshLayout category_refresh_view;

    @ViewInject(R.id.scroll_view)
    private PullableRefreshScrollView scroll_view;

    private List<AssemblingBase> assemblingPlans;
    private BadgeView badgeView1,badgeView2,badgeView3,badgeView4,badgeView5;
    private View mRootView;
    private int state;
    private OSS oss;
    private String localTempImageFileName;
    private int type = 0;
    private String token;
    private UserMenuAdapter userMenuAdapter;
    private  int[] typeArray = new int[]{1,2,3,4,5,6,7,8,9};
    private String [] textMenu = new String[]{"实名认证","兑换","转让果子","复投",
            "报单","收藏夹","推荐好友","金柚计划","团队"};
    private int[] images = new int[]{R.drawable.my_autonym,R.drawable.my_exchange,R.drawable.my_make_over, R.drawable.my_repetition,
            R.drawable.my_taxation_form,R.drawable.my_collect,R.drawable.my_recommend,R.drawable.my_plan,
            R.drawable.my_team};
    /**
     * 上下文
     **/
    private Context mContext;
    private DialogSelPhoto dialogSelPhoto;
    private ArrayList<MenuBean> menuBeans = new ArrayList<>();
    public GeRenFragment() {
        super();
    }
    public DialogChoosePlan dialogChoosePlan;
    public DialogChooseExchange dialogChooseExchange;
    public int clickPosition;
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
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = x.view().inject(this, inflater, container);

        }
        ViewGroup mViewGroup = (ViewGroup) mRootView.getParent();
        if (mViewGroup != null) {
            mViewGroup.removeView(mRootView);
        }

        badgeView1 = new BadgeView(getActivity());
        badgeView1.setTargetView(tv_news_linear);
        badgeView1.setBadgeMargin(0,0,(int)getResources().getDimension(R.dimen.text_pdding3),0);
        badgeView1.setBackground(20, Color.parseColor("#ff3838"));
        badgeView1.setBadgeGravity(Gravity.RIGHT);
        badgeView2 = new BadgeView(getActivity());
        badgeView2.setText("0");
        badgeView2.setTargetView(tv_paying);
        badgeView2.setBadgeMargin(0,(int)getResources().getDimension(R.dimen.text_pdding),25,0);
        badgeView2.setBackground(20, Color.parseColor("#ff3838"));
        badgeView2.setHideOnNull(true);
        badgeView2.setBadgeGravity(Gravity.RIGHT);
        badgeView3 = new BadgeView(getActivity());
        badgeView3.setTargetView(tv_sending);
        badgeView3.setText("0");
        badgeView3.setBadgeMargin(0,(int)getResources().getDimension(R.dimen.text_pdding),25,0);
        badgeView3.setBackground(20, Color.parseColor("#ff3838"));
        badgeView3.setBadgeGravity(Gravity.RIGHT);
        badgeView4 = new BadgeView(getActivity());
        badgeView4.setTargetView(tv_receiving);
        badgeView4.setBadgeMargin(0,(int)getResources().getDimension(R.dimen.text_pdding),25,0);
        badgeView4.setBackground(20, Color.parseColor("#ff3838"));
        badgeView4.setHideOnNull(true);
        badgeView4.setText("0");
        badgeView4.setBadgeGravity(Gravity.RIGHT);
        badgeView5 = new BadgeView(getActivity());
        badgeView5.setTargetView(tv_completed);
        badgeView5.setBadgeMargin(0,(int)getResources().getDimension(R.dimen.text_pdding),25,0);
        badgeView5.setBackground(20, Color.parseColor("#ff3838"));
        badgeView5.setBadgeGravity(Gravity.RIGHT);
        badgeView5.setText("0");
        category_refresh_view.setOnRefreshListener(this);
        dialogSelPhoto = new DialogSelPhoto();
        oss = new OSSClient(mContext, MyConstant.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        setWidget(1);
        dialogChoosePlan = new DialogChoosePlan();
        dialogChoosePlan.setListener(new DialogChoosePlan.OnPlanClickedListener() {
            @Override
            public void onClick(int position) {
                Intent intent;
                if (MyConstant.HASLOGIN) {
                    clickPosition = position;
                    int status = (int) SPUtils.get(mContext, "verified" + token, 0);
                    if (status == 3) {//已认证
                        intent = new Intent(mContext, AssemblingPlanActivity.class);
                        intent.putExtra("type", assemblingPlans.get(0).getActivities().get(clickPosition).getId());
                        startActivity(intent);
                    } else {//查询认证状态
                        getPersonAuth(6);
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialogChooseExchange = new DialogChooseExchange();
        dialogChooseExchange.setListener(new DialogChooseExchange.OnExchangeClickedListener() {
            @Override
            public void onClick(int position) {
                Intent intent;
                if (MyConstant.HASLOGIN) {
                    int payState = (int) SPUtils.get(mContext, "payPwd" + token, 0);
                    if (payState == MyConstant.SUCCESS) {//已设置支付密码
                        int status = (int) SPUtils.get(mContext, "verified" + token, 0);
                        if (status == 3) {//已认证
                            intent = new Intent(mContext, ExchangeActivity.class);
                            intent.putExtra("state",position);
                            startActivity(intent);
                        } else {//查询认证状态
                            getPersonAuth(3);
                        }
                    } else {//未设置支付密码
                        showdDialog(1, getResources().getString(R.string.tv_40), null);
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        for (int i = 0; i <textMenu.length ; i++) {
            MenuBean menuBean = new MenuBean();
            menuBean.setAuth("1");
            menuBean.setText(textMenu[i]);
            menuBean.setLogoDrable(images[i]);
            menuBean.setType(typeArray[i]);
            menuBeans.add(menuBean);
        }
        userMenuAdapter = new UserMenuAdapter(mContext,menuBeans);
        gril_menu.setAdapter(userMenuAdapter);
        setListViewHeightBasedOnChildren(gril_menu);
        gril_menu.setOnItemClickListener(this);
        scroll_view.smoothScrollTo(0,0);
        return mRootView;
    }

    @Override
    protected void lazyLoad() {
        scroll_view.smoothScrollTo(0,1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent;
        switch (menuBeans.get(i).getType()){
            case 1://实名认证
                if (MyConstant.HASLOGIN) {
                    int payState = (int) SPUtils.get(mContext, "payPwd" + token, 0);
                    if (payState == MyConstant.SUCCESS) {//已设置支付密码
                        int status = (int) SPUtils.get(mContext, "verified" + token, 0);
                        if (status == 3) {//已认证
                            ToastUtil.showToast(mContext, "实名认证已通过");
                        } else {//查询认证状态
                            intent = new Intent(mContext,VerifyPayPwdActivity.class);
                            startActivity(intent);
                        }
                    } else {//未设置支付密码
                        showdDialog(1, getResources().getString(R.string.tv_40), null);
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 8://金柚计划
                if (MyConstant.HASLOGIN) {
                    getPlanList();
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 2://兑换
                if (MyConstant.HASLOGIN) {
                    dialogChooseExchange.showDialog(getActivity(),mContext);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 3://转让果子
                if (MyConstant.HASLOGIN) {
                    int payState = (int) SPUtils.get(mContext, "payPwd" + token, 0);
                    if (payState == MyConstant.SUCCESS) {//已设置支付密码
                        int status = (int) SPUtils.get(mContext, "verified" + token, 0);
                        if (status == 3) {//已认证
                            intent = new Intent(mContext, ExchangeActivity.class);
                            intent.putExtra("state", MyConstant.EXCHANGE_TRANSFEN);
                            startActivity(intent);
                        } else {//查询认证状态
                            getPersonAuth(4);
                        }
                    } else {//未设置支付密码
                        showdDialog(1, getResources().getString(R.string.tv_40), null);
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 4://复投
                if (MyConstant.HASLOGIN) {
                    int payState = (int) SPUtils.get(mContext, "payPwd" + token, 0);
                    if (payState == MyConstant.SUCCESS) {//已设置支付密码
                        int status = (int) SPUtils.get(mContext, "verified" + token, 0);
                        if (status == 3) {//已认证
                            intent = new Intent(mContext, ExchangeActivity.class);
                            intent.putExtra("state", MyConstant.EXCHANGE_VOTING);
                            startActivity(intent);
                        } else {//查询认证状态
                            getPersonAuth(5);
                        }
                    } else {//未设置支付密码
                        showdDialog(1, getResources().getString(R.string.tv_40), null);
                    }
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 9://团队
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MyTeamListActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                    startActivity(intent);
                break;
            case 6://收藏夹
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, GoodsCollectActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case 7://推荐好友
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MyRecommendActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case 5://报单
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, DeclarationActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
        }
    }

    private void setWidget(int state) {
        if (MyConstant.HASLOGIN) {
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            ll_login.setVisibility(View.VISIBLE);
            tv_login.setVisibility(View.GONE);
            getUserinfo(state);
        } else {
            if (state == 2) {
                category_refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
            ll_login.setVisibility(View.GONE);
            tv_login.setVisibility(View.VISIBLE);
            tv_wait_fruit.setText("0");
            tv_produce_fruit.setText("0");
            tv_consumption_fruit.setText("0");
            tv_abeautiful_fruit.setText("0");
            badgeView1.setText("0");
            badgeView2.setText("0");
            badgeView3.setText("0");
            badgeView4.setText("0");
            badgeView5.setText("0");

            Glide.with(mContext).load(R.drawable.img_default_head).override(80, 80).transform(new GlideCircleTransform(mContext)).into(img_login);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        setWidget(2);

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        // 加载操作
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    @Event({R.id.img_login, R.id.tv_login,
            R.id.tv_news_linear, R.id.tv_shezhi, R.id.wisdom_integral, R.id.commodity_integral,
            R.id.ll_huokuan,R.id.tv_my_order,R.id.tv_paying,R.id.tv_sending,R.id.tv_receiving,R.id.tv_completed,R.id.ll_pomelo})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.wisdom_integral://待产果
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, PraiseListActivity.class);
                    intent.putExtra("state", 1);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.commodity_integral://存果
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, PraiseListActivity.class);
                    intent.putExtra("state", 2);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);

                }
                startActivity(intent);
                break;
            case R.id.ll_huokuan://消费果
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, PraiseListActivity.class);
                    intent.putExtra("state", 3);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.ll_pomelo://吉柚美：  查看货款奖励记录（进的记录）
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, PraiseListActivity.class);
                    intent.putExtra("state", 4);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.img_login://个人页面
                break;
            case R.id.tv_login://登录
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_news_linear://我的消息
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MyMessageActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_shezhi://设置
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, SettingActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_my_order://我的订单(全部)
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MailOrderActivity.class);
                    intent.putExtra("position",0);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_paying://我的订单(待付款)
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MailOrderActivity.class);
                    intent.putExtra("position",1);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_sending://我的订单(待发货)
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MailOrderActivity.class);
                    intent.putExtra("position",2);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_receiving://我的订单(待收货)
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MailOrderActivity.class);
                    intent.putExtra("position",3);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.tv_completed://我的订单(已完成)
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, MailOrderActivity.class);
                    intent.putExtra("position",4);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
        }
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
        } else {//未登录
            MyConstant.HASLOGIN = false;
        }
        setWidget(1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent3 messageEvent) {
        getUserinfo(1);
    }
    //主页点击个人
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(ToGeRenMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {//已登录
            MyConstant.HASLOGIN = true;
        } else {//未登录
            MyConstant.HASLOGIN = false;
        }
        getUserinfo(1);
    }

    /**
     * 自动匹配listview的高度
     *
     * @param
     */

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
         if (listAdapter == null){
             return;
         }
         // 固定列宽，有多少列
        int col = 4;
         int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        int hightcount = (int) Math.ceil(listView.getCount() * 1.0 / col);
        for (int i = 0; i < hightcount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams  params = listView.getLayoutParams();
        params.height = totalHeight;
        ((ViewGroup.MarginLayoutParams)params).setMargins(10,10,10,10);
        listView.setLayoutParams(params);
    }
    //支付密码设置成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent2 msgEvent2) {
        SPUtils.put(mContext, "payPwd" + token, MyConstant.SUCCESS);
    }
    //支付密码验证成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent18 msgEvent18) {
        Intent intent;
        int status = (int) SPUtils.get(mContext, "verified" + token, 0);

            getPersonAuth(msgEvent18.getType());

    }

    //兑换成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent5 msgEvent5) {
        getUserinfo(1);
    }

    //订单有操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(OrderUpdateEvent orderUpdateEvent) {
        getUserinfo(1);
    }



    /**
     * 查询认证状态、支付密码状态、用户信息
     */
    private void getUserinfo(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<UserBean>>> call = userBiz.getUserInformation(token);
        call.enqueue(new Callback<BaseResponse<List<UserBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<UserBean>>> arg0,
                                   Response<BaseResponse<List<UserBean>>> response) {
                BaseResponse<List<UserBean>> baseResponse = response.body();
                if (state == 2) {
                    category_refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<UserBean> data = baseResponse.getData();
                        UserBean userBean = data.get(0);
                        String nick = userBean.getNick();
                        if (StringUtils.isBlank(nick)) {
                            tv_phone.setText(userBean.getMobile());
                        } else {
                            tv_phone.setText(nick);
                        }
                        tv_dengji.setText(userBean.getLevel_name()  );
                        tv_wait_fruit.setText(userBean.getSum_self());
                        tv_produce_fruit.setText(userBean.getSum_return());
                        tv_consumption_fruit.setText(userBean.getSun_consumption());
                        tv_abeautiful_fruit.setText(userBean.getSum_jiyoumie());
                        tv_verify_state.setText(userBean.getRemarks());
                        int type = userBean.getDerail();
                        String url;
                        if (type == 1) {
                            url=userBean.getHeadurl() + "/"+userBean.getHead();
                        } else {
                            url=userBean.getHeadurl() + userBean.getHead();
                        }
                        setControlAuth(userBean);
                        Glide.with(mContext).load(url).override(80, 80).transform(new GlideCircleTransform(mContext))
                                .placeholder(R.drawable.pic_nomal_loading_style)
                                .error(R.drawable.img_default_head)
                                .into(img_login);
                        int verified = userBean.getVerified();
                        if (verified == 3) {//认证通过
                            SPUtils.put(mContext, "verified" + token, verified);
                        }
                        String payState = userBean.getPwd();
                        if (!StringUtils.isBlank(payState)) {//已设置支付密码
                            SPUtils.put(mContext, "payPwd" + token, MyConstant.SUCCESS);
                        }
                        badgeView1.setText(userBean.getMsg_num());
                        badgeView2.setText(userBean.getPay_num());
                        badgeView3.setText(userBean.getDelivered_num());
                        badgeView4.setText(userBean.getReceived_num());
                        badgeView5.setText(userBean.getDone_num());
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }

                } else {
                    try {
                        int code = response.code();
                        String error = response.errorBody().string();
                        BaseResponse errorResponse = GsonUtil.GsonToBean(error, BaseResponse.class);
                        if (errorResponse.getCode() == (MyConstant.T_ERR_AUTH) || errorResponse.getCode() == MyConstant.T_LOGIN_ERR) {//token过期
                             MyConstant.HASLOGIN = false;
                            SPUtils.remove(mContext, "login");
                            SPUtils.remove(mContext, "token");
                            setWidget(1);
                        } else {
                            MyApplication.getInstance().showShortToast("服务器连接失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<UserBean>>> arg0, Throwable arg1) {
                ToastUtil.showToast(mContext, "服务器连接失败");
                if (state == 2) {
                    category_refresh_view.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
    }

    /**
     * 获取计划列表
     */
    private void getPlanList() {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<AssemblingBase>>> call = userBiz.jinyouActivity();
        call.enqueue(new Callback<BaseResponse<List<AssemblingBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<AssemblingBase>>> arg0,
                                   Response<BaseResponse<List<AssemblingBase>>> response) {
                BaseResponse<List<AssemblingBase>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        assemblingPlans = baseResponse.getData();
                        if (assemblingPlans!=null){
                            dialogChoosePlan.showDialog(getActivity(),mContext,assemblingPlans);
                        }
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<AssemblingBase>>> arg0, Throwable arg1) {
                ToastUtil.showToast(mContext, "服务器连接失败");
                if (state == 2) {
                    category_refresh_view.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
    }

    private  void setControlAuth(UserBean userBean){
        ControlAuth controlAuth = userBean.getControl_auth();
        if (controlAuth.getReturn_auth().equals("0")){
            commodity_integral.setVisibility(View.GONE);
        }
        if (controlAuth.getConsumption_auth().equals("0")){
            ll_huokuan.setVisibility(View.GONE);
        }
        if (controlAuth.getJiyoumei_auth().equals("0")){
            ll_pomelo.setVisibility(View.GONE);
        }
        if (userBean.getTeam_auth().equals("0")){
            for (int i = 0; i < menuBeans.size(); i++) {
                if (menuBeans.get(i).getType() == 9){
                    menuBeans.remove(i);
                    break;
                }
            }
        }
        if (userBean.getDeclaration_auth().equals("0")){
            for (int i = 0; i < menuBeans.size(); i++) {
                if (menuBeans.get(i).getType() == 5){
                    menuBeans.remove(i);
                    break;
                }
            }
        }
        if (controlAuth.getTransfer_auth().equals("0")){
            for (int i = 0; i < menuBeans.size(); i++) {
                if (menuBeans.get(i).getType() == 3){
                    menuBeans.remove(i);
                    break;
                }
            }
        }
        if (controlAuth.getRecast_auth().equals("0")){
            for (int i = 0; i < menuBeans.size(); i++) {
                if (menuBeans.get(i).getType() == 4){
                    menuBeans.remove(i);
                    break;
                }
            }
        }
        userMenuAdapter.notifyDataSetChanged();
//        scroll_view.smoothScrollTo(0,1);
        setListViewHeightBasedOnChildren(gril_menu);

    }
    /**
     * @param state 2:实名认证点击；3兑换点击
     */
    private void getPersonAuth(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<PersonAuth>>> call = userBiz.getPersonAuth(token);
        call.enqueue(new Callback<BaseResponse<List<PersonAuth>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<PersonAuth>>> arg0,
                                   Response<BaseResponse<List<PersonAuth>>> response) {

                BaseResponse<List<PersonAuth>> baseResponse = response.body();
                if (null != baseResponse) {
                    Intent intent;
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        List<PersonAuth> data = baseResponse.getData();
                        PersonAuth personAuth = data.get(0);
                        int status = personAuth.getStatus();
                        //实名认证0未申请,1待审核,2拒绝,3通过,4待完善资料
                        if (status == 0) {//未提交审核
                            showdDialog(3, getResources().getString(R.string.tv_41), null);
                        } else if (status == 1) {//1审核中
                            ToastUtil.showToast(mContext, "请等待实名认证审核通过后即可操作");
                        } else if (status == 2) {//2审核失败
                            showdDialog(2, getResources().getString(R.string.tishi_12), personAuth);
                        } else if (status == 3) {//3认证通过
                            SPUtils.put(mContext, "verified" + token, status);
                            if (state == 3) {//兑换点击
                                intent = new Intent(mContext, ExchangeActivity.class);
                                intent.putExtra("state",clickPosition);
                                startActivity(intent);
                            }else if(state == 4){
                                intent = new Intent(mContext, ExchangeActivity.class);
                                intent.putExtra("state", MyConstant.EXCHANGE_TRANSFEN);
                                startActivity(intent);
                            }else if(state == 5){
                                intent = new Intent(mContext, ExchangeActivity.class);
                                intent.putExtra("state", MyConstant.EXCHANGE_VOTING);
                                startActivity(intent);
                            }else if(state == 6){
                                intent = new Intent(mContext, AssemblingPlanActivity.class);
                                intent.putExtra("type", assemblingPlans.get(0).getActivities().get(clickPosition).getId());
                                startActivity(intent);
                            }else if(state == 2){
                                ToastUtil.showToast(mContext, "您已通过实名认证");
                            }
                        }else if (status == 4) {//4完善信息
                            showdDialog(4, getResources().getString(R.string.tishi_121), personAuth);
                        }
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<PersonAuth>>> arg0, Throwable arg1) {

                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }


    /**
     * state
     * 1:未设置支付密码
     * 2：实名认证未通过
     * 3：未提交实名认证
     * 4:完善实名认证信息
     */
    private void showdDialog(final int state, String title, final PersonAuth personAuth) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {//进入设置支付密码页面
                    Intent intent;
                    if (state == 1) {
                        intent = new Intent(mContext, SetPayPwdActivity.class);
                    } else {
                        if (state == 2 || state == 4) {
                            AuthError authError = personAuth.getRemarks();
                            String name = authError.getName();
                            String card_number = authError.getCard_number();
                            String hand_logo = authError.getHand_logo();
                            String front_card = authError.getFront_card();
                            String rear_card = authError.getRear_card();
                            String bank_card = authError.getBank_card();
                            String bank_logo = authError.getBank_logo();
                                intent = new Intent(mContext, VerifyBankCardActivity.class);
                                intent.putExtra("title","实名认证");
                                intent.putExtra("state", 3);
                                intent.putExtra("personAuth", personAuth);
//                            }

                        }
                        else {//未提交实名认证
                            intent = new Intent(mContext, VerifyBankCardActivity.class);
                            intent.putExtra("title","实名认证");
                            intent.putExtra("state", 1);
                        }


                    }
                    startActivity(intent);
                }

            }
        });
        alert.showDialog(getActivity(), title, "确定", "取消");
    }

    /**
     * 选择兑换内容
     */
    private void showdSelectExchangeDialog() {
        DialogSelectExchange alert = new DialogSelectExchange();
        alert.setListener(new DialogSelectExchange.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                Intent intent = new Intent(mContext, ExchangeActivity.class);
                if (isOkClicked) {//货款兑换
                    intent.putExtra("state", 1);
                } else {//鼓励积分兑换
                    intent.putExtra("state", 2);

                }
                startActivity(intent);
            }
        });
        alert.showDialog(getActivity());
    }

    /**
     * 相册
     *
     * @param value
     */
    private void takePhotoForAlbum(int value) {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, value);
    }

    /**
     * 拍照
     *
     * @param value
     */
    private void takePhotoForCamera(int value) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, value);
    }

    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions, PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), type);
        } else {
            onGranted(type);
        }
    }

    //获得权限
    @Override
    public void onGranted(int value) {
        if (type == 1) {//拍照
            captureImage(CreateFile.FEEDBACK_PATH, value);
        } else if (type == 2) {//相册
            selectImage(value);
        }
    }


    @Override
    public void onDenied(List<String> deniedPermission) {


    }

    /**
     * 拍照
     *
     * @param path 照片存放的路径
     */
    public void captureImage(String path, int value) {
        Uri data;
        localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";//拍照后的图片路径
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(CreateFile.FEEDBACK_PATH, localTempImageFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(getActivity(), "com.fengda.app.fileprovider", f);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(f);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, data);
        startActivityForResult(intent, value);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int value) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, value);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            switch (type) {
                case 1: {
                    handleCameraRet(data, requestCode);
                    break;
                }
                case 2: {
                    handleAlbumRet(data, requestCode);
                    break;
                }

                default:
                    break;
            }
        }
    }

    private void handleCameraRet(Intent data, int value) {
        // 将保存在本地的图片取出并缩小后显示在界面上
        String imgKey = ImgSetUtil.getImgKeyString();
        String path = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        compressWithLs(new File(path), value, imgKey);
    }


    private void handleAlbumRet(Intent data, int value) {
        String imgKey = ImgSetUtil.getImgKeyString();
        ContentResolver resolver = getActivity().getContentResolver();
        // 照片的原始资源地址
        Uri originalUri = data.getData();
        String path = getAblumPicPath(data, getActivity());
        compressWithLs(new File(path), value, imgKey);

    }


    /**
     * @param data
     * @param ac
     * @return
     * @方法说明:获得相册图片的路径
     * @方法名称:getAblumPicPath
     * @返回 String
     */
    public static String getAblumPicPath(Intent data, Activity ac) {
        Uri originalUri = data.getData();
        String path = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        if (originalUri != null && proj != null) {
            Cursor cursor = ac.getContentResolver().query(originalUri, null, null, null, null);
            if (cursor == null) {
                path = originalUri.getPath();
                if (!StringUtils.isEmpty(path)) {
                    String type = ".jpg";
                    String type1 = ".png";
                    if (path.endsWith(type) || path.endsWith(type1)) {
                        return path;
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            } else {
                /**将光标移至开，这个很重要，不小心很容易引起越**/
                cursor.moveToFirst();
                /**按我个人理解 这个是获得用户选择的图片的索引**/
                int column_index = cursor.getColumnIndex(proj[0]);
                /** 最后根据索引值获取图片路**/
                path = cursor.getString(column_index);
                cursor.close();
            }
        }
        return path;
    }


    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(final File f, final int value, final String imgKey) {
        Luban.get(mContext)
                .load(f)
                .putGear(Luban.THIRD_GEAR)
                .setFilename(System.currentTimeMillis() + "")
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File f) {
                        dialog.show();
                        upLoadAli(imgKey, f.getAbsolutePath(), value);
                    }

                    @Override
                    public void onError(Throwable e) {//压缩失败
                        upLoadAli(imgKey, f.getAbsolutePath(), value);
                    }
                }).launch();
    }


    private void upLoadAli(final String key, final String path, final int value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = new PutObjectSamples(oss, MyConstant.ALI_PUBLIC_BUCKET_RECOMMEND, key, path).putObjectFromLocalFile();
                if (flag) {//上传成功
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            switch (value) {
                                case 1:
                                    updateHeadPic(key);
                                    Glide.with(mContext).load(path).fitCenter().override(80, 80).transform(new GlideCircleTransform(mContext))
                                            // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                            .into(img_login);
                                    break;
                            }
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ToastUtil.showToast(getActivity(), "上传失败,请重新上传");
                        }
                    });
                }
            }
        }).start();
    }

    private LoadingDialog dialog;

    protected void initDialog() {
        dialog = new LoadingDialog(getActivity(), R.style.dialog, "请稍候...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void updateHeadPic(String key) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.updateHeadPic(token, key);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        ToastUtil.showToast(mContext, "头像修改成功");
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }

}
