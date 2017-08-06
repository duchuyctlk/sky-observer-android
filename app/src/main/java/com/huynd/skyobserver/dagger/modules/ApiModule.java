package com.huynd.skyobserver.dagger.modules;

import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.services.ServiceGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by HuyND on 8/6/2017.
 */

@Module
public class ApiModule {
    @Singleton
    @Provides
    public Retrofit providesRetrofit() {
        return ServiceGenerator.createService();
    }

    @Singleton
    @Provides
    public PricesAPI providesPricesAPI(Retrofit retrofit) {
        return retrofit.create(PricesAPI.class);
    }
}