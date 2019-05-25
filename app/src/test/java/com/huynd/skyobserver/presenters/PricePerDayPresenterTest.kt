package com.huynd.skyobserver.presenters

import com.huynd.skyobserver.models.PricePerDayModel
import org.junit.Before
import org.junit.Test
import org.junit.Assert.fail

/**
 * Created by HuyND on 5/25/2019.
 */

class PricePerDayPresenterTest {
    private lateinit var mPresenter: PricePerDayPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mPresenter = PricePerDayPresenterImpl(null, null)
    }

    @Test
    fun onBtnGetPricesClickShouldDoNothingIfViewIsNull() {
        try {
            mPresenter.onBtnGetPricesClick(2017, 10, "SGN", "HAN")
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }

    @Test
    fun onResponseShouldDoNothingIfViewIsNull() {
        try {
            (mPresenter as PricePerDayModel.PricePerDayModelEventListener).onGetPricesResponse(null)
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }

    @Test
    fun initSpinnersValuesShouldDoNothingIfViewIsNull() {
        try {
            mPresenter.initSpinnersValues()
        } catch (e: Exception) {
            fail("Unexpected behavior happened.")
        }
    }
}
