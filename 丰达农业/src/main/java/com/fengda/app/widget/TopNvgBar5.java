package com.fengda.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;

import static android.R.attr.visibility;


public class TopNvgBar5 extends RelativeLayout {
    /**标题*/
    private  TextView titleView;
    private RelativeLayout vtnb_back;
    private  TextView rtextView;
    public TopNvgBar5(Context context) {
        this(context, null);
    }

    public TopNvgBar5(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopNvgBar1, 0, 0);
        String title = typedArray.getString(R.styleable.TopNvgBar1_title);
        String right = typedArray.getString(R.styleable.TopNvgBar1_right);
        Boolean lvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_lvisible, true);
        Boolean rvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_rvisible, false);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_top_nvg_bar5, this, true);

        titleView = (TextView) rootView.findViewById(R.id.vtnb_title);
        RelativeLayout lRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.vtnb_back);
        rtextView = (TextView) rootView.findViewById(R.id.vtnb_right);
        vtnb_back= (RelativeLayout) rootView.findViewById(R.id.vtnb_back);


        if(!lvisible)lRelativeLayout.setVisibility(GONE);
        if(!rvisible)rtextView.setVisibility(GONE);
        rtextView.setText(right);
        titleView.setText(title);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.vtnb_back:
                        if(onMyClickListener != null) {
                            onMyClickListener.onLeftClick();
                        }
                        break;
                    case R.id.vtnb_right:
                        if(onMyClickListener != null) {
                            onMyClickListener.onRightClick();
                        }
                        break;
                }
            }
        };
        lRelativeLayout.setOnClickListener(onClickListener);
        rtextView.setOnClickListener(onClickListener);
    }
    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setRight(String right) {
        rtextView.setText(right);
    }
    private MyOnClickListener onMyClickListener;

    public void setMyOnClickListener(MyOnClickListener listener) {
        this.onMyClickListener = listener;
    }

    public interface MyOnClickListener {
        void onLeftClick();
        void onRightClick();
    }
    public void setRightClickable(boolean click){
        rtextView.setClickable(click);
    }
    public void setRightVisibility(int visibility){
        rtextView.setVisibility(visibility);
    }

    public void setLeftVisibility(int visibility){
        vtnb_back.setVisibility(visibility);
    }
}
