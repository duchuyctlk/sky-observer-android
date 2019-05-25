package com.huynd.skyobserver.presenters

import org.junit.Before
import org.junit.Test
import org.junit.Assert.fail

/**
 * Created by HuyND on 5/25/2019.
 */

class PriceOneDayPresenterTest {
    private lateinit var mPresenter: PriceOneDayPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mPresenter = PriceOneDayPresenterImpl(null, null)
    }

    @Test
    fun onBtnGetPricesClickShouldDoNothingIfViewIsNull() {
        try {
            mPresenter.getPrices(2017, 10, 0, "SGN", "HAN", true)
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }

    @Test
    fun onResponseShouldDoNothingIfViewIsNull() {
        try {
            (mPresenter as PriceOneDayPresenterImpl).onGetPricesResponse(null, true)
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }
}
