package com.huynd.skyobserver.dagger.modules;
import com.huynd.skyobserver.services.PricesAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by HuyND on 8/6/2017.
 */

@Module
public class ApiModuleAndroidTest {
    @Singleton
    @Provides
    public PricesAPI providesPricesAPI() {
        return mock(PricesAPI.class);
    }
}
