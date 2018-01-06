package com.fengda.app.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.fengda.app.R;
import com.fengda.app.bean.Campaign;
import com.fengda.app.dialog.LoadingDialog;
import com.fengda.app.eventbus.ToActivityMsgEvent;
import com.fengda.app.view.AdvancedWebView;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * 活动
 */

@ContentView(R.layout.fragment_activitics)
public class ActiviticeFragment extends Fragment  implements AdvancedWebView.Listener{
    @ViewInject(R.id.top_nvg_bar)
    private TopNvgBar5 topNvgBar;
    @ViewInject(R.id.acw_webview)
    private AdvancedWebView webView;

    private Context mContext;
    private View mRootView;



    private LoadingDialog dialog;




    public ActiviticeFragment() {
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
        return mRootView;
    }

    private void setWidget(Campaign campaign) {
        topNvgBar.setLeftVisibility(View.GONE);
        dialog = new LoadingDialog(mContext, "加载中...");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WebSettings settings=webView.getSettings();
        //   settings.setTextSize(WebSettings.TextSize.SMALLEST);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
//       settings.setTextSize(WebSettings.TextSize.SMALLER);
//        settings.setTextSize(WebSettings.TextSize.SMALLEST);
        //      settings.setTextSize(WebSettings.TextSize.LARGER);
////        settings.setTextSize(WebSettings.TextSize.LARGEST);
        webView.loadUrl(campaign.getUrl());
        topNvgBar.setTitle(campaign.getTitle());

    }


    @Override
    public void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }





    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        webView.onPause();
        super.onPause();

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        dialog.show();
    }

    @Override
    public void onPageFinished(String url) {
        dialog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        dialog.dismiss();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(ToActivityMsgEvent messageEvent) {

        Campaign campaign= messageEvent.getCampaign();
        setWidget(campaign);

    }

}
