package com.fengda.app.interfaces;

import android.view.View;

/**
 * listview点击事件接口
 *
 */
public interface ExpandListItemClickHelp {
	void onCheckClick(View item, View widget, int groupPosition, int position, int which);
	void updateData();
	void onClick(String id);
}
