package com.huynd.skyobserver.presenters.cheapestflight.date

import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.models.cheapestflight.date.DateCheapestModel
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.date.DateCheapestResultView

/**
 * Created by HuyND on 4/21/2018.
 */

class DateCheapestResultPresenterImpl(
        private val mView: DateCheapestResultView,
        private val mPricesAPI: PricesAPI) : DateCheapestResultPresenter,
        DateCheapestModel.EventListener {
    private val mModel = DateCheapestModel()

    init {
        mModel.setEventListener(this)
    }

    override fun getPrices(yearOutbound: Int, monthOutbound: Int, dayOutbound: Int,
                           yearInbound: Int, monthInbound: Int, dayInbound: Int,
                           port: String, isReturnTrip: Boolean) {
        mView.showLoadingDialog()
        mModel.getPrices(mPricesAPI,
                yearOutbound, monthOutbound, dayOutbound,
                yearInbound, monthInbound, dayInbound,
                port, isReturnTrip)
    }

    override fun notifyInvalidDate() {
        mView.showInvalidDateDialog()
    }

    override fun onGetPricesResponse(listCountryPriceInfo: List<CountryPriceInfo>) {
        mView.run {
            updateListViewInboundPrices(listCountryPriceInfo)
            dismissLoadingDialog()
        }
    }
}
