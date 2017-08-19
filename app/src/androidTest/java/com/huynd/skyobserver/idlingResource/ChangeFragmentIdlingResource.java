package com.huynd.skyobserver.idlingResource;

import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;

import com.huynd.skyobserver.activities.BaseActivity;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.fragments.PriceOneDayFragment;

/**
 * Created by HuyND on 8/19/2017.
 */

public class ChangeFragmentIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;
    private BaseActivity mActivity;

    public ChangeFragmentIdlingResource(MainActivity activity) {
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

        isIdle = fragment != null && fragment instanceof PriceOneDayFragment;
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
