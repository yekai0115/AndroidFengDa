package com.fengda.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.Address;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.dialog.DialogNotify;
import com.fengda.app.dialog.DialogSelectExchange;
import com.fengda.app.eventbus.MsgEvent10;
import com.fengda.app.eventbus.MsgEvent17;
import com.fengda.app.eventbus.MsgEvent9;
import com.fengda.app.eventbus.ToGeRenMsgEvent;
import com.fengda.app.fragment.CarFragment;
import com.fengda.app.fragment.GeRenFragment;
import com.fengda.app.fragment.HomeFragment;
import com.fengda.app.fragment.NewProductFragment;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.model.Message;
import com.fengda.app.utils.ConnectionUtil;
import com.fengda.app.utils.PermissionUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.widget.viewpager.ControlScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, PermissionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String KEY_SELECTED_PAGE = "KEY_SELECTED_PAGE";


    private TextView tv_shouye;
    private TextView tv_fenlei;
    private TextView tv_car;
    private TextView tv_geren;
    private ControlScrollViewPager mViewPager;


    private static long lastClickTime = System.currentTimeMillis();
    private List<Fragment> mViews;
    private Context mContext;


    private NotificationManager notificationManager;
    private Notification notification;
    private Intent updateIntent;
    private PendingIntent pendingIntent;
    private String app_name = "";
    private int notification_id = 0x1122;
    protected int updateCount;
    private final int DOWN_UPDATE = 1;
    private final int DOWN_OVER = 2;
    private String downApkPath = null;
    private boolean toShopCar = false;
    private AppUpdateInfo updateInfo;
    /**
     * 1：安装apk文件 2.下载apk文件
     */
    public int types;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        mContext = this;
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
        } else {// 未登录
            MyConstant.HASLOGIN = false;
        }
        BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
        int selectedPage = 0;
        if (savedInstanceState != null) {
            selectedPage = savedInstanceState.getInt(KEY_SELECTED_PAGE);
        }
        initFragment();
        initUI(selectedPage);
        getNotice();
        if (getIntent().getBooleanExtra("toShopCar", false)) {
            toShopCar = true;
            mViewPager.setCurrentItem(2);
        }
    }

    /**
     * 获取公告
     */
    private void getNotice() {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<Message>>> call = userBiz.getNotify();
        call.enqueue(new HttpCallBack<BaseResponse<List<Message>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Message>>> arg0,
                                   Response<BaseResponse<List<Message>>> response) {

                super.onResponse(arg0, response);
                BaseResponse<List<Message>> baseResponse = response.body();
                if (null != baseResponse) {
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<Message> messageList=  baseResponse.getData();
                        if(null==messageList||messageList.isEmpty()){
                            return;
                        }else{
                            DialogNotify alert = new DialogNotify();
                            alert.showDialog(MainActivity.this,messageList);
                        }
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Message>>> arg0, Throwable arg1) {


            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_PAGE, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    private void initUI(int selectedPage) {
        tv_shouye = (TextView) findViewById(R.id.tv_shouye);
        tv_fenlei = (TextView) findViewById(R.id.tv_fenlei);
        tv_car = (TextView) findViewById(R.id.tv_car);
        tv_geren = (TextView) findViewById(R.id.tv_geren);
        tv_shouye.setSelected(true);
        PageAdapter mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager = (ControlScrollViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setClipToPadding(false);
        mViewPager.setCurrentItem(selectedPage);
        mViewPager.addOnPageChangeListener(this);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_shouye://首页
                        mViewPager.setCurrentItem(0);
                        resetImgButtonStatus(0);
                        break;
                    case R.id.tv_fenlei://分类
                        mViewPager.setCurrentItem(1);
                        resetImgButtonStatus(1);
                        break;
                    case R.id.tv_car://购物车
                        String login = (String) SPUtils.get(mContext, "login", "");
                        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {//已登录
                            MyConstant.HASLOGIN = true;
                            mViewPager.setCurrentItem(2);
                            resetImgButtonStatus(2);
                        } else {//未登录
                            MyConstant.HASLOGIN = false;
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.tv_geren://我的
                        mViewPager.setCurrentItem(3);
                        resetImgButtonStatus(3);
                        EventBus.getDefault().post(new ToGeRenMsgEvent(1));
                        break;
                }
            }
        };
        tv_shouye.setOnClickListener(onClickListener);
        tv_fenlei.setOnClickListener(onClickListener);
        tv_car.setOnClickListener(onClickListener);
        tv_geren.setOnClickListener(onClickListener);
        resetImgButtonStatus(selectedPage);
    }


    private void initFragment() {// 点击底部调用
        mViews = new ArrayList<Fragment>();
        HomeFragment homeFragment = new HomeFragment();//首页
        NewProductFragment categoryFragment = new NewProductFragment();
        CarFragment carFragment = new CarFragment();
        GeRenFragment geRenFragment = new GeRenFragment();
        mViews.add(homeFragment);
        mViews.add(categoryFragment);
        mViews.add(carFragment);
        mViews.add(geRenFragment);
    }


    @Override
    protected void onResume() {
        int item = mViewPager.getCurrentItem();
        resetImgButtonStatus(item);
        super.onResume();
    }

    /**
     * 底部按钮状态
     *
     * @param index
     */
    private void resetImgButtonStatus(int index) {
        switch (index) {
            case 0://首页
                toShopCar = false;
                tv_shouye.setSelected(true);
                tv_fenlei.setSelected(false);
                tv_car.setSelected(false);
                tv_geren.setSelected(false);
                break;
            case 1://分类
                toShopCar = false;
                tv_shouye.setSelected(false);
                tv_fenlei.setSelected(true);
                tv_car.setSelected(false);
                tv_geren.setSelected(false);
                break;
            case 2://购物车
                tv_car.setSelected(true);
                tv_fenlei.setSelected(false);
                tv_shouye.setSelected(false);
                tv_geren.setSelected(false);
                break;
            case 3://我的
                toShopCar = false;
                tv_geren.setSelected(true);
                tv_fenlei.setSelected(false);
                tv_shouye.setSelected(false);
                tv_car.setSelected(false);
                break;
        }
    }

    boolean doubleBackClickedFinish = false;

    @Override
    public void onBackPressed() {
        if (toShopCar) {
            finish();
            return;
        }
        if (!doubleBackClickedFinish) {
            this.doubleBackClickedFinish = true;
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackClickedFinish = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    // -------------------------------------- viewpager part ---------------------------------------
    private final class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mViews.get(position);
        }

        @Override
        public int getCount() {
            return mViews.size();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrentItem(position);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //防止快速的重复点击
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 400) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 检查有无文件读写权限
     *
     * @param
     * @param
     */
    protected void checkPermisson() {
        if (PermissionUtils.isNeedCheckPermisson(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermisson();
        } else {// 有权限
            if (types == 1) {
                BDAutoUpdateSDK.cpUpdateInstall(mContext, downApkPath);// 安装apk
            } else if (types == 2) {
                BDAutoUpdateSDK.cpUpdateDownload(mContext, updateInfo, new UpdateDownloadCallback());// 下载apk
            }

        }

    }

    private void requestPermisson() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, 1);
    }

    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions,
                                         PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    type);
        } else {
            onGranted(type);
        }
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @Override
    public void onGranted(int type) {

        if (types == 1) {
            BDAutoUpdateSDK.cpUpdateInstall(mContext, downApkPath);
        } else if (types == 2) {
            BDAutoUpdateSDK.cpUpdateDownload(mContext, updateInfo, new UpdateDownloadCallback());// 下载apk
        }
    }

    // 权限被拒绝
    @Override
    public void onDenied(List<String> deniedPermission) {

    }


    // 版本检测回调
    private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(final AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                types = 1;
                downApkPath = infoForInstall.getInstallPath();
                checkPermisson();
            } else if (info != null) {
                alertNeedUpgrade(mContext, info);
            } else {
                //   txtLog.setText(txtLog.getText() + "\n no update.");
            }

            //   dialog.dismiss();
        }

        private String byteToMb(long fileSize) {
            float size = ((float) fileSize) / (1024f * 1024f);
            return String.format("%.2fMB", size);
        }
    }


    /**
     * 弹出版本更新提示
     *
     * @param ctx
     */

    private void alertNeedUpgrade(final Context ctx, final AppUpdateInfo info) {
        final Dialog lDialog = new Dialog(ctx, R.style.destionStyle);
        lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lDialog.setContentView(R.layout.dialog_new_version);
        lDialog.setCancelable(false);
        ((TextView) lDialog.findViewById(R.id.tv_new_version)).setText(ctx
                .getResources().getString(R.string.version_update)
                + "("
                + info.getAppVersionName() + ")"); // 应用版本名称
        TextView updateTv = ((TextView) lDialog
                .findViewById(R.id.tv_update_content));
        ImageView img_close = ((ImageView) lDialog.findViewById(R.id.img_close));
        updateTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        String change = info.getAppChangeLog();
        int versionCode = info.getAppVersionCode();// 最新版本号
        if (versionCode % 2 == 0) {// 偶数版本号：非强制更新
            img_close.setVisibility(View.VISIBLE);
        } else {// 奇数版本号：强制更新
            img_close.setVisibility(View.GONE);
        }
        try {
            change = change.replace("<br>", "\r\n").toString();
        } catch (Exception e) {
            change = info.getAppChangeLog();
        }
        updateTv.setText(change); // 新版本更新的内容
        // 更新
        ((TextView) lDialog.findViewById(R.id.btn_update))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lDialog.dismiss();
                        // 非WIFI网络
                        if (!ConnectionUtil.isWifiConnected(ctx)) {
                            alertNotWifiNetworkDialogNormal(ctx, info);
                            return;
                        }
                        // BDAutoUpdateSDK.cpUpdateDownload(ctx, info, new
                        // UpdateDownloadCallback());//下载apk
                        types = 2;
                        updateInfo = info;
                        checkPermisson();

                    }
                });
        // 不更新
        img_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lDialog.dismiss();
            }
        });
        lDialog.show();
    }


    /**
     * 非WIFI网络提示
     */
    @SuppressLint("NewApi")
    private void alertNotWifiNetworkDialogNormal(final Context ctx,
                                                 final AppUpdateInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("提示");
        builder.setMessage("当前网络处于2G/3G/4G网络，确定下载吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                types = 2;
                updateInfo = info;
                checkPermisson();
            }

        });
        builder.show();
    }


    //    // 下载回调
    private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

        /**
         * 下载完成 /storage/emulated/0/com.baidu.autoupdate/
         */
        @Override
        public void onDownloadComplete(String apkPath) {
            // 安装
            // BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(),
            // apkPath);
            downApkPath = apkPath;
            EventBus.getDefault().post(new MsgEvent10());
        }

        /**
         * cpUpdateDownload被调用时触发回调该方法
         */
        @Override
        public void onStart() {
            createNotification();
        }

        /**
         * 下载进度通过该方法通知应用 percent：进度百分百 rcvLen：已下载文件大小 fileSize：文件总大小
         */
        @Override
        public void onPercent(int percent, long rcvLen, long fileSize) {
            updateCount = percent;
            EventBus.getDefault().post(new MsgEvent9());
        }

        /**
         * 下载失败或者发送错误时回调
         */
        @Override
        public void onFail(Throwable error, String content) {

        }

        /**
         * 下载流程结束后统一调次接口
         */
        @Override
        public void onStop() {
            // txt_log.setText(txt_log.getText() + "\n Download onStop");
        }

    }


    private void createNotification() {
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notification = new Notification();
//        notification.icon = R.mipmap.app_logo;
//        // 这个参数是通知提示闪出来的值.
//        notification.tickerText = "开始下载";
//        updateIntent = new Intent();
//        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
//        // notification.flags |=
//        // Notification.FLAG_AUTO_CANCEL;//该通知能被状态栏的清除按钮给清除掉
//        // 这里面的参数是通知栏view显示的内容
//        notification.setLatestEventInfo(this, app_name, "下载：0%", pendingIntent);
//        notificationManager.notify(notification_id, notification);
    }

    //正在下载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MsgEvent9 event) {
//        notification.setLatestEventInfo(mContext, "正在下载:" + app_name,
//                updateCount + "%" + "", pendingIntent);
//        notificationManager.notify(notification_id, notification);
    }

    //正在下载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent17 event) {
        mViewPager.setCurrentItem(3);
        resetImgButtonStatus(3);
    }

    //下载完成
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MsgEvent10 event) {
        File updateFile = new File(downApkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
//        notification.setLatestEventInfo(mContext, app_name, "下载成功，点击安装",
//                pendingIntent);
        if (Build.VERSION.SDK_INT >= 24) { // 判读版本是否在7.0以上
            // 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致 参数3 共享的文件
            Uri apkUri = FileProvider.getUriForFile(mContext,
                    "com.fengda.app.fileprovider", updateFile);
            // 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
            mContext.startActivity(intent);
            //     notificationManager.cancel(notification_id);
        } else {
            intent.setDataAndType(Uri.fromFile(updateFile),
                    "application/vnd.android.package-archive");
            //   notificationManager.notify(notification_id, notification);
            BDAutoUpdateSDK.cpUpdateInstall(mContext, downApkPath);
            //    notificationManager.cancel(notification_id);
        }
    }

}
