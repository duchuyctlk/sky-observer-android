package com.huynd.skyobserver.idlingResource;

import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;

import com.huynd.skyobserver.activities.BaseActivity;
import com.huynd.skyobserver.activities.MainActivity;

/**
 * Created by HuyND on 8/19/2017.
 */

public class ChangeFragmentIdlingResource<T extends Fragment> implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;
    private BaseActivity mActivity;

    Class<T> mClass;

    public ChangeFragmentIdlingResource(Class<T> clazz, MainActivity activity) {
        mClass = clazz;
        mActivity = activity;
    }

    @Override
    public String getName() {
        return ChangeFragmentIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) {
            return true;
        }
        if (mActivity == null) {
            return false;
        }

        Fragment fragment = mActivity.getCurrentFragment();

        isIdle = fragment != null && mClass.isInstance(fragment);
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
