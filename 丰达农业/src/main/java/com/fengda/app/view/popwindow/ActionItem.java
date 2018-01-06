package com.fengda.app.view.popwindow;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @author  功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class ActionItem {
	// 定义图片对象
	private Drawable mDrawable;
	// 定义文本对象
	private String mTitle;
	private boolean check;
	private int status;
	public ActionItem(Drawable drawable, String title) {
		this.mDrawable = drawable;
		this.mTitle = title;
	}

	public ActionItem(String title,boolean check,int status) {
		this.mTitle = title;
		this.check = check;
		this.status = status;
	}


	public ActionItem(Context context, int titleId, int drawableId) {
		this.mTitle = (String) context.getResources().getText(titleId);
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}

	public ActionItem(Context context, String title, int drawableId) {
		this.mTitle = title;
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isCheck() {
		return check;
	}

	public Drawable getmDrawable() {
		return mDrawable;
	}

	public void setmDrawable(Drawable mDrawable) {
		this.mDrawable = mDrawable;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
