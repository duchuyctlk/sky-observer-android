package com.huynd.skyobserver;

import android.app.Application;

import com.huynd.skyobserver.dagger.component.DaggerSkyObserverComponent;
import com.huynd.skyobserver.dagger.component.SkyObserverComponent;
import com.huynd.skyobserver.dagger.modules.ApiModule;

/**
 * Created by HuyND on 8/6/2017.
 */

public class SkyObserverApp extends Application {
    private SkyObserverComponent mSkyObserverComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mSkyObserverComponent = DaggerSkyObserverComponent
                .builder()
                .apiModule(new ApiModule())
                .build();
    }

    public SkyObserverComponent getSkyObserverComponent() {
        return mSkyObserverComponent;
    }
}
