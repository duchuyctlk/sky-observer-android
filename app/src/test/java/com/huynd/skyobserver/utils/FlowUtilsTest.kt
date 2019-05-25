package com.huynd.skyobserver.utils

import com.huynd.skyobserver.activities.MainActivity
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito.doThrow
import org.mockito.Mockito.spy
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by HuyND on 8/13/2017.
 */

@RunWith(RobolectricTestRunner::class)
class FlowUtilsTest {
    private lateinit var mMainActivity: MainActivity
    private lateinit var mFlowUtils: FlowUtils

    @Before
    fun setUp() {
        mMainActivity = Robolectric.setupActivity(MainActivity::class.java)
        mFlowUtils = FlowUtils.instance
    }

    @Test
    fun shouldCatchExceptionWhenDismissDialogAfterActivityFinished() {
        try {
            mFlowUtils.showLoadingDialog(mMainActivity)

            val progressDialog = spy(mFlowUtils.mProgressDialog)
            doThrow(IllegalArgumentException()).`when`(progressDialog)?.dismiss()
            mFlowUtils.mProgressDialog = progressDialog

            mFlowUtils.dismissLoadingDialog()
        } catch (e: IllegalArgumentException) {
            fail("IllegalArgumentException should be caught.")
        }
    }
}
