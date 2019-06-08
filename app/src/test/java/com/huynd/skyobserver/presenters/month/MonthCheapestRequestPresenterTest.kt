package com.huynd.skyobserver.presenters.month

import com.huynd.skyobserver.presenters.cheapestflight.date.DateCheapestResultPresenterImpl
import com.huynd.skyobserver.presenters.cheapestflight.month.MonthCheapestRequestPresenter
import com.huynd.skyobserver.presenters.cheapestflight.month.MonthCheapestRequestPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.date.DateCheapestRequestView
import com.huynd.skyobserver.views.date.DateCheapestResultView
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*

/**
 * Created by HuyND on 6/8/2019.
 */
class MonthCheapestRequestPresenterTest {
    private lateinit var mPresenter: MonthCheapestRequestPresenter
    private lateinit var mView: DateCheapestRequestView

    @Before
    fun setUp() {
        mView = Mockito.mock(DateCheapestRequestView::class.java)
        mPresenter = MonthCheapestRequestPresenterImpl(mView, Mockito.mock(PricesAPI::class.java))
    }

    @Test
    fun mViewShouldShowInvalidDateDialogWhenDatesAreInvalid() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)

        // invalid year
        Mockito.clearInvocations(mView)
        mPresenter.getPrices(
                2000, month,
                2000, month,
                "SGN", true
        )
        Mockito.verify(mView, Mockito.times(1)).showInvalidDateDialog()

        // invalid month
        Mockito.clearInvocations(mView)
        mPresenter.getPrices(
                year, 1,
                year, 1,
                "SGN", true
        )
        Mockito.verify(mView, Mockito.times(1)).showInvalidDateDialog()
    }
}