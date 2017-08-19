package com.huynd.skyobserver.fragments;

import android.support.test.rule.ActivityTestRule;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by HuyND on 8/19/2017.
 */

public class BaseFragmentAndroidTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void testShowFailedDialogWithNullMessage() throws Exception {
        MainActivity activity = (MainActivity) mActivityRule.getActivity();
        final BaseFragment fragment = (BaseFragment) activity.getCurrentFragment();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                fragment.showFailedDialog(null);
            }
        });
        onView(withText(R.string.failed_to_get_prices_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }
}
