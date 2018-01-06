package com.fengda.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.aliutil.GetObjectSamples;
import com.fengda.app.aliutil.PutObjectSamples;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.AuthError;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.PersonAuth;
import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.constant.MyOSSConfig;
import com.fengda.app.dialog.DialogSelPhoto;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.eventbus.MsgEvent14;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.interfaces.PermissionListener;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.FileUtils;
import com.fengda.app.utils.ImgSetUtil;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.utils.luban.Luban;
import com.fengda.app.utils.luban.OnCompressListener;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 实名认证流程：个人信息
 */

public class CertificationBindUserActivity extends BaseActivity implements View.OnClickListener, PermissionListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private Context mContext;
    private Button btn_next;
    private String localTempImageFileName;
    private EditText ed_name, ed_number;
    private int type = 0;
    private String frontImgKey;
    private String backImgKey;
    private String handImgKey;

    private DialogSelPhoto dialogSelPhoto;
    private ImageView img_card_front;
    private ImageView img_card_back;
    private ImageView img_card_hand;

    private ImageView img_add_card_front,showTranslucentFront;
    private ImageView img_add_card_back,showTranslucentBack;
    private ImageView img_add_card_hand,showTranslucentHand;
    private ScrollView scroll_view;
    private int state;
    private OSS oss;

    private TextView tv_err1;
    private TextView tv_err2;
    private TextView tv_err3;
    private TextView tv_err4;
    private TextView tv_err5;
    private String token;
    private AuthError authError;
    private PersonAuth personAuth;
    private String code;
    private File cacheFile;
    private GetObjectSamples getObject;
    private List<String> urls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_person);
        mContext = this;
        EventBus.getDefault().register(this);
        token = (String) SPUtils.get(mContext, "token", "");
        token = EncodeUtils.base64Decode2String(token);
        dialogSelPhoto = new DialogSelPhoto();
        oss = new OSSClient(getApplicationContext(), MyConstant.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        initDialog();
        initViews();
        initEvents();
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

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        btn_next = (Button) findViewById(R.id.btn_next);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_number = (EditText) findViewById(R.id.ed_number);
        img_card_front = (ImageView) findViewById(R.id.img_card_front);
        img_card_back = (ImageView) findViewById(R.id.img_card_back);
        img_card_hand = (ImageView) findViewById(R.id.img_card_hand);

        img_add_card_front = (ImageView) findViewById(R.id.img_add_card_front);
        img_add_card_back = (ImageView) findViewById(R.id.img_add_card_back);
        img_add_card_hand = (ImageView) findViewById(R.id.img_add_card_hand);

        showTranslucentFront = (ImageView) findViewById(R.id.show_translucent_front);
        showTranslucentBack = (ImageView) findViewById(R.id.show_translucent_back);
        showTranslucentHand = (ImageView) findViewById(R.id.show_translucent_hand);
        img_add_card_front.setOnClickListener(this);
        img_add_card_back.setOnClickListener(this);
        img_add_card_hand.setOnClickListener(this);
        ed_number.setOnClickListener(this);
        ed_name.setOnClickListener(this);
        tv_err1 = (TextView) findViewById(R.id.tv_err1);
        tv_err2 = (TextView) findViewById(R.id.tv_err2);
        tv_err3 = (TextView) findViewById(R.id.tv_err3);
        tv_err4 = (TextView) findViewById(R.id.tv_err4);
        tv_err5 = (TextView) findViewById(R.id.tv_err5);
        btn_next.setOnClickListener(this);
        scroll_view.smoothScrollTo(0, 0);
        state = getIntent().getIntExtra("state", 0);
        code = getIntent().getStringExtra("code");
        if (state == 1) {//未提交实名认证
            btn_next.setText(mContext.getResources().getString(R.string.tishi_19));
        }else if (state == 3) {//身份信息有误,银行卡信息有误
            personAuth = (PersonAuth) getIntent().getSerializableExtra("personAuth");
            btn_next.setText(mContext.getResources().getString(R.string.tishi_19));
            updateUi();
        }
    }

    private void updateUi() {
        ed_name.setText(personAuth.getName());
        ed_number.setText(personAuth.getCard_number());
        Glide.with(mContext).load(personAuth.getPic_uri() + personAuth.getFront_card()).fitCenter()
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_front);
        Glide.with(mContext).load(personAuth.getPic_uri() + personAuth.getRear_card()).fitCenter()
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_back);
        Glide.with(mContext).load(personAuth.getPic_uri() + personAuth.getHand_logo()).fitCenter()
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_hand);

        urls.add(personAuth.getFront_card());
        urls.add(personAuth.getRear_card());
        urls.add(personAuth.getHand_logo());
        downLoadPic();
        frontImgKey = personAuth.getFront_card();
        backImgKey = personAuth.getRear_card();
        handImgKey = personAuth.getHand_logo();
        authError = personAuth.getRemarks();
        String name = authError.getName();
        String card_number = authError.getCard_number();
        String hand_logo = authError.getHand_logo();
        String front_card = authError.getFront_card();
        String rear_card = authError.getRear_card();
        String bank_card = authError.getBank_card();
        String bank_logo = authError.getBank_logo();
        String remarks = authError.getRemarks();
        if (!StringUtils.isBlank(name)) {
            tv_err1.setText(name);
        }
        if (!StringUtils.isBlank(card_number)) {
            tv_err2.setText(card_number);
        }
        if (!StringUtils.isBlank(hand_logo)) {
            tv_err3.setText(hand_logo);
            showTranslucentFront.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isBlank(front_card)) {
            tv_err4.setText(front_card);
            showTranslucentBack.setVisibility(View.VISIBLE);

        }
        if (!StringUtils.isBlank(rear_card)) {
            tv_err5.setText(rear_card);
            showTranslucentHand.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (!checkParmer()) {
                    return;
                }
                if (state == 1 || state == 3) {
                    Intent intent = new Intent(mContext, BindBankActivity.class);
                    if (state == 1) {
                        personAuth = new PersonAuth();
                    }
                    personAuth.setFront_card(frontImgKey);
                    personAuth.setRear_card(backImgKey);
                    personAuth.setHand_logo(handImgKey);
                    personAuth.setName(ed_name.getText().toString());
                    personAuth.setCard_number(ed_number.getText().toString());
                    intent.putExtra("personAuth", personAuth);
                    intent.putExtra("state", state);
                    intent.putExtra("code", code);
                    startActivity(intent);
                }
                break;
            case R.id.img_add_card_front://选择正面照
                tv_err3.setVisibility(View.GONE);
                dialogSelPhoto.showDialog(CertificationBindUserActivity.this, 1);
                break;
            case R.id.img_add_card_back://选择反面照
                tv_err4.setVisibility(View.GONE);
                dialogSelPhoto.showDialog(CertificationBindUserActivity.this, 2);
                break;
            case R.id.img_add_card_hand://选择手持照
                tv_err5.setVisibility(View.GONE);
                dialogSelPhoto.showDialog(CertificationBindUserActivity.this, 3);
                break;
            case R.id.ed_name://
                tv_err1.setVisibility(View.GONE);
                break;
            case R.id.ed_number://
                tv_err2.setVisibility(View.GONE);
                break;

        }
    }

    private boolean checkParmer() {
        if (StringUtils.isBlank(ed_name.getText().toString())) {
            ToastUtil.showToast(mContext, "姓名不能为空");
            return false;
        }
        if (StringUtils.isBlank(ed_number.getText().toString())) {
            ToastUtil.showToast(mContext, "身份证号不能为空");
            return false;
        }
        if (StringUtils.isBlank(frontImgKey)) {
            showShortToast("请上传身份证正面照");
            return false;
        }
        if (StringUtils.isBlank(backImgKey)) {
            showShortToast("请上传身份证反面照");
            return false;
        }
        if (StringUtils.isBlank(handImgKey)) {
            showShortToast("请上传手持身份证照");
            return false;
        }
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        switch (value){
                            case 1:
                                showTranslucentFront.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                showTranslucentBack.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                showTranslucentHand.setVisibility(View.VISIBLE);
                                break;
                        }
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
                boolean flag = new PutObjectSamples(oss, MyConstant.ALI_BUCKET_RECOMMEND, key, path).putObjectFromLocalFile();
                if (flag) {//上传成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            switch (value) {
                                case 1:
                                    //  imgPath = path;
                                    showTranslucentFront.setVisibility(View.GONE);
                                    frontImgKey = key;
                                    Glide.with(mContext).load(path).fitCenter()
                                            // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                            .into(img_card_front);
                                    break;
                                case 2:
                                    //     imgPath = path;
                                    backImgKey = key;
                                    showTranslucentBack.setVisibility(View.GONE);
                                    //    img_card_back.setImageBitmap(newBitmap);
                                    Glide.with(mContext).load(path).fitCenter()
                                            // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                            .into(img_card_back);
                                    break;
                                case 3:
                                    //    imgPath = path;
                                    handImgKey = key;
                                    showTranslucentHand.setVisibility(View.GONE);
                                    //      img_card_hand.setImageBitmap(newBitmap);
                                    Glide.with(mContext).load(path).fitCenter()
                                            // .placeholder(R.drawable.load_fail).error(R.drawable.load_fail)
                                            .into(img_card_hand);
                                    break;
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (value) {
                                case 1:
                                    showTranslucentFront.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    showTranslucentBack.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    showTranslucentHand.setVisibility(View.VISIBLE);
                                    break;
                            }
                            dialog.dismiss();
                            showShortToast("上传失败,请重新上传");
                        }
                    });
                }
            }
        }).start();
    }



    /**
     * 修改认证信息
     */
    private void updateAuthentication() {
        dialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", ed_name.getText().toString());//
        params.put("card_number", ed_number.getText().toString());//
        params.put("hand_logo", handImgKey);//
        params.put("front_card", frontImgKey);//
        params.put("rear_card", backImgKey);//
        params.put("bank_id", personAuth.getBank_id() + "");//
        params.put("bank_card", personAuth.getCard_number());
        params.put("province", personAuth.getProvince_id());
        params.put("city", personAuth.getCity_id());
        params.put("county", personAuth.getCounty_id());
        params.put("branch", personAuth.getBranch());
        params.put("bank_logo", personAuth.getBank_card());

        APIService userBiz = RetrofitWrapper.getInstance().create(APIService.class);
        Call<BaseResponse<Object>> call = userBiz.updateAuthentication(token, params);
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
                        ToastUtil.showToast(mContext, "修改信息提交成功,请等待审核");
                        finish();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(LoginMsgEvent messageEvent) {
        String login = (String) SPUtils.get(mContext, "login", "");
        if (!StringUtils.isBlank(login) && login.equals(MyConstant.SUC_RESULT)) {// 已登录
            MyConstant.HASLOGIN = true;
            token = (String) SPUtils.get(mContext, "token", "");
            token = EncodeUtils.base64Decode2String(token);
        } else {// 未登录
            finish();
            MyConstant.HASLOGIN = false;
        }
    }


    private void downLoadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < urls.size(); i++) {
                    String key = urls.get(i);
                    cacheFile = FileUtils.getCacheFile(new File(FileUtils.getDiskCacheDir(getApplicationContext())), key);
                    getObject = new GetObjectSamples(oss, MyConstant.ALI_BUCKET_RECOMMEND, key);
                    String url = getObject.getObject(cacheFile);
                    urls.set(i, url);
                    if ((i + 1) == urls.size()) {
                        EventBus.getDefault().post(new MsgEvent14());
                    }
                }
            }
        }).start();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(MsgEvent14 messageEvent) {

        Glide.with(mContext).load(urls.get(0)).centerCrop()
                .override(187,100)
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_front);
        Glide.with(mContext).load(urls.get(1)).centerCrop()
                .override(187,100)
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_back);
        Glide.with(mContext).load(urls.get(2)).centerCrop()
                .override(187,100)
                .placeholder(R.drawable.pic_nomal_loading_style).error(R.drawable.pic_nomal_loading_style)
                .into(img_card_hand);


    }


}