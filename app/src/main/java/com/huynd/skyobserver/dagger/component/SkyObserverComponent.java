package com.huynd.skyobserver.dagger.component;

import com.huynd.skyobserver.dagger.modules.ApiModule;
import com.huynd.skyobserver.fragments.PricePerDayFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HuyND on 8/6/2017.
 */

@Singleton
@Component(modules = ApiModule.class)
public interface SkyObserverComponent {
    void inject(PricePerDayFragment fragment);
}
