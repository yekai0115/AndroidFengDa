package com.fengda.app.dialog.avloading;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.activity.LoginActivity;
import com.fengda.app.activity.MainActivity;
import com.fengda.app.activity.MyWebViewActivity;
import com.fengda.app.adapter.PlanAdapter;
import com.fengda.app.bean.AssemblingBase;
import com.fengda.app.bean.AssemblingPlan;
import com.fengda.app.constant.MyConstant;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogChoosePlan {
private PlanAdapter planAdapter;
    public void showDialog(final Activity activity, final Context context, final List<AssemblingBase> assemblingPlans) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_choose_plan);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = screenWidth;
        dialog.getWindow().setAttributes(layoutParams);
        planAdapter = new PlanAdapter(context,assemblingPlans);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView tv_rule = (TextView) dialog.findViewById(R.id.tv_rule);
        ListView lv_gold_grapefruit = (ListView) dialog.findViewById(R.id.lv_gold_grapefruit);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lv_gold_grapefruit.setAdapter(planAdapter);
        tv_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MyWebViewActivity.class);
                intent.putExtra("site_url", MyConstant.COM_SERVICE);
                intent.putExtra("name","活动规则");
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        lv_gold_grapefruit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(listener != null) {
                    listener.onClick(position);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private OnPlanClickedListener listener;

    public void setListener(OnPlanClickedListener listener) {
        this.listener = listener;
    }

    public interface OnPlanClickedListener {
        void onClick(int position);
    }
}
