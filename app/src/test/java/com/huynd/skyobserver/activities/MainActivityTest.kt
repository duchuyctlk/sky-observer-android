package com.huynd.skyobserver.activities

import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

import org.junit.Assert.fail
import org.junit.Test

/**
 * Created by HuyND on 5/25/2019.
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private lateinit var mActivity: MainActivity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mActivity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun shouldHandleOnOptionsItemSelected() {
        try {
            mActivity.onOptionsItemSelected(null)
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }
}