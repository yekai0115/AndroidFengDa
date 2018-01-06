package com.fengda.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.fengda.app.R;
import com.fengda.app.activity.LoginActivity;
import com.fengda.app.activity.RecommendActivity;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.RecommendInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.QQConstants;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent11;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.utils.CommonUtils;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.FileUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pullableview.PullableRefreshScrollView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

@ContentView(R.layout.activity_my_recommend)
public class HomeRecommendFragment extends Fragment implements PullLayout.OnRefreshListener, IUiListener {


    @ViewInject(R.id.tv_1)
    private TextView tv_1;
    @ViewInject(R.id.tv_2)
    private TextView tv_2;
    @ViewInject(R.id.tv_3)
    private TextView tv_3;

    @ViewInject(R.id.tv_recommend)
    private TextView tv_recommend;
    @ViewInject(R.id.tv_recommend_num)
    private TextView tv_recommend_num;

    @ViewInject(R.id.ll_recommend)
    private  LinearLayout ll_recommend;
    @ViewInject(R.id.ll_num)
    private  LinearLayout ll_num;



    @ViewInject(R.id.refresh_view)
    private PullLayout refresh_view;

    @ViewInject(R.id.mScrollView)
    private PullableRefreshScrollView mScrollView;
    private Bitmap qrForSave;
    private View mRootView;
    private String token;
    private String phone;
    private int imgHeight;

    @ViewInject(R.id.a_recommend_qrcode)
    private ImageView a_recommend_qrcode;

    /**
     * 上下文
     **/
    private Context mContext;
    private Tencent mTencent;// 新建Tencent实例用于调用分享方法
    private IWXAPI api;
    private String shareUrl;

    public HomeRecommendFragment() {
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
        updateRecommInfo(1);
        return mRootView;
    }

