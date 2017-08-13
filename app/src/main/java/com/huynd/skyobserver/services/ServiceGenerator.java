package com.huynd.skyobserver.services;

import com.huynd.skyobserver.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HuyND on 8/6/2017.
 */

public class ServiceGenerator {
    public ServiceGenerator() {
        throw new AssertionError();
    }

    public static Retrofit createService() {

        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }
}
