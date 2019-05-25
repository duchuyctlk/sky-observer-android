package com.huynd.skyobserver

import com.huynd.skyobserver.activities.MainActivity
import com.huynd.skyobserver.fragments.PriceOneDayFragment
import com.huynd.skyobserver.presenters.PriceOneDayPresenterImpl
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by HuyND on 5/25/2019.
 */
@RunWith(RobolectricTestRunner::class)
class PriceOneDayViewTest {
    private lateinit var mActivity: MainActivity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mActivity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun testShowInvalidDateDialog() {
        try {
            val fragment = PriceOneDayFragment.newInstance() as PriceOneDayFragment
            val presenter = PriceOneDayPresenterImpl(fragment, null)
            mActivity.setFragment(fragment, PriceOneDayFragment.TAG, false)
            presenter.getPrices(2015, 1, 1, "SGN", "HAN", true)
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }
}
