package com.fengda.app.view.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.fengda.app.view.pulltorefresh.Pullable;


/**
 * 可下拉可上拉
 * 
 * @author Administrator
 * 
 */
public class PullableExpandableListView extends ExpandableListView implements
		Pullable {

	/**是否需要上拉：默认需要*/
	public Boolean needPullUp=true;	
	
	
	public PullableExpandableListView(Context context) {
		super(context);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		try {
			if (getCount() == 0)
			{
				// 没有item的时候也可以下拉刷新
				return true;
			} else if (getFirstVisiblePosition() == 0&& getChildAt(0).getTop() >= 0)
			{
				// 滑到顶部了
				return true;
			} else{
				return false;
			}
				
		} catch (Exception e) {
			
		}
		return false;
		
	}

	/**
	 * 上拉
	 */
	@Override
	public boolean canPullUp() {
		try {
			if(needPullUp){
				if (getCount() == 0) {
					// 没有item的时候也可以上拉加载
					return true;
				} else if (getLastVisiblePosition() == (getCount() - 1)) {
					// 滑到底部了
					if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
							&& getChildAt(
									getLastVisiblePosition()
											- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
						return true;
				}
			}else{
				return false;
			}
			
		} catch (Exception e) {			
		}
		return false;
	}

}
