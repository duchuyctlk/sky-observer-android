package com.huynd.skyobserver.utils;

import android.app.ProgressDialog;

import com.huynd.skyobserver.BuildConfig;
import com.huynd.skyobserver.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

/**
 * Created by HuyND on 8/13/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = LOLLIPOP)
public class FlowUtilsTest {
    private MainActivity mMainActivity;
    private FlowUtils mFlowUtils;

    @Before
    public void setUp() {
        mMainActivity = Robolectric.setupActivity(MainActivity.class);
        mFlowUtils = FlowUtils.getInstance();
    }

    @Test
    public void shouldCatchExceptionWhenDismissDialogAfterActivityFinished() throws Exception {
        try {
            mFlowUtils.showLoadingDialog(mMainActivity);

            ProgressDialog progressDialog = spy(mFlowUtils.mProgressDialog);
            doThrow(new IllegalArgumentException()).when(progressDialog).dismiss();
            mFlowUtils.mProgressDialog = progressDialog;

            mFlowUtils.dismissLoadingDialog();
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException should be caught.");
        }
    }
}
