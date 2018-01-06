package com.fengda.app.http;


import com.fengda.app.application.MyApplication;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.utils.GsonUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/7/1 0001.
 */

public class HttpCallBack<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.body() == null) {
            try {
                int code = response.code();
                String errorResponse = response.errorBody().string();
                BaseResponse baseResponse = GsonUtil.GsonToBean(errorResponse, BaseResponse.class);
                if(code==MyConstant.SERVER_ERROE){
                    MyApplication.getInstance().showShortToast("服务器内部错误,请稍后再试");
                }else if (baseResponse.getCode()==(MyConstant.T_ERR_AUTH)||baseResponse.getCode()==(MyConstant.T_LOGIN_ERR)) {//token过期
                    MyApplication.getInstance().startLoginActivity();
                }else{
                    MyApplication.getInstance().showShortToast("服务器连接失败");
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        MyApplication.getInstance().showShortToast("服务器连接失败");
    }



}
