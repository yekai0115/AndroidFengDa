package com.fengda.app.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.fengda.app.R;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_SMS;

/**
 * 启动页面
 */
public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback, PermissionListener {
    private RelativeLayout rl_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        getPicDpi();
        if (Build.VERSION.SDK_INT >= 23) {// 6.0以上系统申请文件权限
            checkPermisson();
        } else {
            startMain();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkPermisson() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        requestRuntimePermission(permissions, this, 1);
    }

    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions,
                                         PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {// 如果permissionList不为空，说明需要申请这些权限
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    type);
        } else {//权限已有
            startMain();
        }
    }


    //获得权限
    @Override
    public void onGranted(int type) {
        startMain();
    }

    // 权限被拒绝
    @Override
    public void onDenied(List<String> deniedPermission) {
        startMain();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                onGranted(requestCode);
            } else {
                onDenied(deniedPermissions);
            }
        }
    }

    private void startMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start(SplashActivity.this);
            }
        }, 500);
    }

    protected void start(Context ctx) {
        Boolean isfirst = (Boolean) SPUtils.get(ctx, "isFirstRun", true);// 是否为第一次运行程序
        if (isfirst) {
            toMainActivity(2);
        } else {
            toMainActivity(1);
        }
    }

    private void toMainActivity(final int type) {
        rl_splash.animate().scaleXBy(0.1f).scaleYBy(0.1f).alphaBy(0.1f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                startActivity(new Intent(mContext, MainActivity.class));
//                overridePendingTransition(0,0);
                rl_splash.clearAnimation();
                if (type == 1) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {//第一次运行
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SPUtils.put(SplashActivity.this, "isFirstRun", false);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    /**
     * 获取高德地图表ID
     */
    private void getTableId() {
        startMain();
    }

    /**
     * densityDpi:
     * 320(xhdpi)
     * 480(xxhdpi)
     * 240(hdpi)
     * 160(mhdpi)
     * 120(lhdpi)
     */
    private void getPicDpi() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        double diagonalPixels = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        double screenSize = diagonalPixels / (160 * density);

        if (densityDpi == 240) {
            MyConstant.PIC_DPI2 = "1.png";
        } else if (densityDpi == 320) {
            MyConstant.PIC_DPI2 = "2.png";
        } else if (densityDpi == 480) {
            MyConstant.PIC_DPI2 = "3.png";
        }

    }


}
