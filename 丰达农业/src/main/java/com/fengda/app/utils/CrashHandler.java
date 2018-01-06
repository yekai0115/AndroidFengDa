package com.fengda.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	/** 系统默认的UncaughtException处理类 */
	private UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	/** 用来存储设备信息和异常信息 */
	private Map<String, String> infos = new HashMap<String, String>();

	private String path;
//	private String url =CoolParkConstant.WEB_SERVICE_ERROE_FILE;

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	
	
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			 // 退出程序
//			SysApplication.exit();
//			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
			

		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// SysApplication.exit
//		Intent intent = new Intent(mContext, CatchErrorActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		mContext.startActivity(intent);
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();// 异常信息
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			// 获取内置内存卡  
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String phone;

						phone="13000000000";

					String time = formatter.format(new Date());
					String fileName = "Error-" + "[" + phone + "]" + ".log";
					path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;   //   /storage/emulated/0/Error-[18813904075].log

					File oldfile = new File(path);
					if(oldfile.isFile()){
						oldfile.delete();
					}
					File file = new File(path);
					if (!file.exists() && file.canWrite()) {
						file.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(time.getBytes());
					fos.write(sb.toString().getBytes());
					fos.flush();
					fos.close();
					long size = file.length();
//					RequestParams params = new RequestParams();
//					params.addBodyParameter("phone", phone);
//					params.addBodyParameter("version", "android");
//					params.addBodyParameter("errorsFile", file);
////					if(ConnectionUtil.isWifiConnected(mContext)){
////						upLoad(url, params);
////						// new SendErrorMailThread(new String(sb), fileName).start();// 后台发送捕获到的异常信息邮件
////					}	
					
					return fileName;
				}
			
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}



	// 发送错误信息线程
	class SendErrorMailThread extends Thread {

		String errorInfo, titlt;

		public SendErrorMailThread(String errorInfo, String titlt) {
			this.errorInfo = errorInfo;
			this.titlt = titlt;
		}

		@Override
		public void run() {
			super.run();
			sendErrorInfoToEmail();

		}

		// 后台发送错误信息到QQ邮件，实时了解程序出现什么异常，前提是邮箱要开通POP3/SMTP服务
		private void sendErrorInfoToEmail() {
//			try {
//				Calendar calendar = Calendar.getInstance();
//				Properties p = new Properties();
//				p.put("mail.smtp.host", "smtp.qq.com");// qq发件服务器地址
//				p.put("mail.smtp.port", "25");// 非SSL协议端口号
//				p.put("mail.smtp.auth", "true");
//				MyAuthenticator authenticator = new MyAuthenticator(
//						"2264322075@qq.com", "ssjy123456");
//				Session session = Session.getDefaultInstance(p, authenticator);
//				Message mailMessage = new MimeMessage(session);
//				mailMessage.setFrom(new InternetAddress("2264322075@qq.com"));
//				mailMessage.setRecipient(Message.RecipientType.TO,
//						new InternetAddress("2264322075@qq.com"));
//				mailMessage.setSubject("盛世基业停车场问题反馈 -" + titlt);
//				mailMessage.setSentDate(new Date());
//				mailMessage.setText(errorInfo + "\n发送时间："
//						+ calendar.get(Calendar.YEAR) + "年 "
//						+ (calendar.get(Calendar.MONTH) + 1) + "月"
//						+ calendar.get(Calendar.DAY_OF_MONTH) + "日\t"
//						+ calendar.get(Calendar.HOUR_OF_DAY) + "时"
//						+ calendar.get(Calendar.MINUTE) + "分"
//						+ calendar.get(Calendar.SECOND) + "秒");
//				Transport.send(mailMessage);
//				System.out.println("----Send_BugInfo_Email_Success----");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

	}

	/* 上传文件至Server，uploadUrl：接收文件的处理页面 */

	/* 上传文件至Server，uploadUrl：接收文件的处理页面 */

	private static void uploadFile(final String upUrl, final String path) {
		final String end = "\r\n";
		final String twoHyphens = "--";
		final String boundary = "*****";
		new Thread() {
			public void run() {
				try {
					String result;
					File file = new File(path);
					if (!file.exists() && file.canWrite()) {
						file.createNewFile();
					}

					// 构造param参数部分的数据内容，格式都是相同的，依次添加param1和param2
					StringBuilder sb = new StringBuilder();
					sb.append("--" + boundary + end);
					sb.append("Content-Disposition: form-data; name=\"phone\""
							+ end);
					sb.append("\r\n");
					sb.append("18813904075" + end);

					sb.append(twoHyphens + boundary + end);
					sb.append("Content-Disposition: form-data; name=\"version\""
							+ end);
					sb.append("\r\n");
					sb.append("android" + end);

					// 构造要上传文件的前段参数内容，和普通参数一样，在这些设置后就可以紧跟文件内容了。
					sb.append(twoHyphens + boundary + end);
					sb.append("Content-Disposition: form-data; name=\"errorsFile\"; filename=\""
							+ file.getName() + "\"" + end);
					sb.append("Content-Type: text/plain" + end);
					sb.append(end);

					byte[] before = sb.toString().getBytes("UTF-8");
					byte[] after = (twoHyphens + boundary + "--\r\n")
							.getBytes("UTF-8");

					URL url = new URL(upUrl);
					// 得到HttpURLConnection对象，设置一些头信息基本属性
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=" + boundary);
					conn.setRequestProperty(
							"Content-Length",
							String.valueOf(before.length + file.length()
									+ after.length));
					conn.setRequestProperty("HOST", url.getHost());
					conn.setDoOutput(true);
					OutputStream out = conn.getOutputStream();
					InputStream in = new FileInputStream(file);
					// 写入参数信息
					out.write(before);
					// 写入文件数据
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					// 写结束符，代表该HTTP组包完毕
					out.write(after);
					// 发送出去
					out.flush();
					// 关闭流
					in.close();
					out.close();
					/* 取得Response内容 */
					InputStream is = conn.getInputStream();
					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					// 从返回值中取出值
					result = new String(b.toString().getBytes("ISO-8859-1"),"UTF-8");
				} catch (Exception e) {
				}

			};
		}.start();

	}
}