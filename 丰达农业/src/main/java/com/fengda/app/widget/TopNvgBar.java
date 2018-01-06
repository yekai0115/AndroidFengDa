package com.fengda.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;


/**
 * Created by StormShadow on 2017/3/8.
 * Knowledge is power.
 *
 * 左边是ImageView，右边没有控件
 */
public class TopNvgBar extends RelativeLayout {

    private RelativeLayout rootLayout;
    private View rootView;
    private TextView textView;

    public TopNvgBar(Context context) {
        this(context, null);
    }

    public TopNvgBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopNvgBar, 0, 0);
        String text = typedArray.getString(R.styleable.TopNvgBar_toptext);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.view_top_nvg_bar, this, true);
        rootLayout = (RelativeLayout) rootView.findViewById(R.id.vtnb_root);

        final RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.vtnb_back);
        textView = (TextView) rootView.findViewById(R.id.vtnb_title);
        textView.setText(text);

        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null) { // ignore v, use ImageButtonTextView itself instead
                    onClickListener.onClick(v);
                }
            }
        });
    }

    public void setTitle(String title) {
        textView.setText(title);
    }

    public String getTitle() {
        return textView.getText().toString();
    }

    public void setBgColor(int color) {
        rootLayout.setBackgroundColor(color);
        ((RelativeLayout) rootView.findViewById(R.id.vtnb_back)).setBackgroundColor(color);
    }

    private MyOnClickListener onClickListener;

    public void setMyOnClickListener(MyOnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface MyOnClickListener {
        void onClick(View view);
    }
}
