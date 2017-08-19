package com.huynd.skyobserver.activities;

import android.os.Bundle;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;

import com.huynd.skyobserver.fragments.PriceOneDayFragment;
import com.huynd.skyobserver.fragments.PricePerDayFragment;
import com.huynd.skyobserver.idlingResource.ChangeFragmentIdlingResource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/19/2017.
 */

public class BaseActivityAndroidTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;
    private ChangeFragmentIdlingResource mIdlingResource;

    @Before
    public void setUp() throws Exception {
        mActivity = (MainActivity) mActivityRule.getActivity();
        mIdlingResource = new ChangeFragmentIdlingResource(mActivity);
    }

    @Test
    public void shouldPressBackOnPriceOneDayProperly() throws Exception {
        mActivity.setFragment(PricePerDayFragment.newInstance(), PricePerDayFragment.TAG);

        Fragment priceOneDayFragment = PriceOneDayFragment.newInstance();
        priceOneDayFragment.setArguments(new Bundle());
        mActivity.setFragment(priceOneDayFragment, PriceOneDayFragment.TAG);

        Espresso.registerIdlingResources(mIdlingResource);
        pressBack();

        assertTrue(mActivity.getCurrentFragment() instanceof PricePerDayFragment);
        Espresso.unregisterIdlingResources(mIdlingResource);
    }

    @Test
    public void shouldPressBackOnPricePerDayProperly() throws Exception {
        mActivity.setFragment(PricePerDayFragment.newInstance(), PricePerDayFragment.TAG);

        try {
            pressBack();
        } catch (NoActivityResumedException exception) {
            exception.printStackTrace();
            return;
        }
        fail("The app is killed. This line should never be reached.");
    }

    @Test
    public void shouldShowCorrectFragments() throws Exception {
        Fragment pricePerDayFragment = PricePerDayFragment.newInstance();
        mActivity.setFragment(pricePerDayFragment, PricePerDayFragment.TAG);

        Fragment priceOneDayFragment = PriceOneDayFragment.newInstance();
        priceOneDayFragment.setArguments(new Bundle());
        mActivity.setFragment(priceOneDayFragment, PriceOneDayFragment.TAG);

        Espresso.registerIdlingResources(mIdlingResource);

        mActivity.setFragment(pricePerDayFragment, PricePerDayFragment.TAG);
        assertTrue(mActivity.getCurrentFragment() instanceof PricePerDayFragment);
    }
}
