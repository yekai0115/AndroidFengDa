package com.fengda.app.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不可滑动的viewpager
 * @author Administrator
 *
 */
public class ControlScrollViewPager extends ViewPager {  
	  
	    private boolean scrollable = false;  
	  
	    public ControlScrollViewPager(Context context) {  
	       super(context);  
	   }  
	  
	    public ControlScrollViewPager(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	    }  
	  
	    public void setScrollable(boolean enable) {  
	        scrollable = enable;  
	    }  
	  
	    @Override  
	    public boolean onInterceptTouchEvent(MotionEvent event) {  
	        if (scrollable) {  
	            return super.onInterceptTouchEvent(event);  
	       } else {  
	            return false;  
	        }  
	    }  
	    
	    @Override
	    public void setCurrentItem(int item) {
	    	super.setCurrentItem(item,false);//切换时不经过中间页
	    }
	    
	}  

