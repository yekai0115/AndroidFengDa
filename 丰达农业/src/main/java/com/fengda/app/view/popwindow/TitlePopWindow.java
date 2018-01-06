package com.fengda.app.view.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengda.app.view.popwindow.TitlePopAdapter.onItemClickListener;
import com.fengda.app.R;

import java.util.ArrayList;

public class TitlePopWindow extends PopupWindow implements OnItemClickListener, OnTouchListener {

    private TitlePopWindow mWindow;
    private TitlePopAdapter.onItemClickListener mListener;

    private RelativeLayout rl_pop;


    public TitlePopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitlePopWindow(Activity activity, int width, int height, ArrayList<ActionItem> mActionItems) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.title_pop, null);
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        setHeight(height);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(000000000);
        setBackgroundDrawable(dw);

        ListView listView = (ListView) contentView.findViewById(R.id.lv_list);
        mWindow = this;

        TitlePopAdapter adapter = new TitlePopAdapter(mWindow, activity, mActionItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        rl_pop = (RelativeLayout) contentView.findViewById(R.id.rl_pop);
        rl_pop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                close();
            }
        });
    }


    /**
     * listview点击事件
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TitlePopWindow.this.dismiss();

    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void close() {
        this.dismiss();
    }

    public int position() {

        return 0;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public onItemClickListener getListener() {
        //可以通过this的实例来获取设置好的listener
        return mListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mWindow.dismiss();
        return false;
    }


}
