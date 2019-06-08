package com.huynd.skyobserver.dagger.component;

import com.huynd.skyobserver.dagger.modules.ApiModuleAndroidTest;
import com.huynd.skyobserver.fragments.ChooseOneDayAndroidTest;
import com.huynd.skyobserver.fragments.DateCheapestFragmentAndroidTest;
import com.huynd.skyobserver.fragments.MonthCheapestFragmentAndroidTest;
import com.huynd.skyobserver.fragments.PriceOneDayFragmentAndroidTest;
import com.huynd.skyobserver.fragments.PricePerDayFragmentAndroidTest;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HuyND on 8/6/2017.
 */

@Singleton
@Component(modules = ApiModuleAndroidTest.class)
public interface SkyObserverComponentAndroidTest extends SkyObserverComponent {
    void inject(PricePerDayFragmentAndroidTest fragmentAndroidTest);

    void inject(PriceOneDayFragmentAndroidTest fragmentAndroidTest);

    void inject(ChooseOneDayAndroidTest fragmentAndroidTest);

    void inject(DateCheapestFragmentAndroidTest fragmentAndroidTest);

    void inject(MonthCheapestFragmentAndroidTest fragmentAndroidTest);
}
