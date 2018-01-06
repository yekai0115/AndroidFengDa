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


public class TopNvgBar2 extends RelativeLayout {
    /**标题*/
    private  TextView titleView;

  //  private  TextView rtextView;
    public TopNvgBar2(Context context) {
        this(context, null);
    }

    public TopNvgBar2(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopNvgBar1, 0, 0);
        String title = typedArray.getString(R.styleable.TopNvgBar1_title);
        String right = typedArray.getString(R.styleable.TopNvgBar1_right);
        Boolean lvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_lvisible, true);
        Boolean rvisible = typedArray.getBoolean(R.styleable.TopNvgBar1_rvisible, false);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_top_nvg_bar2, this, true);

        titleView = (TextView) rootView.findViewById(R.id.vtnb_title);
        ImageView lRelativeLayout = (ImageView) rootView.findViewById(R.id.img_return);
    //    rtextView = (TextView) rootView.findViewById(R.id.vtnb_right);



        if(!lvisible)lRelativeLayout.setVisibility(GONE);
//        if(!rvisible)rtextView.setVisibility(GONE);
//        rtextView.setText(right);
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
                    case R.id.vtnb_right:
                        if(onMyClickListener != null) {
                            onMyClickListener.onRightClick();
                        }
                        break;
                }
            }
        };
        lRelativeLayout.setOnClickListener(onClickListener);
  //      rtextView.setOnClickListener(onClickListener);
    }
    public void setTitle(String title) {
        titleView.setText(title);
    }
    private MyOnClickListener onMyClickListener;

    public void setMyOnClickListener(MyOnClickListener listener) {
        this.onMyClickListener = listener;
    }

    public interface MyOnClickListener {
        void onLeftClick();
        void onRightClick();
    }

    public void setRightVisibility(int visibility){
     //   rtextView.setVisibility(visibility);
    }


}
