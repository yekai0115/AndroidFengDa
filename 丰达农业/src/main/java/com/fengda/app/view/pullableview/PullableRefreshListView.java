package com.fengda.app.view.pullableview;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.fengda.app.view.pulltorefresh.Pullable;


/**
 * 只可下拉
 * 
 * @author Administrator
 * 
 */
public class PullableRefreshListView extends ListView implements Pullable {

	public PullableRefreshListView(Context context) {
		super(context);
	}

	public PullableRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 判断是否可以下拉
	 */
	@Override
	public boolean canPullDown() {
		try {
			if (getCount() == 0) {
				// 没有item的时候也可以下拉刷新
				return true;
			} else if (getFirstVisiblePosition() == 0
					&& getChildAt(0).getTop() >= 0) {
				// 滑到ListView的顶部了
				return true;
			} else
				return false;
		} catch (Exception e) {
			
		}
		return false;
	}

	/**
	 * 判断是否可以上拉
	 */
	@Override
	public boolean canPullUp() {
		// if (getCount() == 0)
		// {
		// // 没有item的时候也可以上拉加载
		// return true;
		// } else if (getLastVisiblePosition() == (getCount() - 1))
		// {
		// // 滑到底部了
		// if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition())
		// != null
		// && getChildAt(
		// getLastVisiblePosition()
		// - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
		// return true;
		// }
		return false;
	}
}
