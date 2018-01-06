package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.ExchangeInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.DialogWidget;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent2;
import com.fengda.app.eventbus.MsgEvent3;
import com.fengda.app.eventbus.MsgEvent5;
import com.fengda.app.eventbus.MsgEvent8;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.CommonUtils;
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

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class ExchangeActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private HandyTextView canCash;
    private HandyTextView aeBankName;
    private Button confirmButton;
    private EditText exchangeEdit;
    private TextView tv_name,ae_receive_mobile;
    private DialogConfirm dialogConfirm;
    private DialogWidget mDialogWidget;
    private String token;
    private String phone;
    private String bankCard;
    private long mAmount;
    private TextView aeAmount,acbTv7,exRemarks,tv_bcast_number;
    private RelativeLayout asRelative1,asRelative2,as_relative3;
    private String title;
    /**
     * 1货款提现
     * 2积分提现
     */
    private int type;
    private ExchangeInfo exchangeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        mContext = this;
        // 注册事件
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
        dialogConfirm = new DialogConfirm();
        type = getIntent().getIntExtra("state",MyConstant.EXCHANGE_CASH);
        initViews();
        setSlidr();
        initDialog();
        initEvents();
        getExchangeInfo(1);
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
                Intent intent = new Intent(mContext, ExchangeListActivity.class);
                intent.putExtra("state",type);
                startActivity(intent);
            }
        });
        aeBankName = (HandyTextView) findViewById(R.id.ae_bank_name);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        exchangeEdit = (EditText) findViewById(R.id.act_exchange_edit);
        canCash = (HandyTextView) findViewById(R.id.ae_can_cash);
        tv_name= (TextView) findViewById(R.id.tv_name);
        aeAmount = (TextView) findViewById(R.id.ae_amount);
        acbTv7 = (TextView) findViewById(R.id.acb_tv7);
        exRemarks = (TextView) findViewById(R.id.ex_remarks);
        tv_bcast_number = (TextView) findViewById(R.id.tv_bcast_number);
        asRelative1 = (RelativeLayout) findViewById(R.id.as_relative1);
        asRelative2 = (RelativeLayout) findViewById(R.id.as_relative2);
        as_relative3 = (RelativeLayout) findViewById(R.id.as_relative3);
        ae_receive_mobile = (TextView) findViewById(R.id.ae_receive_mobile);
        aeBankName.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        if(type==MyConstant.EXCHANGE_CASH){//提现
            topNvgBar.setTitle("兑换");
            tv_name.setText("可兑换产果");
            title = "兑换";
        }else if(type==MyConstant.EXCHANGE_TRANSFEN){//转让
            topNvgBar.setTitle("转让");
            tv_name.setText("产果");
            aeAmount.setText("转让数量");
            acbTv7.setVisibility(View.GONE);
            exRemarks.setVisibility(View.GONE);
            asRelative1.setVisibility(View.VISIBLE);
            asRelative2.setVisibility(View.GONE);
            confirmButton.setText("确认转让");
            title = "转让数量";
        }else if(type==MyConstant.EXCHANGE_VOTING){//复投
            topNvgBar.setTitle("复投");
            tv_name.setText("产果");
            aeAmount.setText("复投数量");
            acbTv7.setVisibility(View.GONE);
            as_relative3.setVisibility(View.VISIBLE);
            asRelative2.setVisibility(View.GONE);
            confirmButton.setText("确认复投");
            title = "复投金额";
        }else{//吉柚美
            topNvgBar.setTitle("兑换");
            tv_name.setText("可兑换吉柚美");
            title = "兑换";
        }
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
                if(type==MyConstant.EXCHANGE_CASH || type==MyConstant.EXCHANGE_JIYOUMEI) {//兑换产果
                    confirmExchange();
                }else if(type==MyConstant.EXCHANGE_TRANSFEN) {//转让
                    confirmAssignmentExchange();
                }else {//复投
                    confirmVotingExchange();
                }

                break;
        }
    }

    /**
     * 兑换产果检查参数
     */
    private void confirmExchange() {
        if (StringUtils.isBlank(bankCard)) {
            showShortToast("您还没有绑定银行卡");
            return;
        }
        String value = exchangeEdit.getText().toString();
        if (!StringUtils.isBlank(value)) {
            long amount = Long.parseLong(value);
            BigDecimal amountMoney = new BigDecimal(value);
            BigDecimal money = new BigDecimal(canCash.getText().toString());
            if (amountMoney.compareTo(money) == 1) {
                showShortToast("超过最大可用兑换金额");
                return;
            }
            if (amount <= 0) {
                showShortToast("提现金额不合法，请重新输入");
                return;
            }
            if (amount % 100 != 0) {
                showShortToast("提现金额必须是100的整数倍");
                return;
            }
            int compare = CommonUtils.compareTo(value, "10000");
            if (compare == 1) {
                showShortToast("一次最多只能兑换10000");
                return;
            }
            mAmount = amount;
            checkPayPwdState(2);//查询是否设置了支付密码
        } else {
            showShortToast("请输入提现金额");
            return;
        }
    }
    /**
     * 转让产果检查参数
     */
    private void confirmAssignmentExchange() {
        if (StringUtils.isBlank(ae_receive_mobile.getText().toString())) {
            showShortToast("您还没有填写接收人");
            return;
        }
        if (ae_receive_mobile.getText().toString().equals(phone)) {
            showShortToast("转让人不能是自己");
            return;
        }
        String value = exchangeEdit.getText().toString();
        if (!StringUtils.isBlank(value)) {
            long amount = Long.parseLong(value);
            BigDecimal amountMoney = new BigDecimal(value);
            BigDecimal money = new BigDecimal(canCash.getText().toString());
            if (amountMoney.compareTo(money) == 1) {
                showShortToast("超过最大可用转让金额");
                return;
            }
            if (amount <= 0) {
                showShortToast("转让金额不合法，请重新输入");
                return;
            }
//            if (amount % 100 != 0) {
//                showShortToast("转让金额必须是100的整数倍");
//                return;
//            }
//            int compare = CommonUtils.compareTo(value, "10000");
//            if (compare == 1) {
//                showShortToast("一次最多只能转让10000");
//                return;
//            }
            mAmount = amount;
            checkPayPwdState(2);//查询是否设置了支付密码
        } else {
            showShortToast("请输入转让金额");
            return;
        }
    }
    /**
     * 复投产果检查参数
     */
    private void confirmVotingExchange() {
        String value = exchangeEdit.getText().toString();
        if (!StringUtils.isBlank(value)) {
            long amount = Long.parseLong(value);
            BigDecimal amountMoney = new BigDecimal(value);
            BigDecimal money = new BigDecimal(canCash.getText().toString());
            BigDecimal surplusMoney = new BigDecimal(tv_bcast_number.getText().toString());

            if (amountMoney.compareTo(money) == 1) {
                showShortToast("超过最大可用复投金额");
                return;
            }
            if (exchangeInfo.getRecast_limit() == 0){
                if (amountMoney.compareTo(surplusMoney) == 1) {
                    showShortToast("超过最大剩余复投额度");
                    return;
                }
            }
            if (amount <= 0) {
                showShortToast("复投金额不合法，请重新输入");
                return;
            }
            if (amount % 100 != 0) {
                showShortToast("复投金额必须是100的整数倍");
                return;
            }
            int compare = CommonUtils.compareTo(value, "10000");
            if (compare == 1) {
                showShortToast("一次最多只能复投10000");
                return;
            }
            mAmount = amount;
            checkPayPwdState(2);//查询是否设置了支付密码
        } else {
            showShortToast("请输入复投金额");
            return;
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
            if (state == 2) {//兑换
                showExchangeDialog();
            } else if (state == 3) {//修改银行卡
                showResetBankDialog();
            }
        } else {//查询认证状态
            getExchangeInfo(state);
        }
    }


    /**
     * 弹出兑换密码弹窗
     */
    private void showExchangeDialog() {
        String value = exchangeEdit.getText().toString();
        mDialogWidget = new DialogWidget(ExchangeActivity.this, getDecorViewDialog(value,title));
        mDialogWidget.show();
    }


    /**
     * 修改银行卡提示（已设置支付密码，已实名认证）
     */
    private void showResetBankDialog() {
        dialogConfirm.showDialog(ExchangeActivity.this, getResources().getString(R.string.ae_alter_bank), "确定", "取消");
        dialogConfirm.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {
                    Intent intent;
                    intent = new Intent(mContext, VerifyPayPwdActivity.class);
//                    intent.putExtra("title","修改银行卡");
                    intent.putExtra("state", 2);
                    intent.putExtra("type", 2);//修改银行卡
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
        alert.showDialog(ExchangeActivity.this, getResources().getString(R.string.tv_40), "确定", "取消");
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
        alert.showDialog(ExchangeActivity.this, title, "确定", "取消");
    }


    protected View getDecorViewDialog(final String amount,final  String title) {
        return PayPasswordView.getInstance(amount, title, this, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(String password) {
                mDialogWidget.dismiss();
                mDialogWidget = null;
                confirmPwd(password);
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
     * 1：默认查询
     * 2：兑换操作
     * 3：修改银行卡操作
     */
    private void getExchangeInfo(final int state) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<ExchangeInfo>>> call = userBiz.exchangeInfo(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<ExchangeInfo>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<ExchangeInfo>>> arg0,
                                   Response<BaseResponse<List<ExchangeInfo>>> response) {
                dialog.dismiss();
                super.onResponse(arg0,response);
                BaseResponse<List<ExchangeInfo>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        List<ExchangeInfo> data = baseResponse.getData();
                        exchangeInfo = data.get(0);
                        bankCard = exchangeInfo.getBankname();
                        if (!StringUtils.isBlank(bankCard)) {
                            aeBankName.setText(bankCard);
                        }
                        String canCashText = exchangeInfo.getSum_return();
                        String canRecastText = exchangeInfo.getRecast_left();
                            if (!StringUtils.isBlank(canCashText)) {
                                canCash.setText(canCashText);
                            }
                        if(type == MyConstant.EXCHANGE_VOTING){
                            if (!StringUtils.isBlank(canRecastText)) {
                                tv_bcast_number.setText(canRecastText);
                            }
                            if (exchangeInfo.getRecast_limit() == 1){
                                as_relative3.setVisibility(View.VISIBLE);
                            }
                            exRemarks.setText("复投金额必须是100的整数倍且收取"+exchangeInfo.getExchange_ratio()+"的手续费");
                        }
                        if(type == MyConstant.EXCHANGE_JIYOUMEI){
                            canCash.setText(exchangeInfo.getSum_jiyoumie());
                            exRemarks.setText("兑换金额必须是100的整数倍且收取"+exchangeInfo.getExchange_jiyoumei_ratio()+"的手续费");
                        }else if(type == MyConstant.EXCHANGE_CASH){
                            exRemarks.setText("兑换金额必须是100的整数倍且收取"+exchangeInfo.getExchange_ratio()+"的手续费");
                        }else if(type == MyConstant.EXCHANGE_VOTING){
                            exRemarks.setText("复投金额必须是100的整数倍且收取"+exchangeInfo.getRecast_ratio()+"的手续费");
                        }
                        int status = exchangeInfo.getStatus();//认证状态0未提交,2认证失败,1审核中,3认证通过
                        SPUtils.put(mContext, "verified" + token, status);
                        if (status == 3) {//认证通过
                            if (state == 2) {//兑换操作
                                showExchangeDialog();
                            } else if (state == 3) {//修改银行卡操作
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
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ExchangeInfo>>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }



    /**
     * 兑换
     * @param password
     */
    private void confirmPwd(final String password) {
        //验证支付密码
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<ExchangeInfo>>> call;
        if(type==MyConstant.EXCHANGE_CASH){//产果兑换
            call = userBiz.canCashExchange(token, password, exchangeEdit.getText().toString());
        }else  if(type==MyConstant.EXCHANGE_TRANSFEN){//产果转让
            call = userBiz.transferReturn(token, password, exchangeEdit.getText().toString(),ae_receive_mobile.getText().toString());
        }else  if(type==MyConstant.EXCHANGE_VOTING){//产果复投
            call = userBiz.recastReturn(token, password, exchangeEdit.getText().toString());
        }else {//吉优美兑换
            call = userBiz.exchangeJiYouMei(token, password, exchangeEdit.getText().toString());
        }
        call.enqueue(new HttpCallBack<BaseResponse<List<ExchangeInfo>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ExchangeInfo>>> arg0,
                                   Response<BaseResponse<List<ExchangeInfo>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0, response);
                BaseResponse<List<ExchangeInfo>> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    String msg=baseResponse.getMsg();
                    if (retCode == MyConstant.SUCCESS) {
                        ExchangeInfo exchangeInfo =baseResponse.getData().get(0);
                        //提现成功
                        canCash.setText(exchangeInfo.getSum_return());
                        if (type==MyConstant.EXCHANGE_TRANSFEN){
                            ae_receive_mobile.setText("");
                        }
                        if (type==MyConstant.EXCHANGE_VOTING){
                            tv_bcast_number.setText(exchangeInfo.getRecast_left());
                        }
                        if (type==MyConstant.EXCHANGE_JIYOUMEI){
                            canCash.setText(exchangeInfo.getSum_jiyoumie());
                        }

                        exchangeEdit.setText("");
                        ToastUtil.showToast(mContext,msg);
                        EventBus.getDefault().post(new MsgEvent5());//个人页面刷新数据
                    }else if(retCode == MyConstant.PAY_PWD_ERROE){//密码错误
                        DialogConfirm alert = new DialogConfirm();
                        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
                            @Override
                            public void onClick(boolean isOkClicked) {
                                if (isOkClicked) {
                                } else {
                                    startActivity(SetPayPwdActivity.class);
                                }
                            }
                        });
                        alert.showDialog(ExchangeActivity.this, "支付密码错误,请重试", "确定", "忘记密码");
                    } else {
                        ToastUtil.showToast(mContext,msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ExchangeInfo>>> arg0,
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
        getExchangeInfo(1);
    }


    //银行卡更换成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent8 msgEvent8) {
        getExchangeInfo(1);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getExchangeInfo(1);
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
