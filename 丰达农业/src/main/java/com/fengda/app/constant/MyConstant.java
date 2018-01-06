package com.fengda.app.constant;

import android.os.Environment;

/**
 * 常量
 */

public class MyConstant {
//    public final static String WEB_SERVICE_BASE="https://app.ddzyigou.com/index.php/";
        public final static String WEB_SERVICE_BASE="http://120.77.128.6:9001/";
    public final static int SUCCESS=1000;
    public final static int FAILED=1001;
    public final static int ERR_AUTH=1003;
  public final static int PAY_NO_PERSION=1011;
  public final static int PAY_PWD_ERROE=1004;
    public final static int NO_PAY_PWD=1015;
    public static final int T_ERR_AUTH = 1006;//登录过期
    public static final int T_LOGIN_ERR = 1080;//登录异常
  //  public static final int SERVER_ERROE = 403;//登录异常
    public static final int SERVER_ERROE = 500;//服务器异常
    public static  String  PIC_DPI2="2.png";
    public static  String  PIC_DPI1="1.png";
    public final static int EXCHANGE_CASH=1;
    public final static int EXCHANGE_TRANSFEN=2;
    public final static int EXCHANGE_VOTING=3;
    public final static int EXCHANGE_JIYOUMEI=4;
    /**关于*/
    public static final String COM_ABOUT = "http://120.77.128.6:9003/public/about";

    public static final String COM_SERVICE = "http://120.77.128.6:9003/public/protocol";

  public static final String COM_RULE= " http://120.77.128.6:9003/public/activityRules";

    public static final String IMG_PATH = Environment.getExternalStorageDirectory().getPath().toString();
    public static final int DEF_IMG_W = 800;
    public static final int DEF_IMG_H = 640;

    /**阿里云EndPoint*/
    public static final String ALI_ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";
    public static final String ALI_KEYID = "LTAIvHGYyxQXtl2K";
    public static final String ALI_KEYSECRET = "XfEFIm4oFBeFtXvitvWrRk7HcoTIpV";

    /**上传下载的bucketName*/
    public static final String ALI_BUCKET_RECOMMEND = "fd-private-personal"; //

    public static final String ALI_PUBLIC_BUCKET_RECOMMEND = "fd-public-personal"; //


    /**阿里图片前缀*/
    public static final String ALI_PUBLIC_URL = "http://fd-public-personal.oss-cn-shenzhen.aliyuncs.com/";
    /**图片前缀*/
    public static final String BANNER_PUBLIC_URL = " http://www2-2.ddzyigou.com/views/public/banner/";



    public static  Boolean HASLOGIN=false;
    public static final String SUC_RESULT="0";
    public final static String VISTER="ddz";

    public static final String AUTH_USER="authUser";
    public static final String CLOSE_ON_KEYDOWN="close_on_keydown";
    public static final String COM_PHONE_NUM = "0755-23071963";

    /**
     * 本应用的文件
     */
    public final static String APP_HOME_PATH = Environment.getExternalStorageDirectory() + "/fd/";

    public static final String SHARE_IMAGE_PATH = APP_HOME_PATH + "app_icon.jpg";



}