    private void setWidget() {
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
       if(StringUtils.isBlank(phone)){
           shareUrl="http://www2.ddzyigou.com/public/register";
       }else{
           shareUrl="http://www2.ddzyigou.com/public/register/" + phone;
       }
        mTencent = Tencent.createInstance(QQConstants.mAppid, mContext);
        api = WXAPIFactory.createWXAPI(mContext, WxConstants.APP_ID, true);
        api.registerApp(WxConstants.APP_ID);
        setTextviewTitle(tv_1);
        setTextviewTitle2(tv_2);
        setTextviewTitle2(tv_3);
        setQRCode();
        a_recommend_qrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogConfirm dialogConfirm = new DialogConfirm();
                dialogConfirm.setListener(new DialogConfirm.OnOkCancelClickedListener() {
                    @Override
                    public void onClick(boolean isOkClicked) {
                        if (isOkClicked) {
                            if (FileUtils.saveBitmap(mContext, qrForSave)) {
                                Toast.makeText(mContext, getResources().getString(R.string.a_recommend_save_success), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialogConfirm.showDialog(getActivity(), getResources().getString(R.string.a_recommend_save), "确定", "取消");
                return false;
            }
        });
    }
    //  普通会员：购买一台小哈产品或者激活账户，购买价格为1688元/台，可获得1688智品积分。
    //  普通会员：购买一台小哈产品，购买价格为1688元/台，可获得1688智品积分。
    private void setTextviewTitle(TextView tv) {
        SpannableStringBuilder sp = new SpannableStringBuilder(tv.getText().toString());
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.bg_main_bottom)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 19, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 30, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new AbsoluteSizeSpan(15, true), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make them also bold
        StyleSpan bss2 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss2,  19, 24, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        StyleSpan bss3 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss3,  30, 34, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(sp);
    }

    private void setTextviewTitle2(TextView tv) {
        SpannableStringBuilder sp = new SpannableStringBuilder(tv.getText().toString());
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.bg_main_bottom)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 27, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 38, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new AbsoluteSizeSpan(15, true), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make them also bold
        StyleSpan bss2 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss2, 27, 31, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        StyleSpan bss3 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss3, 38, 42, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(sp);
    }

    private void setTextviewTitle3(TextView tv) {
        SpannableStringBuilder sp = new SpannableStringBuilder(tv.getText().toString());
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.bg_main_bottom)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 33, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tv_color7)), 44, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
        sp.setSpan(new AbsoluteSizeSpan(15, true), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make them also bold
        StyleSpan bss2 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss2,33, 38, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        StyleSpan bss3 = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sp.setSpan(bss3, 44, 48, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(sp);
    }


    private void updateRecommInfo(int state){
        if (MyConstant.HASLOGIN) {
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            ll_recommend.setVisibility(View.VISIBLE);
            ll_num.setVisibility(View.VISIBLE);
            getMyReferInformation(state);
        } else {
            ll_recommend.setVisibility(View.INVISIBLE);
            ll_num.setVisibility(View.INVISIBLE);
            if(state==2){
                refresh_view.refreshFinish(PullLayout.SUCCEED);
            }

        }
        phone = (String) SPUtils.get(mContext, "phone", "");
        if(StringUtils.isBlank(phone)){
            shareUrl="http://www2.ddzyigou.com/public/register";
        }else{
            shareUrl="http://www2.ddzyigou.com/public/register/" + phone;
        }
        setQRCode();


    }


    @Event({R.id.btn_share})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.btn_share://
                shareDialog();
                break;
        }
    }

    @Override
    public void onRefresh(PullLayout pullToRefreshLayout) {
        // 下拉刷新操作
        updateRecommInfo(2);

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {//已登录
            MyConstant.HASLOGIN = true;
        } else {//未登录
            MyConstant.HASLOGIN = false;
        }
        updateRecommInfo(1);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent11 messageEvent) {
       int code= messageEvent.getCode();
        switch (code) {
            // 发送成功
            case BaseResp.ErrCode.ERR_OK:
                ToastUtil.showToast(mContext,"分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: //用户取消

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://认证被否决
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED://发送失败
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:

                break;
            case BaseResp.ErrCode.ERR_COMM://一般错误
                break;
            default://其他不可名状的情况
                break;
        }

    }




    /**
     * 查询
     */
    private void getMyReferInformation(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<RecommendInfo>>> call = userBiz.myReferInformation(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<RecommendInfo>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<RecommendInfo>>> arg0,
                                   Response<BaseResponse<List<RecommendInfo>>> response) {
                super.onResponse(arg0, response);
                if(state==2){
                    refresh_view.refreshFinish(PullLayout.SUCCEED);
                }
                BaseResponse<List<RecommendInfo>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<RecommendInfo> data= baseResponse.getData();
                        if(null==data||data.isEmpty()){
                            tv_recommend.setText("");
                            tv_recommend_num.setText("0");
                            return;
                        }
                        RecommendInfo recommendInfo= data.get(0);
                        tv_recommend.setText(recommendInfo.getMobile());
                        tv_recommend_num.setText(recommendInfo.getCount());
                    }else{
                        ToastUtil.showToast(mContext, desc);
                        tv_recommend.setText("");
                        tv_recommend_num.setText("0");
                    }
                }else{
                    try {
                        int code = response.code();
                        String error = response.errorBody().string();
                        BaseResponse errorResponse = GsonUtil.GsonToBean(error, BaseResponse.class);
                        if (errorResponse.getCode() == (MyConstant.T_ERR_AUTH) || errorResponse.getCode() == MyConstant.T_LOGIN_ERR) {//token过期
                            MyConstant.HASLOGIN = false;
                            SPUtils.remove(mContext, "login");
                            SPUtils.remove(mContext, "token");
                            updateRecommInfo(1);
                        } else {
                            MyApplication.getInstance().showShortToast("服务器连接失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<RecommendInfo>>> arg0, Throwable arg1) {
                ToastUtil.showToast(mContext, "服务器连接失败");
                if(state==2){
                    refresh_view.refreshFinish(PullLayout.FAIL);
                }
            }
        });
    }

    private void shareDialog() {
        final Dialog lDialog = new Dialog(mContext, R.style.shareDialogStyle);
        lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lDialog.setContentView(R.layout.share_layout);
        lDialog.setCancelable(true);
        lDialog.setCanceledOnTouchOutside(true);
        LinearLayout ll_wx = (LinearLayout) lDialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq = (LinearLayout) lDialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq = (LinearLayout) lDialog.findViewById(R.id.ll_qq);
        LinearLayout ll_kj = (LinearLayout) lDialog.findViewById(R.id.ll_kj);
        TextView tv_quxiao = (TextView) lDialog.findViewById(R.id.tv_quxiao);
        View close_view = (View) lDialog.findViewById(R.id.close_view);
        // 从底部弹出
        Window dialogWindow = lDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        RelativeLayout lLayout_bg = (RelativeLayout) lDialog.findViewById(R.id.rl_share);
        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int)
                (display.getWidth()), LayoutParams.MATCH_PARENT));
        tv_quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lDialog.dismiss();

            }
        });
        close_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lDialog.dismiss();

            }
        });
        ll_wx.setOnClickListener(new View.OnClickListener() {//分享到微信
            @Override
            public void onClick(View v) {
                shareToWeChat(0);
                lDialog.dismiss();

            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {//分享到微博
            @Override
            public void onClick(View v) {
                shareToWeChat(1);
                lDialog.dismiss();

            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {//分享到朋友圈
            @Override
            public void onClick(View v) {
                if (ToastUtil.isAvilible(mContext, "com.tencent.mobileqq")) {
                    shareToQQ();
                } else {
                    ToastUtil.showToast(mContext, "请先安装QQ!");
                }
                lDialog.dismiss();

            }
        });
        ll_kj.setOnClickListener(new View.OnClickListener() {//分享到qq
            @Override
            public void onClick(View v) {
//                if (ToastUtil.isAvilible(mContext, "com.tencent.mobileqq")) {
//                    shareToQzone();
//                } else {
//                    ToastUtil.showToast(mContext, "请先安装QQ!");
//                }
                shareToQzone();
                lDialog.dismiss();

            }
        });

        lDialog.show();
    }

    public void shareToQQ() {
        File appIconfile = createFile();
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "邀请您注册智品惠");// 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "智品惠是以智能产品为核心的一款结合线上上传、线下体验中心的自动化购买模式的APP");// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);// 内容地址
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, appIconfile.getAbsolutePath());// 网络图片地址　　
         params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "智品惠");// 应用名称
        params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其它附加功能");
        mTencent.shareToQQ(getActivity(), params, this);
    }

    private void shareToQzone() {
        File appIconfile = createFile();
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "邀请您注册智品惠");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "智品惠是以智能产品为核心的一款结合线上上传、线下体验中心的自动化购买模式的APP");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, appIconfile.getAbsolutePath());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "智品惠");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(getActivity(), params, this);
    }


    // 0-分享给朋友  1-分享到朋友圈
    private void shareToWeChat(int flag) {
        if (!api.isWXAppInstalled()) {
            ToastUtil.showToast(mContext, "请先安装微信!");
            return;
        }
        File appIconfile = createFile();
        //创建一个WXWebPageObject对象，用于封装要发送的Url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        //创建一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "邀请您注册智品惠";
        msg.description = "智品惠是以智能产品为核心的一款结合线上上传、线下体验中心的自动化购买模式的APP";


        Bitmap bmp = BitmapFactory.decodeFile(appIconfile.getAbsolutePath());
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        bmp.recycle();


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());//transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.message = msg;
        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);


        //  msg.thumbData =getThumbData();//封面图片byte数组


