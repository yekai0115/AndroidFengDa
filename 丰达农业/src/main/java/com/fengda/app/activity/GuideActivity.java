package com.fengda.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fengda.app.R;
import com.fengda.app.adapter.GuideViewPagerAdapter;
import com.fengda.app.fragment.GuideFragment1;
import com.fengda.app.fragment.GuideFragment2;
import com.fengda.app.fragment.GuideFragment3;
import com.fengda.app.interfaces.PermissionListener;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 引导页
 */
@SuppressLint("Override") public class GuideActivity extends FragmentActivity implements OnPageChangeListener,PermissionListener,OnRequestPermissionsResultCallback{


	public ViewPager viewPage;
	private GuideFragment1 mFragment1;
	private GuideFragment2 mFragment2;
	private GuideFragment3 mFragment3;
	private PagerAdapter mPgAdapter;
	private LinearLayout group;//圆点指示器
	private List<Fragment> mListFragment = new ArrayList<Fragment>();
	private ImageView[] ivPoints;//小圆点图片的集合
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();


	}

	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	protected void onPause() {
		super.onPause();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView() {
		viewPage=(ViewPager)findViewById(R.id.guide_viewpager);
		group = (LinearLayout)findViewById(R.id.points);
		mFragment1 = new GuideFragment1();
		mFragment2 = new GuideFragment2();
		mFragment3 = new GuideFragment3();
		mListFragment.add(mFragment1);
		mListFragment.add(mFragment2);
		mListFragment.add(mFragment3);
		mPgAdapter = new GuideViewPagerAdapter(getSupportFragmentManager(),mListFragment);
		viewPage.setAdapter(mPgAdapter);
		int size=mListFragment.size();
		ivPoints = new ImageView[size];
		for(int i = 0; i < size; i++){
			//循坏加入点点图片组
			ivPoints[i] = new ImageView(this);
			if(i==0){
				ivPoints[i].setImageResource(R.drawable.round_style5);
			}else {
				ivPoints[i].setImageResource(R.drawable.round_style6);
			}
			ivPoints[i].setPadding(20, 8, 20, 8);
			group.addView(ivPoints[i]);
		}





        viewPage.addOnPageChangeListener(this);


		applyLoaPermisson();
		 if (Build.VERSION.SDK_INT >= 23 ){//6.0以上系统申请文件权限
			 checkPermisson();
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}


	@Override
	public void onPageSelected(int position) {
        for(int i=0 ; i < mListFragment.size(); i++){
            if(i == position){
                ivPoints[i].setImageResource(R.drawable.round_style5);
            }else {
                ivPoints[i].setImageResource(R.drawable.round_style6);
            }
        }
	}
	
	/**
	 * 申请定位权限
	 */
	private void applyLoaPermisson() {
		String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION };
		requestRuntimePermission(permissions, this,2);
	}

	private void checkPermisson() {
		String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
		requestRuntimePermission(permissions, this,1);
	}
	
	// andrpoid 6.0 及以上需要写运行时权限
	public void requestRuntimePermission(String[] permissions,PermissionListener listener,int type) {

		List<String> permissionList = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
				permissionList.add(permission);
			}
		}
		if (!permissionList.isEmpty()) {//如果permissionList不为空，说明需要申请这些权限
			ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),type);
		} 
	}
	
	// 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    	
    	
    	 if (grantResults.length > 0) {
             List<String> deniedPermissions = new ArrayList<>();
             for (int i = 0; i < grantResults.length; i++) {
                 int grantResult = grantResults[i];
                 String permission = permissions[i];
                 if (grantResult != PackageManager.PERMISSION_GRANTED) {
                     deniedPermissions.add(permission);
                 }
             }
             if (deniedPermissions.isEmpty()) {
                 onGranted(requestCode);
             } else {
                onDenied(deniedPermissions);
             }
         }
    }
	
	//获得权限
	@Override
	public void onGranted(int type) {
		
	}


	@Override
	public void onDenied(List<String> deniedPermission) {
		
	}
}
