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
public class DialogSelectExchange {

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.destionStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_select_exchange);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView okBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_cancel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(true);
                }
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
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
