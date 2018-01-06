package com.fengda.app.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.fengda.app.R;


/**
 * 
 * @author yezi
 *2015-1-6 17:38:43
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorUtils {
	
	/**
	 * 通知动画
	 * @param view
	 * @param f  需要移动的距离
	 * @param lon  多少毫秒后执行第二个动画
	 * @param popupWindow 
	 */
	public static void setY(final View view,final float f,long lon, final PopupWindow popupWindow){
		//boolean isRunning=false;//控制动画是否运行中
		Log.e("viewHeight==", f+"");
		ObjectAnimator animator1= ObjectAnimator.ofFloat(view, "ko", -f,0f);//添加了addUpdateListener  方法 ko 随意取名
		animator1.setDuration(1000).start();
		animator1.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				float f=(Float) arg0.getAnimatedValue();
				view.setY(f);	//通过设置view的Y轴 实现移动 
			}
		});
		
		
		
		
		
		
//		new Handler().postDelayed(new Runnable() {	
//			@Override
//			public void run() {
//				if(popupWindow.isShowing()){
//					popupWindow.dismiss();
//				}
//				
////				ObjectAnimator animator1= ObjectAnimator.ofFloat(view, "ko", 0f,-f);
////				animator1.setDuration(1000);
////				animator1.start();
////				animator1.addUpdateListener(new AnimatorUpdateListener() {
////					@Override
////					public void onAnimationUpdate(ValueAnimator arg0) {
////						float f=(Float) arg0.getAnimatedValue();
////						view.setY(f);	
////					}
////				});
//			}
//		},lon );

	}
	
	
	public static  void translateY(View view){
		Log.e("", "translateY");
		 ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY", 30f);
		 ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "translationY", -30f);
		 AnimatorSet animSet = new AnimatorSet();//动画集合
		 animSet.play(anim1);
		 animSet.play(anim2).after(anim1);//2在1执行完后执行
		 animSet.setDuration(1000);
		 animSet.setInterpolator(new LinearInterpolator());//动画匀速执行
		 animSet.start();
	}
	
	
	
	/**
	 * 动画设置宽高
	 * @param v
	 */
	public static void setWidth(final View v){
		
		ObjectAnimator animator1 =ObjectAnimator.ofInt(v, "width", 0,270);
		animator1.setDuration(1000);
		animator1.setRepeatCount(3);
		animator1.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator animator2 =ObjectAnimator.ofInt(v, "height", 0,200);
		animator2.setDuration(1000);
		AnimatorSet animSet = new AnimatorSet();//动画集合
		animSet.play(animator1);
		animSet.play(animator2).after(animator1);//2在1执行完后执行
		animSet.start();
	}
	
	
	public  static void loadscaleDown(View v){
		PropertyValuesHolder propertyValues1=PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.9f);
		PropertyValuesHolder propertyValues2=PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.9f);	
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, propertyValues1,propertyValues2);  
		animator.setDuration(200);  
		animator.start();
	}
	
	public  static void loadscaleUp(View v){
		PropertyValuesHolder propertyValues3=PropertyValuesHolder.ofFloat("scaleX", 0.9f,1.0f);
		PropertyValuesHolder propertyValues4=PropertyValuesHolder.ofFloat("scaleY", 0.9f,1.0f);		
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v,propertyValues3,propertyValues4);  
		animator.setDuration(200);  
		animator.start();
	}
	
	
	/**
	 * XY轴同时缩放
	 * @param v
	 */
	public  static void loadscaleXY(View v){
		PropertyValuesHolder propertyValues1=PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.8f);
		PropertyValuesHolder propertyValues2=PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.8f);
		PropertyValuesHolder propertyValues3=PropertyValuesHolder.ofFloat("scaleX", 0.8f,1.0f);
		PropertyValuesHolder propertyValues4=PropertyValuesHolder.ofFloat("scaleY", 0.8f,1.0f);		
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, propertyValues1
				,propertyValues2,propertyValues3,propertyValues4);  
		animator.setDuration(900);  
		animator.start();
		animator.addListener(new AnimatorListener() {			
			@Override
			public void onAnimationStart(Animator arg0) {
				System.err.println("s==========onAnimationStart===========");
			}		
			@Override
			public void onAnimationRepeat(Animator arg0) {
				System.err.println("s==========onAnimationRepeat===========");
			}
			@Override
			public void onAnimationEnd(Animator arg0) {
				System.err.println("s==========onAnimationEnd===========");
			}	
			@Override
			public void onAnimationCancel(Animator arg0) {
				System.err.println("s==========onAnimationCancel===========");
			}
		});
	}
	
	/**
	 * Y坐标缩放动画
	 * @param view
	 */
	public static void scaleY(View view,Context context){
		  
		// 从XML中加载属性动画
		/*Animator scaleYanim1 = AnimatorInflater.loadAnimator(context, R.anim.scale_y_in);
		final Animator scaleYanim2 = AnimatorInflater.loadAnimator(context, R.anim.scale_y_out);
		view.setPivotX(0);
		view.setPivotY(0); 
		//view.invalidate();
		scaleYanim1.setTarget(view);
		scaleYanim2.setTarget(view);

			scaleYanim1.start();
			new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	scaleYanim2.start();
	            }
	        }, 2000);*/
		}
	
	/**
	 * 上侧进入
	 * @param v
	 * @param lon
	 */
	public static  void sideTop(final View v,long lon){
		Animator animator1=	ObjectAnimator.ofFloat(v, "translationY", -300, 0).setDuration(lon);
		Animator animator2=ObjectAnimator.ofFloat(v, "alpha", 0.1f, 1).setDuration(lon*3/2);
        
        AnimatorSet animSet = new AnimatorSet();//动画集合
        animSet.playTogether(animator1,animator2);//同时执行
        animSet.start();
	}
	
	/**
	 * 底部翻转
	 * @param v
	 * @param lon
	 */
	public static  void rotateBottom(final View v,long lon){
		Animator animator1=ObjectAnimator.ofFloat(v, "rotationX",90, 0).setDuration(lon);
		Animator animator2=ObjectAnimator.ofFloat(v, "translationY", 250, 0).setDuration(lon);
		Animator animator3=ObjectAnimator.ofFloat(v, "alpha", 0, 1).setDuration(lon*3/2); 
        
        AnimatorSet animSet = new AnimatorSet();//动画集合
        animSet.playTogether(animator1,animator2,animator3);//同时执行
        animSet.start();
	}
	
	/**
	 * 上下翻转
	 * @param v
	 * @param lon
	 */
	public static  void fromTop(final View v,long lon){
		Animator animator1=ObjectAnimator.ofFloat(v, "rotationX", 90,88,88,45,0).setDuration(lon);
		Animator animator2=ObjectAnimator.ofFloat(v, "alpha", 0,0.4f,0.8f, 1).setDuration(lon*3/2);
		Animator animator3=ObjectAnimator.ofFloat(v, "scaleX", 0,0.5f, 0.9f, 0.9f, 1).setDuration(lon);
		Animator animator4=ObjectAnimator.ofFloat(v,"scaleY",0,0.5f, 0.9f, 0.9f, 1).setDuration(lon);
        
        AnimatorSet animSet = new AnimatorSet();//动画集合
        animSet.playTogether(animator1,animator2,animator3,animator4);//同时执行
        animSet.start();
	}
	
	
	/**
	 * 抖动
	 * @param v
	 * @param lon
	 * @param RepeatCount 重复次数
	 */
	public static  void shake(final View v,long lon,int RepeatCount){
		ObjectAnimator objectAnimator=	ObjectAnimator.ofFloat(v, "translationX",
				0, .10f, -25, .26f, 25,.42f, -25, .58f, 25,.74f,-25,.90f,1,0);
		objectAnimator.setRepeatCount(RepeatCount);
		objectAnimator.setDuration(lon).start();  
	}
	
	
	/**
	 * 垂直翻转
	 * @param v
	 * @param lon
	 */
	public static  void rotationX(final View v,long lon,int flag){
		if(flag==0){
			ObjectAnimator.ofFloat(v, "rotationX", 0, 180).setDuration(lon).start();
		}else{
			ObjectAnimator.ofFloat(v, "rotationX", 180, 0).setDuration(lon).start();
		}
		
	}
	
	
	/**
	 * 水平翻转
	 * 
	 * @param v
	 */
	public static void rotationY(final View v){
		v.setPivotX(30);
		v.setPivotY(30);
		//rotationY=Y轴翻转
		ObjectAnimator animator=ObjectAnimator.ofFloat(v, "rotationY", 0f,360f).setDuration(1000);
		animator.setRepeatCount(2); 
		animator.start();
	}
	public static void rotation(final View v){
		v.setPivotX(v.getMeasuredWidth() /2f);//设置动画从 该视图的中心开始
		 v.setPivotY(v.getMeasuredWidth() /2f); 
		//rotationY=Y轴翻转
		final ObjectAnimator animator=ObjectAnimator.ofFloat(v, "rotation", 0,360).setDuration(1000);
		animator.setRepeatCount(-1);//无限循环
		animator.setInterpolator(new LinearInterpolator());//匀速
		animator.start();
	}
	
	/**
	 * 旋转缩放
	 * @param v
	 * @param lonfv
	 */
	public static  void rotationScaleXY(final View v,long lon){
		ObjectAnimator animator1=ObjectAnimator.ofFloat(v, "rotation", 1080,720,360,0).setDuration(lon);
		//Animator animator1=ObjectAnimator.ofFloat(v, "rotation",360,0).setDuration(lon);
		ObjectAnimator animator2=ObjectAnimator.ofFloat(v, "alpha", 0, 1).setDuration(lon*3/2);
		ObjectAnimator animator3=ObjectAnimator.ofFloat(v, "scaleX", 0.1f, 0.5f, 1).setDuration(lon);
		ObjectAnimator animator4=ObjectAnimator.ofFloat(v,"scaleY",0.1f,0.5f,1).setDuration(lon);
         
        AnimatorSet animSet = new AnimatorSet();//动画集合
        animSet.playTogether(animator1,animator2,animator3,animator4);//同时执行
        animSet.start();
	}
	
	
	
	public static  void test1(final View v){
		ObjectAnimator animator= ObjectAnimator.ofFloat(v, "tfsls", 1f,0.6f).setDuration(2000);
		animator.setRepeatCount(3);
		animator.start();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				 float f=(Float) animator.getAnimatedValue();
				 v.setPivotX(v.getMeasuredWidth() /2f);//设置动画从 该视图的中心开始
				 v.setPivotY(v.getMeasuredWidth() /2f); 
				// v.setTranslationX(f);
				// v.setTranslationY(f);
				 v.setScaleX(f);
				 v.setScaleY(f);
				 v.setAlpha(f);
				 v.setRotation(f); 
			}
		});
	}
	
	
	 /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }
 
    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }
	
	
}
