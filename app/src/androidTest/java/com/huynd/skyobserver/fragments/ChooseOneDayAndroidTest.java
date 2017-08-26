package com.huynd.skyobserver.fragments;

import android.support.test.rule.ActivityTestRule;
import android.widget.ArrayAdapter;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.models.AvailableMonth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by HuyND on 8/26/2017.
 */

public class ChooseOneDayAndroidTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();

        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(1).perform(click());
    }

    @Test
    public void shouldCoverOnNothingSelected() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((ChooseOneDayFragment) mActivity.getCurrentFragment())
                            .updateAvailOutBoundMonths(new ArrayList<AvailableMonth>());
                } catch (Exception exception) {
                    fail("Unexpected behavior happened.");
                }
            }
        });
    }

    @Test
    public void shouldClassCatchCastException() throws Exception {
        ChooseOneDayFragment fragment = (ChooseOneDayFragment) mActivity.getCurrentFragment();
        ArrayAdapter<AvailableMonth> adapter = spy(fragment.mSpinnerOutboundMonthAdapter);
        when(adapter.getItem(any(int.class))).thenThrow(new ClassCastException());
        fragment.mSpinnerOutboundMonthAdapter = adapter;
        try {
            onView(withId(R.id.btn_find_flights)).perform(click());
        } catch (Exception exception) {
            fail("Unexpected behavior happened.");
        }
    }
}
