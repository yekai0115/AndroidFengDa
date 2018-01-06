package com.fengda.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @fileName HandyTextView.java
 * @package com.sunray.yunlong.view
 * @description 自定义普通文本控件
 * @author loro
 * @version 1.0
 * 
 */
public class HandyTextView extends TextView {
	public HandyTextView(Context context) {
		super(context);
	}

	public HandyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HandyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (text == null) {
			text = "";
		}
		super.setText(text, type);
	}
}
