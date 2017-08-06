package com.huynd.skyobserver;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * Created by HuyND on 8/6/2017.
 */

public class AndroidTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, SkyObserverAndroidTestApp.class.getName(), context);
    }
}
