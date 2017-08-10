package com.huynd.skyobserver;

import com.huynd.skyobserver.dagger.component.DaggerSkyObserverComponentAndroidTest;
import com.huynd.skyobserver.dagger.component.SkyObserverComponent;
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest;
import com.huynd.skyobserver.dagger.modules.ApiModuleAndroidTest;

/**
 * Created by HuyND on 8/6/2017.
 */

public class SkyObserverAndroidTestApp extends SkyObserverApp {
    private SkyObserverComponentAndroidTest mSkyObserverComponentAndroidTest;

    @Override
    public void onCreate() {
        super.onCreate();
        mSkyObserverComponentAndroidTest = DaggerSkyObserverComponentAndroidTest
                .builder()
                .apiModuleAndroidTest(new ApiModuleAndroidTest())
                .build();
    }

    @Override
    public SkyObserverComponent getSkyObserverComponent() {
        return mSkyObserverComponentAndroidTest;
    }
}
