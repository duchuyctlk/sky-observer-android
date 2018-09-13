package com.huynd.skyobserver.dagger.component;

import com.huynd.skyobserver.dagger.modules.ApiModuleAndroidTest;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HuyND on 8/6/2017.
 */

@Singleton
@Component(modules = ApiModuleAndroidTest.class)
public interface SkyObserverComponentAndroidTest extends SkyObserverComponent {

}
