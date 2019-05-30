package com.huynd.skyobserver.presenters.cheapestflight

import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceResultModel
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView

/**
 * Created by HuyND on 4/21/2018.
 */

class FlightWithCheapestPriceResultPresenterImpl(
        private val mView: FlightWithCheapestPriceResultView,
        private val mPricesAPI: PricesAPI) : FlightWithCheapestPriceResultPresenter,
        FlightWithCheapestPriceResultModel.EventListener {
    private val mModel = FlightWithCheapestPriceResultModel()

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
