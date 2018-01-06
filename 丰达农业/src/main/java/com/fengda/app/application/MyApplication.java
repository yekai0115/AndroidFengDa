package com.fengda.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.fengda.app.activity.LoginActivity;
import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.WxConstants;
import com.fengda.app.model.User;
import com.fengda.app.utils.ACache;
import com.fengda.app.utils.CrashHandler;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.ToastUtil;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.unionpay.sdk.ab.mContext;

/**
 * Created by Star on 2017/3/7.
 */
public class MyApplication extends Application {
    public static List<Activity> mList = new LinkedList<Activity>();

    public static IWXAPI api;
    private static final String TAG = MyApplication.class.getName();
    public static MyApplication mApplication;
    public static ExecutorService TASK_EXECUTOR;
    public static ExecutorService FIXED_EXECUTOR;

    private static final int PORT = 1234;
    /**
     * 保存添加到购物车的数据
     */
    public static int arrayList_cart_id = 0;
    /**
     * 保存添加到购物车的数据
     */
    public static ArrayList<HashMap<String, Object>> arrayList_cart = new ArrayList<HashMap<String, Object>>();
    /**
     * 保存购物车中选择的商品的总价数据
     */
    public static float Allprice_cart = 0;
    // 缓存相关
    public ACache mCache;

    public static MyApplication getInstance() {
        return mApplication;
    }

    static {
        TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
        FIXED_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(1); //线程池中只有一个线程，任务排队执行
    }

    public User mUser = new User();// 用户信息

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);// Xutils初始化
//        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        UMShareAPI.get(this);
//        //捕捉异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        api = WXAPIFactory.createWXAPI(getApplicationContext(), WxConstants.APP_ID, true);
        mApplication = this;
        mCache = ACache.get(getApplicationContext());
        if (null != mCache.getAsObject(MyConstant.AUTH_USER))
            mUser = (User) mCache.getAsObject(MyConstant.AUTH_USER);
        com.fengda.app.crash.CrashHandler.getInstance().init(this, new File(CreateFile.CRASH_PATH), null);
        CreateFile creat = new CreateFile(mApplication);
        creat.Create();

    }

    public static void unreisterApp() {
        // api.unregisterApp();
    }

    public static void reisterApp() {
        //   api.registerApp(WxConstants.APP_ID);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //    MultiDex.install(this);
    }

    /**
     * add Activity
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    /**
     * 完全退出系统
     */
    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            Log.e("SysApplication.exit", e.getMessage());
        } finally {
            System.exit(0);
        }

    }

    /**
     * 结束指定的Activity
     */
    public static void finishSingleActivity(Activity activity) {
        if (activity != null) {
            if (mList.contains(activity)) {
                mList.remove(activity);
            }
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity 在遍历一个列表的时候不能执行删除操作，所有我们先记住要删除的对象，遍历之后才去删除。
     */
    public static void finishSingleActivityByClass(Class cls) {
        Activity tempActivity = null;
        for (Activity activity : mList) {
            if (activity.getClass().equals(cls)) {
                tempActivity = activity;
            }
        }
        finishSingleActivity(tempActivity);
    }

    public void startLoginActivity() {
        MyConstant.HASLOGIN=false;
        SPUtils.clear(this);
        SPUtils.put(this, "isFirstRun", false);
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(MyConstant.CLOSE_ON_KEYDOWN, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ToastUtil.showToast(getApplicationContext(), "登录过期,请重新登录");
        startActivity(intent);
    }

    /**
     * 短暂显示Toast提示(来自res)
     **/
    public void showShortToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 短暂显示Toast提示(来自String)
     **/
    public void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
