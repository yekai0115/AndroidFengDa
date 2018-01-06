package com.fengda.app.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CommonUtils {

	private static final String TAG = "CommonUtils";
	public static String OFFLINE = "Offline";
	public static String ONLINE = "Online";
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfYearWithoutSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfMonth = new SimpleDateFormat("MM-dd HH:mm");
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy年MM月dd日");
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfDatePoint = new SimpleDateFormat("yyyy-MM-dd");
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdfDateWithoutYear = new SimpleDateFormat("MM月dd日");

	// 判断GPS是否打开
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return gps;
	}

	/**
	 * 传入字符型时间数据，返回秒
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long getLongFromString(String date) throws ParseException {
		if (null == date)
			return 0;
		long time = sdfYear.parse(date).getTime();
		return time / 1000;
	}

	/**
	 * 传入字符型时间数据，返回秒
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long getLongFromStringWithOutHour(String date) throws ParseException {
		if (null == date)
			return 0;
		long time = sdfDatePoint.parse(date).getTime();
		return time / 1000;
	}

	/**
	 * 传入字符型时间数据，返回秒
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long getLongFromStringWithOutSecond(String date) throws ParseException {
		if (null == date)
			return 0;
		long time = sdfYearWithoutSecond.parse(date).getTime();
		return time / 1000;
	}

	/**
	 * 传入秒，返回字符型时间数据（格式为MM-dd HH:mm）
	 *
	 * @param time
	 * @return
	 */
	public static String getStringFromLongMonth(long time) {
		Date date = new Date(time * 1000);
		return sdfMonth.format(date);
	}

	public static String getStringFromLongDate(long time) {
		Date date = new Date(time * 1000);
		return sdfDate.format(date);
	}

	//获取当月最后一天
	public static Date lastDayOfMonth(int month, int year) {
		Calendar cal_2 = Calendar.getInstance();//
		cal_2.clear();
		cal_2.set(year, month - 1, 1, 23, 59, 59);
		cal_2.set(Calendar.DAY_OF_MONTH, cal_2.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal_2.getTime();
	}

	public static String getStringFromLongDateWithoutYear(long time) {
		Date date = new Date(time * 1000);
		return sdfDateWithoutYear.format(date);
	}

	public static String getStringFromLongDatePoint(long time) {
		Date date = new Date(time * 1000);
		return sdfDatePoint.format(date);
	}

	public static String getStringFromLongYearWithOutSecond(long time) {
		Date date = new Date(time * 1000);
		return sdfYearWithoutSecond.format(date);
	}

	public static String getStringFromLongYearWithOutSecond2(long time) {
		Date date = new Date(time);
		return sdfYearWithoutSecond.format(date);
	}

	public static String getCurrentDateYY_MM_DD() {
		return ymdFormat.format(new Date());
	}

	public static String getCurrentDataYYMMDD_HHMM() {
		return sdfMonth.format(new Date());
	}

	/**
	 * 传入毫秒，返回字符型时间数据（格式为yyyy-MM-dd HH:mm:ss）
	 *
	 * @param time
	 * @return
	 */
	public static String getStringFromMSLongYear(long time) {
		Date date = new Date(time);
		return sdfYear.format(date);
	}

	/**
	 * 传入毫秒数，返回字符型时间间隔（格式为： HH:mm:ss）
	 *
	 * @param
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
	 * @author fy.zhang
	 */
	public static String formatDuring(long mss) {
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return hours + " : " + minutes + " : " + seconds;
	}

	/**
	 * 传入秒，返回字符型时间数据（格式为yyyy-MM-dd HH:mm:ss）
	 *
	 * @param time
	 * @return
	 */
	public static String getStringFromMSIntYear(int time) {
		return getStringFromMSLongYear(((long) time) * 1000);
	}

	/**
	 * 传入毫秒，返回字符型时间数据（格式为MM-dd HH:mm）
	 *
	 * @param time
	 * @return
	 */
	public static String getStringFromMSLongMonth(long time) {
		Date date = new Date(time);
		return sdfMonth.format(date);
	}

	/**
	 * 传入秒，返回字符型时间数据（格式为yyyy-MM-dd HH:mm:ss）
	 *
	 * @param time
	 * @return
	 */
	public static String getStringFromLongYear(long time) {
		Date date = new Date(time * 1000);
		return sdfYear.format(date);
	}

	/**
	 * 得到当前时间的秒
	 *
	 * @return
	 */
	public static long getNowDate() {
		String time = sdfYear.format(new Date());
		try {
			return getLongFromString(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获得当天24点时间
	public static int getTimesnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000);
	}

	/**
	 * 得到当前时间的字符型数据
	 *
	 * @return
	 */
	public static String getNowDateString() {
		String time = sdfYear.format(new Date());
		return time;
	}

	/**
	 * 返回两个时间的间隔，单位秒
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static int getDateInterval(String startDate, String endDate) {

		long interval = 15;
		try {
			long start = sdfYear.parse(startDate).getTime();
			long end = sdfYear.parse(endDate).getTime();
			interval = ((end - start) / 1000);
		} catch (ParseException e) {
			Log.e(CommonUtils.class.getName(), e.getMessage(), e);
		} catch (Exception e) {
			Log.e(CommonUtils.class.getName(), e.getMessage(), e);
		}
		return (int) interval;
	}

	// 类似的wifi是否打开
	public static boolean isWifiEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return network;
	}

	// 网络连接是否可用
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}

	/**
	 * 验证手机号是否正确
	 *
	 * @param text
	 * @return
	 */
	public static boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		return false;
	}

	// 打开GPS方法1
	public static void toggleGPS(Context context) {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/*
	 * 打开GPS需要 <uses-permission
	 * android:name="android.permission.WRITE_SECURE_SETTINGS"/>
	 * 且APP要安装在system/app下
	 */
	public static void openGPSSettings(Context context) {
		// 获取GPS现在的状态（打开或是关闭状态）
		boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
				LocationManager.GPS_PROVIDER);
		if (gpsEnabled) {
			// 关闭GPS
			// Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),
			// LocationManager.GPS_PROVIDER, false);
		} else {
			// 打开GPS
			Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER,
					true);
		}
	}

	/**
	 * 从view 得到图片
	 *
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo("com.sunray.yunlong", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo("com.sunray.yunlong", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;

	}

	/**
	 * 根据秒换算成天时分
	 */
	public static String getTime(long time) {
		time = time * 1000;
		StringBuilder date = new StringBuilder();
		long day = time / (24 * 60 * 60 * 1000);
		long hour = (time / (60 * 60 * 1000) - day * 24);
		long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		if (day > 0)
			date.append(day).append("天");
		if (hour > 0)
			date.append(hour).append("时");
		if (min > 0)
			date.append(min).append("分");
		if (s > 0)
			date.append(s).append("秒");
		return date.toString();
	}

	/**
	 * MD5 加密
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "NoSuchAlgorithmException caught!", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), "");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 5000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static Bitmap decodeFile(String pathName) {
		//为位图设置100K的缓存
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inTempStorage = new byte[100 * 1024];
		//设置位图颜色显示优化方式
		//ALPHA_8：每个像素占用1byte内存（8位）
		//ARGB_4444:每个像素占用2byte内存（16位）
		//ARGB_8888:每个像素占用4byte内存（32位）
		//RGB_565:每个像素占用2byte内存（16位）
		//Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		//设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
		opts.inPurgeable = true;
		//设置位图缩放比例
		//width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
//		opts.inSampleSize = 4;
		//设置解码位图的尺寸信息
		opts.inInputShareable = true;
		opts.inDither = false;

		FileInputStream is = null;
		Bitmap bmp = null;
		try {
			is = new FileInputStream(pathName);
//		    bmp =BitmapFactory.decodeStream(is,null, opts); 
			bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (null != is)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.getMessage(), e);
				}
		}
		return bmp;
	}

	/**
	 * 生成二维码Bitmap
	 *
	 * @param content   内容
	 * @param widthPix  图片宽度
	 * @param heightPix 图片高度
	 * @param logoBm    二维码中心的Logo图标（可以为null）
	 * @param filePath  用于存储二维码图片的文件路径
	 * @return 生成二维码及保存文件是否成功
	 */

	public static boolean createQRImage(String content, int widthPix, int heightPix, String logoBm, String filePath) {
		try {
			if (content == null || "".equals(content)) {
				return false;
			}
			//配置参数
			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
			int[] pixels = new int[widthPix * heightPix];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = 0xff000000;
					} else {
						pixels[y * widthPix + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
			if (logoBm != null) {
				Bitmap bitmap1 = ImgSetUtil.getAdjustCompressBitmapByPath(logoBm);
				bitmap = addLogo(bitmap, bitmap1);
			}
			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static int compareTo(String a, String b) {
		return new BigDecimal(a).compareTo(new BigDecimal(b));
	}
	/**
	 * 在二维码中间添加Logo图案
	 */
	public static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}
		if (logo == null) {
			return src;
		}
		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();
		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		//logo大小为二维码整体大小的1/5
//		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		float scaleFactor = 1;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, 100, 100);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}

	/**
	 * 保留两位小数
	 */
	public static double formatDouble1(double d) {
		return (double) Math.round(d*100)/100;
	}

	/**
	 * 保留两位小数
	 */
	public static String formatDouble2(double d) {
		if(d == 0) {
			return "0.00";
		}
		DecimalFormat df = new DecimalFormat("0.00");
//        if(d < 1) {
//            return 0 + df.format(d);
//        }
		return df.format(d);
	}
}
