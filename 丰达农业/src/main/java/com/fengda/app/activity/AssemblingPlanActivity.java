package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.AssembkungInfo;
import com.fengda.app.bean.AssemblingPayBean;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.DialogWidget;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent2;
import com.fengda.app.eventbus.MsgEvent3;
import com.fengda.app.eventbus.MsgEvent8;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.HandyTextView;
import com.fengda.app.view.PayPasswordView;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
/**
 * 金柚计划页面
 */
public class AssemblingPlanActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private HandyTextView canCash;
    private HandyTextView aeBankName;
    private Button confirmButton;
    private HandyTextView exchangeEdit;
    private TextView tv_name,ae_receive_mobile;
    private DialogConfirm dialogConfirm;
    private DialogWidget mDialogWidget;
    private String token;
    private String bankCard;
    private long mAmount;
    private TextView aeAmount,acbTv7,exRemarks,tv_bcast_number;
    private RelativeLayout asRelative1,asRelative2,as_relative3;
    private String title;
    /**
     * 1金柚计划A
     * 2金柚计划B
     * 3金柚计划C
     */
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembling_plan);
        mContext = this;
        // 注册事件
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        dialogConfirm = new DialogConfirm();
        type = getIntent().getStringExtra("type");
        initViews();
        setSlidr();
        initDialog();
        initEvents();
        getAssembkungInfo(1);
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
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(mContext, PomeloBuyListActivity.class);
                startActivity(intent);
            }
        });
        aeBankName = (HandyTextView) findViewById(R.id.ae_bank_name);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        exchangeEdit = (HandyTextView) findViewById(R.id.tv_fruit_num);
        canCash = (HandyTextView) findViewById(R.id.ae_can_cash);
        aeAmount = (TextView) findViewById(R.id.tv_money_amount);
        exRemarks = (TextView) findViewById(R.id.ex_remarks);
        aeBankName.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ae_bank_name:
                if (StringUtils.isBlank(bankCard)) {//绑卡：需要设置支付密码、实名认证
                    checkPayPwdState(1);
                } else {//改卡：已经设置了支付密码、已提交实名认证
                    checkVeriState(3);//查询认证状态
                }
                break;
            case R.id.confirm_button://兑换
                confirmJoin();
                break;
        }
    }
    /**
     * 查询支付密码状态
     * 1：绑卡操作
     * 2：兑换操作
     */
    private void checkPayPwdState(int state) {
        int payState = (int) SPUtils.get(mContext, "payPwd" + token, 0);
        if (payState == MyConstant.SUCCESS) {//已设置支付密码
            if (state == 1) {//绑卡操作：已设置支付密码,未实名认证
                showVerifyDialog(1, getResources().getString(R.string.tv_41));
            } else if (state == 2) {//兑换操作：已设置支付密码,查询认证状态
                checkVeriState(2);
            }
        } else {//未设置支付密码
            showSetPayPwdDialog();
        }
    }

    /**
     * 查询认证状态
     * 1：默认查询
     * 2：兑换操作
     * 3：修改银行卡操作
     */
    private void checkVeriState(int state) {
        int status = (int) SPUtils.get(mContext, "verified" + token, 0);
        if (status == 3) {//已认证
            showResetBankDialog();
        } else {//查询认证状态
            getAssembkungInfo(state);
        }
    }


    /**
     * 弹出兑换密码弹窗
     */
    private void showExchangeDialog() {
        String value = exchangeEdit.getText().toString();
        mDialogWidget = new DialogWidget(AssemblingPlanActivity.this, getDecorViewDialog(value,title));
        mDialogWidget.show();
    }


    /**
     * 修改银行卡提示（已设置支付密码，已实名认证）
     */
    private void showResetBankDialog() {
        dialogConfirm.showDialog(AssemblingPlanActivity.this, getResources().getString(R.string.ae_alter_bank), "确定", "取消");
        dialogConfirm.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {
                    Intent intent = new Intent(mContext, VerifyBankCardActivity.class);
                    intent.putExtra("title","修改银行卡");
                    intent.putExtra("state", 2);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 设置支付密码弹窗
     */
    private void showSetPayPwdDialog() {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {//进入设置支付密码页面
                    startActivity(SetPayPwdActivity.class);
                }

            }
        });
        alert.showDialog(AssemblingPlanActivity.this, getResources().getString(R.string.tv_40), "确定", "取消");
    }

    /**
     * 实名认证弹窗
     * @param state 1未认证；2认证未通过
     * @param title
     */
    private void showVerifyDialog(final int state, String title) {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {//进入实名认证流程页面
                    Intent intent = new Intent(mContext, CertificationBindUserActivity.class);
                    intent.putExtra("state",state);
                    startActivity(intent);
                }

            }
        });
        alert.showDialog(AssemblingPlanActivity.this, title, "确定", "取消");
    }


    protected View getDecorViewDialog(final String amount,final  String title) {
        return PayPasswordView.getInstance(amount, title, this, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(String password) {
                mDialogWidget.dismiss();
                mDialogWidget = null;
            }

            @Override
            public void onCancelPay() {
                mDialogWidget.dismiss();
                mDialogWidget = null;
            }
        }).getView();
    }

    @Override
    protected void initEvents() {

    }

    /**
     * 查询认证状态
     * 1：默认查询金柚计划
     * 2：兑换操作
     * 3：修改银行卡操作
     */
    private void getAssembkungInfo(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<AssembkungInfo>>> call = userBiz.jinYouInfo(token,String.valueOf(type));
        call.enqueue(new HttpCallBack<BaseResponse<List<AssembkungInfo>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<AssembkungInfo>>> arg0,
                                   Response<BaseResponse<List<AssembkungInfo>>> response) {
                dialog.dismiss();
                super.onResponse(arg0,response);
                BaseResponse<List<AssembkungInfo>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS ) {
                        confirmButton.setClickable(true);
                        confirmButton.setBackground(getResources().getDrawable(R.drawable.bg_login_style));
                    } else if(retCode == MyConstant.FAILED ){
                        confirmButton.setText(desc);
                        confirmButton.setClickable(false);
                        confirmButton.setBackground(getResources().getDrawable(R.drawable.bg_gray_style));

                    }
                    List<AssembkungInfo> data = baseResponse.getData();
                    if (data == null || data.size() == 0)
                        return;
                    AssembkungInfo assembkungInfo = data.get(0);
                    bankCard = assembkungInfo.getBankname();
                    if (!StringUtils.isBlank(bankCard)) {
                        aeBankName.setText(bankCard);
                    }
                    String canCashText = assembkungInfo.getRerurn_need();
                    if (!StringUtils.isBlank(canCashText)) {
                        canCash.setText(canCashText);
                    }
                    exchangeEdit.setText(assembkungInfo.getMoney_need());
                    aeAmount.setText(assembkungInfo.getSum_return());
                    int status = assembkungInfo.getStatus();//认证状态0未提交,2认证失败,1审核中,3认证通过
                    SPUtils.put(mContext, "verified" + token, status);
                    if (status == 3) {//认证通过
                        if (state == 3) {//修改银行卡操作
                            showResetBankDialog();
                        }
                    } else if (status == 0) {//未认证
                        if (state == 2) {//兑换操作:已设置支付密码，未实名认证
                            showVerifyDialog(1, getResources().getString(R.string.tv_41));
                        }
                    } else if (status == 1) {//审核中
                        if (state != 1) {//
                            ToastUtil.showToast(mContext, "您的实名认证正在审核中");
                        }
                    } else {
                        if (state != 1) {//认证失败，修改认证信息
                            showVerifyDialog(2, getResources().getString(R.string.tishi_12));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<AssembkungInfo>>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }



    /**
     * 兑换
     * @param
     */
    private void confirmJoin() {
        //验证支付密码
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<AssemblingPayBean>>> call = userBiz.joinJinYouActivity(token,type);
        call.enqueue(new HttpCallBack<BaseResponse<List<AssemblingPayBean>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<AssemblingPayBean>>> arg0,
                                   Response<BaseResponse<List<AssemblingPayBean>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0, response);
                BaseResponse<List<AssemblingPayBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    String msg=baseResponse.getMsg();
                    if (retCode == MyConstant.SUCCESS) {
                        List<AssemblingPayBean> object = baseResponse.getData();
                        Intent intent = new Intent(AssemblingPlanActivity.this,AssemblingOrderActivity.class);
                        intent.putExtra("assemblingPayBean",(Serializable) object.get(0));
                        intent.putExtra("activityId",type);
                        startActivity(intent);
                    }else {
                        ToastUtil.showToast(mContext,msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<AssemblingPayBean>>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
            }
        });

    }


    //支付密码设置成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent2 msgEvent2) {
        SPUtils.put(mContext, "payPwd" + token, MyConstant.SUCCESS);
    }

    //实名认证提交成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent3 msgEvent3) {
        getAssembkungInfo(1);
    }


    //银行卡更换成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent8 msgEvent8) {
        getAssembkungInfo(1);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getAssembkungInfo(1);
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消事件注册
        EventBus.getDefault().unregister(this);
    }
}
