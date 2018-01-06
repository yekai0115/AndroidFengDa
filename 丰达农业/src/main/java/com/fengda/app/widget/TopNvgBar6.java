package com.fengda.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;


/**
 * Created by StormShadow on 2017/3/8.
 * Knowledge is power.
 *
 * 两边都是ImageView
 */
public class TopNvgBar6 extends RelativeLayout {

    private TextView titleView;
    private ImageView img_right;
    public TopNvgBar6(Context context) {
        this(context, null);
    }

    public TopNvgBar6(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopNvgBar1, 0, 0);
        String title = typedArray.getString(R.styleable.TopNvgBar1_title);
        Boolean lvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_lvisible, true);
        Boolean rvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_rvisible, false);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_top_nvg_bar6, this, true);

         titleView = (TextView) rootView.findViewById(R.id.vtnb_title);
        ImageView img_right=(ImageView) rootView.findViewById(R.id.img_right);
        ImageView img_return = (ImageView) rootView.findViewById(R.id.img_return);


        if(!lvisible)img_return.setVisibility(GONE);
        if(!rvisible)img_right.setVisibility(GONE);
        titleView.setText(title);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.img_return:
                        if(onMyClickListener != null) {
                            onMyClickListener.onLeftClick();
                        }
                        break;
                    case R.id.img_right:
                        if(onMyClickListener != null) {
                            onMyClickListener.onRightClick();
                        }
                        break;
                }
            }
        };
        img_return.setOnClickListener(onClickListener);
        img_right.setOnClickListener(onClickListener);
    }

    private MyOnClickListener onMyClickListener;

    public void setMyOnClickListener(MyOnClickListener listener) {
        this.onMyClickListener = listener;
    }

    public interface MyOnClickListener {
        void onLeftClick();
        void onRightClick();
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setVisibility(){
        img_right.setVisibility(View.GONE);
    }

}
