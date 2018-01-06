package com.fengda.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.fengda.app.R;
import com.fengda.app.view.HandyTextView;

import java.util.List;

/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogSelectAddr {
    private int selection ;
    public void showProvincesDialog(Activity activity, final List<String> dataList, int position, final OnItemClickedListener listener,
                                    final OnConfirmClickedListener onConfirmClickedListener,
                                    final OnCancelClickedListener onCancelClickedListener) {
        final Dialog dialog = new Dialog(activity);
//        final Dialog dialog = new Dialog(new ContextThemeWrapper(activity, R.style.DialogSlideAnim));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_select1, null, true);
        dialog.setContentView(rootView);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(params);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(null);
        HandyTextView cancel = (HandyTextView) rootView.findViewById(R.id.cancel);
        HandyTextView confirm = (HandyTextView) rootView.findViewById(R.id.confirm);

        WheelView wheelView = (WheelView) rootView.findViewById(R.id.dialog_select1_wheel);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(activity));
        wheelView.setSkin(WheelView.Skin.Holo);
        wheelView.setWheelData(dataList);
        wheelView.setWheelSize(5);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        wheelView.setStyle(style);
        wheelView.setWheelClickable(true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onCancelClickedListener.OnCancelClick();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onConfirmClickedListener.onConfirmClick(0, selection);
            }
        });
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                selection = position;
            }
        });
        wheelView.setSelection(position);
        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onItemClick(int position, Object o) {
                dialog.dismiss();
                listener.onClick(0, position);
            }

        });
        dialog.show();
    }

    public interface OnItemClickedListener {
        void onClick(int dialogIndex, int selectedIndex);
    }
    public interface OnConfirmClickedListener {
        void onConfirmClick(int dialogIndex, int selectedIndex);
    }
    public interface OnCancelClickedListener {
        void OnCancelClick();
    }
}
