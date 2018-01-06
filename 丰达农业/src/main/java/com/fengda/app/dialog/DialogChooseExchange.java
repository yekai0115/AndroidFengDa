package com.fengda.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fengda.app.R;
import com.fengda.app.adapter.PlanAdapter;
import com.fengda.app.constant.MyConstant;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogChooseExchange {
private PlanAdapter planAdapter;
    public void showDialog(final Activity activity, final Context context) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_choose_exchange);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = screenWidth;
        dialog.getWindow().setAttributes(layoutParams);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        LinearLayout ll_exchange_name1 = (LinearLayout) dialog.findViewById(R.id.ll_exchange_name1);
        LinearLayout ll_exchange_name2 = (LinearLayout) dialog.findViewById(R.id.ll_exchange_name2);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll_exchange_name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onClick(MyConstant.EXCHANGE_JIYOUMEI);
                    dialog.dismiss();
                }
            }
        });
        ll_exchange_name2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onClick(MyConstant.EXCHANGE_CASH);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private OnExchangeClickedListener listener;

    public void setListener(OnExchangeClickedListener listener) {
        this.listener = listener;
    }

    public interface OnExchangeClickedListener {
        void onClick(int position);
    }
}
