package com.fengda.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengda.app.R;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogSelPhoto {

    public void showDialog(Activity activity, final int imgIndex) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_sel_photo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = screenWidth;
        dialog.getWindow().setAttributes(layoutParams);

        TextView btnCamera = (TextView) dialog.findViewById(R.id.dsp_camera);
        TextView btnAlbum = (TextView) dialog.findViewById(R.id.dsp_album);
        TextView dspCancel = (TextView) dialog.findViewById(R.id.dsp_cancel);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(true, imgIndex);
                }
                dialog.dismiss();
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(false, imgIndex);
                }
                dialog.dismiss();
            }
        });
        dspCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private OnOkCancelClickedListener listener;

    public void setListener(OnOkCancelClickedListener listener) {
        this.listener = listener;
    }

    public interface OnOkCancelClickedListener {
        void onClick(boolean isCameraSel, int imgIndex);
    }
}
