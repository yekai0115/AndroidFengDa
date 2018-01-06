package com.fengda.app.api;


import com.fengda.app.constant.MyConstant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitWrapper {

	private static RetrofitWrapper instance;
	private Retrofit retrofit;
	private static OkHttpClient httpClient = new OkHttpClient.Builder()

	.addInterceptor(new Interceptor() {

		@Override
		public Response intercept(Chain chain) throws IOException {

			Request request = chain.request().newBuilder()
//					.addHeader("version", "1.0")
//					.addHeader("os", "android")
//					.addHeader("deviceid", "123")
//					.addHeader("uid", "0")
//					.addHeader("timestamp", "2016")
//					.addHeader("access_token", "456")
//					.addHeader("osversion", "os")
					.build();
			return chain.proceed(request);

		}

	})

	.build();

	private RetrofitWrapper() {
		retrofit = new Retrofit.Builder()
				.baseUrl(MyConstant.WEB_SERVICE_BASE)
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient)
				.build();
	}

	public static RetrofitWrapper getInstance() {
		if (instance == null) {
			synchronized (RetrofitWrapper.class) {
				instance = new RetrofitWrapper();
			}
		}
		return instance;
	}

	public <T> T create(Class<T> service) {
		return retrofit.create(service);
	}
}
