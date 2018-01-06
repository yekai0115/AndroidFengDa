package com.fengda.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.adapter.BannerAdapter;
import com.fengda.app.adapter.NotifyAdapter;
import com.fengda.app.model.Message;
import com.fengda.app.widget.viewpager.LoopViewPager;
import com.fengda.app.widget.viewpager.MaterialIndicator;

import java.util.List;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogNotify {

    public void showDialog(Activity activity,List<Message> subjectsInfos) {
        final Dialog dialog = new Dialog(activity, R.style.destionStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_notify);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LoopViewPager pager = (LoopViewPager) dialog.findViewById(R.id.ve_notify);
        MaterialIndicator bannerIndicator = (MaterialIndicator) dialog.findViewById(R.id.bannerIndicator);

        NotifyAdapter notifyAdapter = new NotifyAdapter(activity,subjectsInfos);
        pager.setAdapter(notifyAdapter);
        if (subjectsInfos.size() > 1) {
            pager.setLooperPic(true);
            pager.addOnPageChangeListener(bannerIndicator);
            bannerIndicator.setAdapter(pager.getAdapter());
        } else {
            pager.setLooperPic(false);
        }
        dialog.show();
    }


}
