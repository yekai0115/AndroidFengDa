package com.fengda.app.view.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.fengda.app.view.pulltorefresh.Pullable;
import com.fengda.app.widget.MyScrollView;


public class PullableRefreshScrollView extends ScrollView implements Pullable
{
	/**是否需要上拉：默认不需要*/
	private Boolean needPullUp=false;
	public OnScrollChangeListener onScrollChangeListener;

	public View contentView;

	public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener)
	{
		this.onScrollChangeListener = onScrollChangeListener;
	}




	public PullableRefreshScrollView(Context context)
	{
		super(context);
	}

	public PullableRefreshScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableRefreshScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	/**
	 * 下拉
	 */
	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 上拉
	 */
	@Override
	public boolean canPullUp()
	{
//		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
//			return true;
//		else
		if(needPullUp){
			return true;
		}else{
			return false;
		}
			
	}
	public interface OnScrollChangeListener {
		void onScrollChange(PullableRefreshScrollView view, int x, int y, int oldx, int oldy);

	}

	/**
	 * l当前水平滚动的开始位置
	 * t当前的垂直滚动的开始位置
	 * oldl上一次水平滚动的位置。
	 * oldt上一次垂直滚动的位置。
	 **/
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollChangeListener != null)
		{
			onScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
		}
	}
}
