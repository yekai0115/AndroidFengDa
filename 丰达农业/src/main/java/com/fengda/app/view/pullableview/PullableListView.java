package com.fengda.app.view.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.fengda.app.view.pulltorefresh.Pullable;


/**
 * 可上拉下拉
 *
 * @author Administrator
 */
public class PullableListView extends ListView implements Pullable {
    private boolean isNoList = false;
    public Boolean canPullUp = true;

    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 判断是否可以下拉
     */
    @Override
    public boolean canPullDown() {
//		if (getCount() == 0)
//		{
//			// 没有item的时候也可以下拉刷新
//			return true;
//		} else if (getFirstVisiblePosition() == 0&& getChildAt(0).getTop() >= 0)
//		{
//			// 滑到ListView的顶部了
//			return true;
//		} else{
//			return false;
//		}
        try {
            if (getCount() == 0) {
                // 没有item的时候也可以下拉刷新
                return true;
            } else if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0) {
                // 滑到ListView的顶部了
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;


    }

    /**
     * 判断是否可以上拉
     */
    @Override
    public boolean canPullUp() {
        try {
            if (canPullUp) {
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
                    if (isNoList) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public void setPullUp(boolean nolist) {
        isNoList = nolist;
    }
}
