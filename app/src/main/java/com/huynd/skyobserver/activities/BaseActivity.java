package com.huynd.skyobserver.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.huynd.skyobserver.R;

/**
 * Created by HuyND on 8/6/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
    }

    public void setFragment(Fragment fragment, @NonNull String tag, boolean clearFragmentBackStack) {
        if (tag.equals(getCurrentFragmentTag())) {
            return;
        }

        if (clearFragmentBackStack) {
            clearFragmentBackStack();
        }

        try {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null) {
                fragmentTransaction.hide(currentFragment);
            }

            Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
            if (existingFragment != null) {
                fragmentTransaction.show(existingFragment);
            } else {
                fragmentTransaction.add(R.id.fragment_container, fragment, tag);
            }

            fragmentTransaction.addToBackStack(tag);

            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        mCurrentFragmentTag = tag;
    }

    public void clearFragmentBackStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragmentTag != null ? fragmentManager.findFragmentByTag(mCurrentFragmentTag) : null;
    }

    private String getCurrentFragmentTag() {
        return mCurrentFragmentTag;
    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStackImmediate();

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount < 1) {
            super.onBackPressed();
        } else {
            mCurrentFragmentTag = fragmentManager
                    .getBackStackEntryAt(backStackEntryCount - 1)
                    .getName();
        }
    }
}
