package com.fengda.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fengda.app.R;
import com.fengda.app.view.AdvancedWebView;
import com.fengda.app.widget.TopNvgBar5;


/**
 * Created by Administrator on 2017/7/15 0015.
 */

public class MyWebViewActivity extends BaseActivity implements AdvancedWebView.Listener{
    private AdvancedWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_web);
        initViews();
        initDialog();
    }

    @Override
    protected void initViews() {
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        String site_url=intent.getStringExtra("site_url");
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setTitle(name);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        webView = (AdvancedWebView) findViewById(R.id.acw_webview);
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
        webView.loadUrl(site_url);


        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //调用拨号程序
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return false;
            }
        });

    }
    @Override
    protected void initEvents() {

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) { return; }
        super.onBackPressed();
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

}
