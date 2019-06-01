package com.huynd.skyobserver.presenters

import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenter
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

/**
 * Created by HuyND on 6/1/2019.
 */
class FlightWithCheapestPriceResultPresenterTest {
    private lateinit var mPresenter: FlightWithCheapestPriceResultPresenter
    private lateinit var mView: FlightWithCheapestPriceResultView

    @Before
    fun setUp() {
        mView = mock(FlightWithCheapestPriceResultView::class.java)
        mPresenter = FlightWithCheapestPriceResultPresenterImpl(mView, mock(PricesAPI::class.java))
    }

    @Test
    fun mViewShouldShowInvalidDateDialogWhenDatesAreInvalid() {
        val cal = Calendar.getInstance()
        val year = cal.get(YEAR)
        val month = cal.get(MONTH)
        val day = cal.get(DAY_OF_MONTH)

        // invalid year
        Mockito.clearInvocations(mView)
        mPresenter.getPrices(
                2000, month, day,
                2000, month, day,
                "SGN", true
        )
        verify(mView, times(1)).showInvalidDateDialog()

        // invalid month
        Mockito.clearInvocations(mView)
        mPresenter.getPrices(
                year, 1, day,
                year, 1, day,
                "SGN", true
        )
        verify(mView, times(1)).showInvalidDateDialog()

        // invalid day
        Mockito.clearInvocations(mView)
        mPresenter.getPrices(
                year, month, 1,
                year, month, 1,
                "SGN", true
        )
        verify(mView, times(1)).showInvalidDateDialog()
    }
}
