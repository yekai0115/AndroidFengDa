package com.fengda.app.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionUtils {
	private static final String TAG = PermissionUtils.class.getSimpleName();  
	  
    /** 
     * check this permission is need to request 
     * 
     * @param context         context 
     * @param permissionArray permissionArray 
     * @return 
     */  
    public static String[] getNeededPermission(Context context, String[] permissionArray) {  
        if (context == null || permissionArray == null || permissionArray.length == 0) {  
            return new String[]{};  
        }  
  
        List<String> permissionList = new ArrayList<>();  
        for (int i = 0; i < permissionArray.length; i++) {  
            if (PermissionUtils.isNeedAddPermission(context, permissionArray[i])) {  
                permissionList.add(permissionArray[i]);  
            }  
        }  
        return permissionList.toArray(new String[permissionList.size()]);  
    }  
  
    /** 
     * check permission is need ? 
     * @return true: 权限未开启，需要这个权限；  false: 权限已开启
     */  
    public static boolean isNeedAddPermission(Context context, String permission) {  
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;  
    }  
  
    /**
     * @param context
     * @param permission false:api在6.0以下或者API在6.0以上并且权限已开启
     * 					 true:API在6.0以上并且权限未开启
     * @return
     */
    public static boolean isNeedCheckPermisson(Context context, String permission){
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {//6.0以上
    		return isNeedAddPermission(context, permission);
    	}else{
    		return false;	
    	}
    }
    
    
    
    
    /** 
     * check permission and show setting dialog for user 
     * 
     * @param getPermissionListener getPermissionListener 
     * @param context               context 
     * @param permissions           permissions 
     * @param grantResults          grantResults 
     */  
    public static void checkPermissionResult(OnHasGetPermissionListener getPermissionListener, Context context  
            , String[] permissions, int[] grantResults) {  
        if (context == null || permissions == null || grantResults == null || getPermissionListener == null) {  
            Log.d(TAG, "context=" + context + "\n"
                    + "permissions=" + Arrays.toString(permissions) + "\n"  
                    + "grantResults=" + Arrays.toString(grantResults));  
            return;  
        }  
  
        Log.d(TAG, "permissions=" + Arrays.toString(permissions) + ",grantResults=" + Arrays.toString(grantResults));  
        // save the request permission  
        List<PermissionInfo> list = new ArrayList<>();//Your  permission list  
  
        if (grantResults.length <= 0) {  
            return;  
        }  
  
        // check permission request result  
        for (int i = 0; i < permissions.length; i++) {  
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {  
                PermissionInfo info = null;  
                try {  
                    info = context.getPackageManager().getPermissionInfo(permissions[i], 0);  
                } catch (PackageManager.NameNotFoundException e) {  
                    e.printStackTrace();  
                }  
                if (info != null) {  
                    list.add(info);  
                }  
            }  
        }  
  
        // is we has all permission  
        if (list.size() == 0) {  
            Log.d(TAG, "checkPermissionResult onSuccess");  
            getPermissionListener.onSuccess();  
        } else {  
            getPermissionListener.onFail();  
            // show the dialog for user to setting  
            Log.d(TAG, "checkPermissionResult onFail");  
        }  
    }  
  
    public interface OnHasGetPermissionListener {  
        void onSuccess();  
  
        void onFail();  
    }  
}
