package com.fengda.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.aliutil.PutObjectSamples;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.DeclarationBean;
import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.MyOSSConfig;
import com.fengda.app.dialog.DialogSelPhoto;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.ImgSetUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.utils.luban.Luban;
import com.fengda.app.utils.luban.OnCompressListener;
import com.fengda.app.view.HandyTextView;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.fengda.app.R.id.show_translucent;

/**
 * 报单
 */

public class DeclarationActivity extends BaseActivity implements View.OnClickListener, PermissionListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private Context mContext;
    private Button confirmButton;
    private String localTempImageFileName;
    private EditText declarationAmount;
    private HandyTextView tv_declaration_number, remainingAmount;
    private ImageView declarationImg,showTranslucent;
    private String imgKey;
    private DialogSelPhoto dialogSelPhoto;
    private OSS oss;
    private String token;
    private String phone;
    @ViewInject(R.id.tv_card_name)
    private EditText tv_card_name;
    @ViewInject(R.id.tv_card_number)
    private EditText tv_card_number;
    @ViewInject(R.id.ll_remaining)
    private LinearLayout ll_remaining;
    private DeclarationBean declarationBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration);
        mContext = this;
        x.view().inject(this);
        EventBus.getDefault().register(this);
        dialogSelPhoto = new DialogSelPhoto();
        oss = new OSSClient(getApplicationContext(), MyConstant.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        initViews();
        initDialog();
        initEvents();
        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if (isCameraSel) {
                    //拍照
                    takePhotoForCamera(imgIndex);
                } else {
                    //相册
                    takePhotoForAlbum(imgIndex);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initEvents() {
        getInfo();
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
        confirmButton = (Button) findViewById(R.id.confirm_button);
        tv_declaration_number = (HandyTextView) findViewById(R.id.tv_declaration_number);
        remainingAmount = (HandyTextView) findViewById(R.id.tv_remaining_amount);
        declarationAmount = (EditText) findViewById(R.id.et_declaration_amount);
        declarationImg = (ImageView) findViewById(R.id.declaration_img);
        showTranslucent = (ImageView) findViewById(show_translucent);

        confirmButton.setOnClickListener(this);
        declarationImg.setOnClickListener(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        phone = (String) SPUtils.get(mContext, "phone", "");
        tv_declaration_number.setText(phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_button:
                    sumbitBindBankInfo();
                break;
            case R.id.declaration_img:
                dialogSelPhoto.showDialog(DeclarationActivity.this, 0);
                break;
        }
    }

    private void sumbitBindBankInfo() {
        if (!checkParmer()) {
            return;
        }
        dialog.show();
        sumbitInfo();
    }


    /**
     * 获取报单
     */
    private void getInfo() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<DeclarationBean>>> call = userBiz.declarationLeft(token);
        call.enqueue(new HttpCallBack<BaseResponse<List<DeclarationBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<DeclarationBean>>> arg0,
                                   Response<BaseResponse<List<DeclarationBean>>> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                super.onResponse(arg0, response);
                BaseResponse<List<DeclarationBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        declarationBean = baseResponse.getData().get(0);
                        if (declarationBean.getDeclaration_limit() == 1){
                            ll_remaining.setVisibility(View.GONE);
                        }else {
                            ll_remaining.setVisibility(View.VISIBLE);
                        }
                        remainingAmount.setText(declarationBean.getDeclaration_left());
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }
                } else {
                    String error = response.errorBody().toString();
                    error = response.raw().toString();
                    int code = response.code();
                    error = response.toString();
                    String reRsponse = response.raw().toString();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<DeclarationBean>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    /**
     * 提交报单
     */
    private void sumbitInfo() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
       Call<BaseResponse<List<DeclarationBean>>> call = userBiz.declarationForm(token,imgKey,
               declarationAmount.getText().toString(),tv_card_number.getText().toString(),tv_card_name.getText().toString());//短信验证码
        call.enqueue(new HttpCallBack<BaseResponse<List<DeclarationBean>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<DeclarationBean>>> arg0,
                                   Response<BaseResponse<List<DeclarationBean>>> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                super.onResponse(arg0, response);
                BaseResponse<List<DeclarationBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    String desc = baseResponse.getMsg();
                    int retCode = baseResponse.getCode();
                    if (retCode == MyConstant.SUCCESS) {//
                        showShortToast("报单提交成功");
                        declarationAmount.setText("");
                        declarationAmount.setText(baseResponse.getData().get(0).getDeclaration_left());
//                        EventBus.getDefault().post(new MsgEvent3());
                        defaultFinish();
                    } else {
                        ToastUtil.showToast(mContext, desc);
                    }

                } else {
                    String error = response.errorBody().toString();
                    error = response.raw().toString();
                    int code = response.code();
                    error = response.toString();
                    String reRsponse = response.raw().toString();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<DeclarationBean>>> arg0,
                                  Throwable arg1) {
            }
        });
    }


    private boolean checkParmer() {
        String value = declarationAmount.getText().toString();
        if (!StringUtils.isBlank(value)) {
            long amount = Long.parseLong(value);
            BigDecimal amountMoney = new BigDecimal(value);
            BigDecimal money = new BigDecimal(remainingAmount.getText().toString());
            if (declarationBean!= null && declarationBean.getDeclaration_limit() == 0) {
                if (amountMoney.compareTo(money) == 1) {
                    showShortToast("超过最大可用报单金额");
                    return false;
                }
            }
            if (StringUtils.isBlank(tv_card_name.getText().toString())){
                showShortToast("请输入真实姓名");
                return false;
            }
            if (StringUtils.isBlank(tv_card_number.getText().toString())){
                showShortToast("请输入银行卡号后四位");
                return false;
            }
            if (amount <= 0) {
                showShortToast("报单金额不合法，请重新输入");
                return false;
            }
//            int compare = CommonUtils.compareTo(value, "10000");
//            if (compare == 1) {
//                showShortToast("一次最多只能兑换10000");
//                return;
//            }
        } else {
            showShortToast("请输入报单金额");
            return false;
        }
        if (StringUtils.isBlank(imgKey)) {
            showShortToast("您还没有上传银行卡正面照");
            return false;
        }
        return true;
    }

    private void takePhotoForAlbum(int value) {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, 2);
    }

    private void takePhotoForCamera(int value) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, this, 1);
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
        if (value == 1) {
            captureImage(CreateFile.FEEDBACK_PATH, 1);
        } else if (value == 2) {
            selectImage(2);
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
        localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(CreateFile.FEEDBACK_PATH, localTempImageFileName);
        data = Uri.fromFile(f);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            String fileName;
            switch (requestCode) {
                case 1: {
                    handleCameraRet(data, 0);
                    break;
                }
                case 2: {
                    handleAlbumRet(data, 0);
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
        compressWithLs(new File(path), imgKey);
    }


    private void handleAlbumRet(Intent data, int value) {
        String fileName;
        String imgKey = ImgSetUtil.getImgKeyString();
        ContentResolver resolver = getContentResolver();
        // 照片的原始资源地址
        Uri originalUri = data.getData();
        String path = getAblumPicPath(data, this);
        compressWithLs(new File(path), imgKey);
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


    private void compressWithLs(final File f, final String imgKey) {
        Luban.get(this)
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
                        showTranslucent.setVisibility(View.VISIBLE);
                        upLoadAli(imgKey, f.getAbsolutePath());
                        Glide.with(mContext).load(f).fitCenter()
                                // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                .into(declarationImg);
                    }

                    @Override
                    public void onError(Throwable e) {//压缩失败
                        upLoadAli(imgKey, f.getAbsolutePath());
                        Glide.with(mContext).load(f).fitCenter()
                                // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                .into(declarationImg);
                    }
                }).launch();
    }


    private void upLoadAli(final String key, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = new PutObjectSamples(oss, MyConstant.ALI_BUCKET_RECOMMEND, key, path).putObjectFromLocalFile();
                if (flag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            imgKey = key;
                            showTranslucent.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            showShortToast("上传失败,请重新上传");
                        }
                    });
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
            phone = (String) SPUtils.get(mContext, "phone", "");
            tv_declaration_number.setText(phone);
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }

}