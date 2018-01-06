package com.fengda.app.view.slide;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.fengda.app.R;


/**
 * This attacher class is used to attach the sliding mechanism to any {@link Activity}
 * that lets the user slide (or swipe) the activity away as a form of back or up action. The action
 * causes {@link Activity#finish()} to be called.
 *
 *
 * Created by r0adkll on 8/18/14.
 */
public class Slidr {

    /**
     *
     * @param activity
     * @return
     */
    public static SlidrInterface attach(Activity activity){
        return attach(activity, -1, -1);
    }


    public static SlidrInterface attach(final Activity activity, final int statusBarColor1, final int statusBarColor2){

        // Hijack the decorview
        ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();
        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        final SliderPanel panel = new SliderPanel(activity, oldScreen);
        panel.setId(R.id.slidable_panel);
        oldScreen.setId(R.id.slidable_content);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new SliderPanel.OnPanelSlideListener() {

            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onClosed() {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {}

         
            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                        statusBarColor1 != -1 && statusBarColor2 != -1){
                    int newColor = (Integer) mEvaluator.evaluate(percent, statusBarColor1, statusBarColor2);
              //      activity.getWindow().setStatusBarColor(newColor);
                }
            }
        });

        // Setup the lock interface
        SlidrInterface slidrInterface = new SlidrInterface() {
            @Override
            public void lock() {
                panel.lock();
            }

            @Override
            public void unlock() {
                panel.unlock();
            }
        };

        // Return the lock interface
        return slidrInterface;
    }


    public static SlidrInterface attach(final Activity activity, final SlidrConfig config){

        // Hijack the decorview
        ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();
        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        final SliderPanel panel = new SliderPanel(activity, oldScreen, config);
        panel.setId(R.id.slidable_panel);
        oldScreen.setId(R.id.slidable_content);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new SliderPanel.OnPanelSlideListener() {

            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {
                if(config.getListener() != null){
                    config.getListener().onSlideStateChanged(state);
                }
            }

            @Override
            public void onClosed() {
                if(config.getListener() != null){
                    config.getListener().onSlideClosed();
                }

                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {
                if(config.getListener() != null){
                    config.getListener().onSlideOpened();
                }
            }

           
            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                // TODO: Add support for KitKat
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                        config.areStatusBarColorsValid()){

                    int newColor = (Integer) mEvaluator.evaluate(percent, config.getPrimaryColor(),config.getSecondaryColor());

               //     activity.getWindow().setStatusBarColor(newColor);
                }

                if(config.getListener() != null){
                    config.getListener().onSlideChange(percent);
                }
            }
        });

        // Setup the lock interface
        SlidrInterface slidrInterface = new SlidrInterface() {
            @Override
            public void lock() {
                panel.lock();
            }

            @Override
            public void unlock() {
                panel.unlock();
            }
        };

        // Return the lock interface
        return slidrInterface;

    }

}