//
//        WXWebpageObject localWXWebpageObject = new WXWebpageObject();
//        localWXWebpageObject.webpageUrl = "http://www.mojichina.com/";;
//        WXMediaMessage localWXMediaMessage = new WXMediaMessage(
//                localWXWebpageObject);
//        localWXMediaMessage.title = "测试";;//
//        localWXMediaMessage.description = "这是我做的一款天气类app，高端大气上档次，快来看看吧！";;
//        Bitmap bmp = MyTools.readBitMap(mDrawableId, mContext);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, WxConstants.THUMB_SIZE, WxConstants.THUMB_SIZE, true);
//        // 设置缩略图;
//        localWXMediaMessage.thumbData = MmUtil.bmpToByteArray(thumbBmp, true);
//        SendMessageToWX.Req localReq = new SendMessageToWX.Req();
//        localReq.transaction = buildTransaction("webpage");
//        localReq.message = localWXMediaMessage;
//        localReq.scene = SendMessageToWX.Req.WXSceneTimeline;
//        System.out.println("请求分享朋友圈");
//        api.sendReq(localReq);


    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public void onComplete(Object o) {
        // 操作成功
    }

    @Override
    public void onError(UiError uiError) {
        // 分享异常
    }

    @Override
    public void onCancel() {
        // 取消分享
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, this);
            }
        }
    }

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    private void setQRCode() {
        final String filePath = getFileRoot(mContext) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        imgHeight = a_recommend_qrcode.getHeight();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = CommonUtils.createQRImage(shareUrl, 200, 200, null, filePath);
                if (success) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrForSave = BitmapFactory.decodeFile(filePath);
                            a_recommend_qrcode.setImageBitmap(qrForSave);
                        }
                    });
                }
            }
        }).start();
    }


    private File createFile(){
        File file = new File(MyConstant.APP_HOME_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        File appIconfile = new File(MyConstant.SHARE_IMAGE_PATH);
        try {
            if (!appIconfile.exists()) {
                appIconfile.createNewFile();
            }
            FileUtils.copyAssetsFile(mContext, "app_icon.png", appIconfile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appIconfile;
    }



}
