package com.fengda.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.aliutil.PutObjectSamples;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.UserBean;
import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.MyOSSConfig;
import com.fengda.app.dialog.DialogConfirm;
import com.fengda.app.dialog.DialogSelPhoto;
import com.fengda.app.eventbus.MsgEvent2;
import com.fengda.app.eventbus.MsgEvent5;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.ImgSetUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.utils.luban.Luban;
import com.fengda.app.utils.luban.OnCompressListener;
import com.fengda.app.widget.GlideCircleTransform;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fengda.app.R.id.tv_text;

/**
 * Created by 24448 on 2017/10/20.
 */

public class SettingActivity extends BaseActivity implements PermissionListener, ActivityCompat.OnRequestPermissionsResultCallback {


    @ViewInject(R.id.tv_version)
    private TextView tvVersion;
    private Context mContext;
    private String token;


    @ViewInject(R.id.nick_name)
    private TextView nick_name;


    @ViewInject(R.id.img_login)
    private ImageView img_login;

    @ViewInject(R.id.pwd_settint)
    private TextView pwd_settint;



    private DialogSelPhoto dialogSelPhoto;
    private OSS oss;
    private String localTempImageFileName;
    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shezhi);
        x.view().inject(this);
        mContext = this;
        // 注册事件
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText("1.0.0");
            e.printStackTrace();
        }
        if (MyConstant.HASLOGIN) {
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            getUserinfo(1);
        }
    }

    @Override
    protected void initEvents() {
        dialogSelPhoto = new DialogSelPhoto();
        oss = new OSSClient(mContext, MyConstant.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());

        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if (isCameraSel) {
                    type = 1;
                    //拍照
                    takePhotoForCamera(imgIndex);
                } else {
                    //相册
                    type = 2;
                    takePhotoForAlbum(imgIndex);
                }
            }
        });
    }

    @Event({R.id.rl_set_pw, R.id.rl_set_pay_pw, R.id.rl_contact, R.id.ll_about, R.id.btn_tuichu,R.id.tv_myaddress,R.id.name_linear,R.id.logo_linear})
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_set_pw://修改登陆密码
                intent = new Intent(mContext, ForgetPwdActivity.class);
                intent.putExtra("title", "修改密码");
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.rl_set_pay_pw://修改、设置支付密码
                intent = new Intent(mContext, SetPayPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_myaddress://收货地址
                intent = new Intent(mContext, AddressMangeActivity.class);
                intent.putExtra("fromType", 1);
                startActivity(intent);
                break;
            case R.id.rl_contact://联系客服
                showDialDialog();
                break;
            case R.id.ll_about://关于
                intent = new Intent();
                intent.putExtra("site_url",MyConstant.COM_ABOUT);
                intent.putExtra("name","关于");
                intent.setClass(mContext, MyWebViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_tuichu://退出
                showExitDialog();
                break;
            case R.id.name_linear://修改昵称
                if (MyConstant.HASLOGIN) {
                    intent = new Intent(mContext, UpdateNickActivity.class);
                    intent.putExtra("nick", nick_name.getText().toString());
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.logo_linear://上传头像
                if (MyConstant.HASLOGIN) {
                    dialogSelPhoto.showDialog(SettingActivity.this, 1);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private void showExitDialog() {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked){
                    MyConstant.HASLOGIN = false;
                    SPUtils.clear(mContext);
                    SPUtils.put(mContext, "isFirstRun", false);
                    finish();//关闭设置页面
                    startActivity(LoginActivity.class);
                }

            }
        });
        alert.showDialog(SettingActivity.this, getResources().getString(R.string.dialog_personal_exit), "退出", "取消");
    }

    private void showDialDialog() {
        DialogConfirm alert = new DialogConfirm();
        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked)
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", MyConstant.COM_PHONE_NUM, null)));
            }
        });
        alert.showDialog(SettingActivity.this, getResources().getString(R.string.dialog_personal_phone), "呼叫", "取消");
    }

    /**
     * 查询认证状态、支付密码状态、用户信息
     */
    private void getUserinfo(final int state) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<List<UserBean>>> call = userBiz.getUserInformation(token);
        call.enqueue(new Callback<BaseResponse<List<UserBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<UserBean>>> arg0,
                                   Response<BaseResponse<List<UserBean>>> response) {
                BaseResponse<List<UserBean>> baseResponse = response.body();
                if (baseResponse != null) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {
                        List<UserBean> data = baseResponse.getData();
                        UserBean userBean = data.get(0);
                        String nick = userBean.getNick();
                        if (StringUtils.isBlank(nick)) {
                            nick_name.setText(userBean.getMobile());
                        } else {
                            nick_name.setText(nick);
                        }
                        int type = userBean.getDerail();
                        String url;
                        if (type == 1) {
                            url=userBean.getHeadurl() + "/"+userBean.getHead();
                        } else {
                            url=userBean.getHeadurl() + userBean.getHead();
                        }
                        Glide.with(mContext).load(url).override(80, 80).transform(new GlideCircleTransform(mContext))
                                .placeholder(R.drawable.pic_nomal_loading_style)
                                .error(R.drawable.img_default_head)
                                .into(img_login);
                        String payState = userBean.getPwd();
                        if (!StringUtils.isBlank(payState)) {//已设置支付密码
                            pwd_settint.setText("已设置");
                        }else{
                            pwd_settint.setText("未设置");
                        }
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                } else {
                    try {
                        int code = response.code();
                        String error = response.errorBody().string();
                        BaseResponse errorResponse = GsonUtil.GsonToBean(error, BaseResponse.class);
                        if (errorResponse.getCode() == (MyConstant.T_ERR_AUTH) || errorResponse.getCode() == MyConstant.T_LOGIN_ERR) {//token过期
                            MyConstant.HASLOGIN = false;
                            SPUtils.remove(mContext, "login");
                            SPUtils.remove(mContext, "token");
                        } else {
                            MyApplication.getInstance().showShortToast("服务器连接失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<UserBean>>> arg0, Throwable arg1) {
                ToastUtil.showToast(mContext, "服务器连接失败");
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent5 msgEvent5) {
        getUserinfo(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消事件注册
        EventBus.getDefault().unregister(this);
    }
    /**
     * 相册
     *
     * @param value
     */
    private void takePhotoForAlbum(int value) {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, value);
    }
    /**
     * 拍照
     *
     * @param value
     */
    private void takePhotoForCamera(int value) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, value);
    }

    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions, PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), type);
        } else {
            onGranted(type);
        }
    }

    //获得权限
    @Override
    public void onGranted(int value) {
        if (type == 1) {//拍照
            captureImage(CreateFile.FEEDBACK_PATH, value);
        } else if (type == 2) {//相册
            selectImage(value);
        }
    }


    @Override
    public void onDenied(List<String> deniedPermission) {


    }

    /**
     * 拍照
     *
     * @param path 照片存放的路径
     */
    public void captureImage(String path, int value) {
        Uri data;
        localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";//拍照后的图片路径
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(CreateFile.FEEDBACK_PATH, localTempImageFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(this, "com.fengda.app.fileprovider", f);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(f);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, data);
        startActivityForResult(intent, value);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int value) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, value);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            switch (type) {
                case 1: {
                    handleCameraRet(data, requestCode);
                    break;
                }
                case 2: {
                    handleAlbumRet(data, requestCode);
                    break;
                }

                default:
                    break;
            }
        }
    }

    private void handleCameraRet(Intent data, int value) {
        // 将保存在本地的图片取出并缩小后显示在界面上
        String imgKey = ImgSetUtil.getImgKeyString();
        String path = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        compressWithLs(new File(path), value, imgKey);
    }


    private void handleAlbumRet(Intent data, int value) {
        String imgKey = ImgSetUtil.getImgKeyString();
        ContentResolver resolver = getContentResolver();
        // 照片的原始资源地址
        Uri originalUri = data.getData();
        String path = getAblumPicPath(data, this);
        compressWithLs(new File(path), value, imgKey);

    }


    /**
     * @param data
     * @param ac
     * @return
     * @方法说明:获得相册图片的路径
     * @方法名称:getAblumPicPath
     * @返回 String
     */
    public static String getAblumPicPath(Intent data, Activity ac) {
        Uri originalUri = data.getData();
        String path = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        if (originalUri != null && proj != null) {
            Cursor cursor = ac.getContentResolver().query(originalUri, null, null, null, null);
            if (cursor == null) {
                path = originalUri.getPath();
                if (!StringUtils.isEmpty(path)) {
                    String type = ".jpg";
                    String type1 = ".png";
                    if (path.endsWith(type) || path.endsWith(type1)) {
                        return path;
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            } else {
                /**将光标移至开，这个很重要，不小心很容易引起越**/
                cursor.moveToFirst();
                /**按我个人理解 这个是获得用户选择的图片的索引**/
                int column_index = cursor.getColumnIndex(proj[0]);
                /** 最后根据索引值获取图片路**/
                path = cursor.getString(column_index);
                cursor.close();
            }
        }
        return path;
    }


    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(final File f, final int value, final String imgKey) {
        Luban.get(mContext)
                .load(f)
                .putGear(Luban.THIRD_GEAR)
                .setFilename(System.currentTimeMillis() + "")
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File f) {
                        dialog.show();
                        upLoadAli(imgKey, f.getAbsolutePath(), value);
                    }

                    @Override
                    public void onError(Throwable e) {//压缩失败
                        upLoadAli(imgKey, f.getAbsolutePath(), value);
                    }
                }).launch();
    }


    private void upLoadAli(final String key, final String path, final int value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = new PutObjectSamples(oss, MyConstant.ALI_PUBLIC_BUCKET_RECOMMEND, key, path).putObjectFromLocalFile();
                if (flag) {//上传成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            switch (value) {
                                case 1:
                                    updateHeadPic(key);
                                    Glide.with(mContext).load(path).fitCenter().override(80, 80).transform(new GlideCircleTransform(mContext))
                                            // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                            .into(img_login);
                                    break;
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ToastUtil.showToast(mContext, "上传失败,请重新上传");
                        }
                    });
                }
            }
        }).start();
    }

    private void updateHeadPic(String key) {
        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.updateHeadPic(token, key);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                dialog.dismiss();
                super.onResponse(arg0, response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        ToastUtil.showToast(mContext, "头像修改成功");
                        EventBus.getDefault().post(new MsgEvent5());
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0, Throwable arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "网络状态不佳,请稍后再试");
            }
        });
    }
    //支付密码设置成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent2 msgEvent2) {
        pwd_settint.setText("已设置");
        SPUtils.put(mContext, "payPwd" + token, MyConstant.SUCCESS);
    }
}
