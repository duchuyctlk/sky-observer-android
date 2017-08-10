package com.huynd.skyobserver.fragments;

import android.support.test.rule.ActivityTestRule;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragmentAndroidTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        setUpPricePerDayFragment();
    }

    @Test
    public void shouldContainViewWidgets() throws Exception {
        checkViewWidgetsIsDisplayed(R.id.spinner_month, R.id.spinner_year, R.id.btn_get_prices);
        onView(withId(R.id.grid_view_price)).check(matches(isEnabled()));
    }

    private void checkViewWidgetsIsDisplayed(int... ids) {
        for (int id : ids) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    private void setUpPricePerDayFragment() throws Exception {
        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(0).perform(click());
    }
}
