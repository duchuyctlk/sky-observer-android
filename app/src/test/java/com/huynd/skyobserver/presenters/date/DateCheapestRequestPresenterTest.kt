package com.huynd.skyobserver.presenters.date

import com.huynd.skyobserver.presenters.cheapestflight.date.DateCheapestRequestPresenter
import com.huynd.skyobserver.presenters.cheapestflight.date.DateCheapestRequestPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.date.DateCheapestRequestView
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * Created by HuyND on 6/8/2019.
 */
class DateCheapestRequestPresenterTest {
    private lateinit var mPresenter: DateCheapestRequestPresenter
    private lateinit var mView: DateCheapestRequestView

    @Before
    fun setUp() {
        mView = Mockito.mock(DateCheapestRequestView::class.java)
        mPresenter = DateCheapestRequestPresenterImpl(mView, Mockito.mock(PricesAPI::class.java))
    }

    @Test
    fun mViewShouldShowInvalidDateDialog() {
        (mPresenter as DateCheapestRequestPresenterImpl).notifyInvalidDate()
        verify(mView, times(1)).showInvalidDateDialog()
    }
}
