package com.fengda.app.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fengda.app.activity.MainActivity;
import com.fengda.app.common.ActivityManager;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG="CrashHandler";
    private static final CrashHandler sHandler = new CrashHandler();
    //系统默认的UncaughtException处理类   
    private static final UncaughtExceptionHandler sDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private Future<?> future;
    private CrashListener mListener;
    private File mLogFile;
    private Context mContext;
    


    public static CrashHandler getInstance() {
        return sHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(!handleException(ex) && sDefaultHandler != null){ 
             //如果用户没有处理则让系统默认的异常处理器来处理  
            sDefaultHandler.uncaughtException(thread, ex);              
        }else{       
            try{  
                Thread.sleep(2000);
            }catch (InterruptedException e){
                Log.e(TAG, "error : ", e);
            }   
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent restartIntent = PendingIntent.getActivity(
             		mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,  
//                     restartIntent); // 1秒钟后重启应用 
            ActivityManager.getInstance().exit();
            System.exit(0);
        }  
//        future = THREAD_POOL.submit(new Runnable() {
//            public void run() {
////            	Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();  
//                if (mListener != null) {
//                    mListener.afterSaveCrash(mLogFile);
//                }
//            };
//        });
//        if (!future.isDone()) {
//            try {
//                future.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        sDefaultHandler.uncaughtException(thread, ex);
    }
    
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(final Throwable ex) {
        if (ex == null) {  
            return false;  
        }  
        Log.e(TAG, ex.getMessage(),ex);
        //使用Toast来显示异常信息  
        new Thread(){
            @Override
            public void run() {  
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现未知异常,即将退出.",
                		Toast.LENGTH_LONG).show();
                Looper.loop();
            } 
        }.start();  
        CrashLogUtil.writeLog(mLogFile, "CrashHandler", ex.getMessage(), ex);
        return true;  
    }  

    public void init(Context context, File logFile, CrashListener listener) {
        mContext=context;
        mLogFile = logFile;
        mListener = listener;
        //设置该CrashHandler为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

}

