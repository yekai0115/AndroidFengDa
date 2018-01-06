package com.fengda.app.interfaces;

import java.util.List;

public interface PermissionListener {
	/**
	 * 成功获取权限
	 */
	void onGranted(int type);

	/**
	 * 为获取权限
	 * 
	 * @param deniedPermission
	 */
	void onDenied(List<String> deniedPermission);

}
