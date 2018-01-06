package com.fengda.app.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class IntentUtil {

    // 打开新Activity
    public static void openActivity(Context context,Class<?> pClass) {
        openActivity(context,pClass, null);
    }

    public static void openActivity(Context context,Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(context, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        context.startActivity(intent);
        intent = null;
    }

}
