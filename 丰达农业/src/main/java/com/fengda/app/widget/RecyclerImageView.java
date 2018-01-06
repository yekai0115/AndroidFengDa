package com.fengda.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RecyclerImageView extends ImageView{

	
	public RecyclerImageView(Context context) {
		super(context);
	}
	
	public RecyclerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecyclerImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		setImageDrawable(null);//在它从屏幕中消失时回调，去掉drawable引用，能加快内存的回收
	}
}
