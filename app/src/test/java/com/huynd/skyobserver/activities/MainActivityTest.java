package com.huynd.skyobserver.activities;

import com.huynd.skyobserver.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/5/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = LOLLIPOP)
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
