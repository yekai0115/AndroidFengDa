package com.fengda.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.fengda.app.R;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogConfirm {

    public void showDialog(Activity activity, String msg, String okBtnTitle, String cancelBtnTitle){
        final Dialog dialog = new Dialog(activity, R.style.destionStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView text = (TextView) dialog.findViewById(R.id.dialog_confirm_message);
        text.setText(msg);

        TextView okBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_cancel);
        okBtn.setText(okBtnTitle);
        cancelBtn.setText(cancelBtnTitle);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(true);
                }
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(false);
                }
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
        void onClick(boolean isOkClicked);
    }
}
