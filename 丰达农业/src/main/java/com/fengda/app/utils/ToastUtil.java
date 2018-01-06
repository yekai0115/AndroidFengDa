package com.fengda.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fengda.app.R;
import com.fengda.app.activity.LoginActivity;
import com.fengda.app.constant.MyConstant;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class ToastUtil {

    private static String oldMsg;
    private static long time;

    /**
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        // 获取LayoutInflater对象，该对象可以将布局文件转换成与之一致的view对象
        if(StringUtils.isBlank(msg)){
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 将布局文件转换成相应的View对象
        View layout = inflater.inflate(R.layout.my_toast_layout, null);
        // 从layout中按照id查找TextView对象
        TextView textView = (TextView) layout.findViewById(R.id.tvForToast);
        // 设置TextView的text内容
        textView.setText(msg);
        // 实例化一个Toast对象
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        if(!msg.equals(oldMsg)){// 当显示的内容不一样时，即断定为不是同一个Toast
            toast.show();
        } else {
            // 显示内容一样时，只有间隔时间大于2秒时才显示
            if (System.currentTimeMillis() - time > 2000) {
                toast.show();
                time = System.currentTimeMillis();
            }
        }
        oldMsg = msg;
    }
    public static void showReLogin(Context context) {
        MyConstant.HASLOGIN = false;
        SPUtils.remove(context, "login");
        SPUtils.remove(context, "token");
        SPUtils.remove(context, "phone");
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }


    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

}
