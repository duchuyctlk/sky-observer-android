package com.huynd.skyobserver.dagger.modules;
import com.huynd.skyobserver.services.PricesAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HuyND on 8/6/2017.
 */

@Module
public class ApiModuleAndroidTest {
    @Singleton
    @Provides
    public PricesAPI providesPricesAPI() {
        // TODO mock(PricesAPI.class);
        return null;
    }
}
