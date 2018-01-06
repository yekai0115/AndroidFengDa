package com.fengda.app.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 创建文件夹
 * 
 * @author lenovo
 * 
 */
public class CreateFile {

	Context context;

	public CreateFile(Context context) {
		this.context = context;
	}

	public static final String FirstFolder = "fengda";// 一级目录
	public static final String SecondFolder = "fengda";// 二级目录
	public static final String ThirdFolder = "image";// 三级目录
	public static final String QuartusFolder = "image";// 三级目录
	public static final String CrashFolder="crash";  //保存程序崩溃日志
	public static final String FeedbackFolder="feedback";
	public static final String ReturnFolder="return";
	public static final String ConpressFolder="conpress";
	public static final String QxcodeFolder="qxcode";  //二维码图片

	public final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ FirstFolder
			+ File.separator;

	public static final String Second_PATH = ALBUM_PATH + SecondFolder
			+ File.separator;

	public static final String Third_PATH = Second_PATH + ThirdFolder
			+ File.separator;

	public static final String Quartus_PATH = Third_PATH + QuartusFolder
			+ File.separator;
	
	public static final String CRASH_PATH=Second_PATH+CrashFolder+ File.separator;
	
	public static final String FEEDBACK_PATH=Third_PATH+FeedbackFolder+ File.separator;

	public static final String RETURN_PATH=Third_PATH+ReturnFolder+ File.separator;

	public static final String QXCODE_PATH=Third_PATH+QxcodeFolder+ File.separator;

	public static final String FEEDBACK_CONPRESS_PATH=Third_PATH+ConpressFolder+ File.separator;
	public void Create() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {// 如果不存在SD卡，进行提示
//			Toast.makeText(context, "请插入外部SD存储卡", Toast.LENGTH_SHORT).show();
		} else {// 如果存在SD卡，判断文件夹目录是否存在
			File dirFirstFile = new File(ALBUM_PATH);// 新建一级主目录
			if (!dirFirstFile.exists()) {// 判断文件夹目录是否存在
				dirFirstFile.mkdir();// 如果不存在则创建
			}

			File dirSecondFile = new File(Second_PATH);// 新建二级主目录
			if (!dirSecondFile.exists()) {// 判断文件夹目录是否存在
				dirSecondFile.mkdir();// 如果不存在则创建
			}

			File dirThirdFile = new File(Third_PATH);// 新建三级主目录
			if (!dirThirdFile.exists()) {// 判断文件夹目录是否存在
				dirThirdFile.mkdir();// 如果不存在则创建
			}

			File dirQuartusFile = new File(Quartus_PATH);// 新建三级主目录
			if (!dirQuartusFile.exists()) {// 判断文件夹目录是否存在
				dirQuartusFile.mkdir();// 如果不存在则创建
			}
			File dirCrashFile=new File(CRASH_PATH);
			if(!dirCrashFile.exists()){
				dirCrashFile.mkdir();
			}
			File conpressFile=new File(ConpressFolder);
			if(!conpressFile.exists()){
				conpressFile.mkdir();
			}
			File feedbackFile=new File(FEEDBACK_PATH);
			if(!feedbackFile.exists()){
				feedbackFile.mkdir();
			}
			File returnFile=new File(RETURN_PATH);
			if(!returnFile.exists()){
				returnFile.mkdir();
			}
			File qxCodeFile=new File(QXCODE_PATH);
			if(!qxCodeFile.exists()){
				qxCodeFile.mkdir();
			}
		}
	}
}
