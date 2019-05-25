package com.huynd.skyobserver.activities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/5/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    private MainActivity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void shouldHandleOnOptionsItemSelected() throws Exception {
        try {
            mActivity.onOptionsItemSelected(null);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }
}
