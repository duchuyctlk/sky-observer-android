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

    public void setFragment(Fragment fragment, @NonNull String tag) {
        if (tag.equals(getCurrentFragmentTag())) {
            return;
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

            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        mCurrentFragmentTag = tag;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragmentTag != null ? fragmentManager.findFragmentByTag(mCurrentFragmentTag) : null;
    }

    private String getCurrentFragmentTag() {
        return mCurrentFragmentTag;
    }
}
