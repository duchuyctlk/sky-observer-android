package com.huynd.skyobserver.presenters.cheapestflight.date

import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.models.cheapestflight.date.DateCheapestModel
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.DateUtils
import com.huynd.skyobserver.views.date.DateCheapestRequestView

/**
 * Created by HuyND on 11/19/2017.
 */

class DateCheapestRequestPresenterImpl(
        private val mView: DateCheapestRequestView,
        private val mPricesAPI: PricesAPI) : DateCheapestRequestPresenter,
        DateCheapestModel.EventListener {

    private val mModel = DateCheapestModel()

    init {
        mModel.setEventListener(this)
    }

    override fun initSpinnersValues() {
        val year = DateUtils.getStartYear()
        val month = DateUtils.getStartMonth()
        val dayOfMonth = DateUtils.getStartDayOfMonth()
        val dateAsString = DateUtils.dateToString(year, month, dayOfMonth)

        mView.run {
            updateAirports(mModel.getAirports())
            updateDatePickers(year, month, dayOfMonth)
            updateDateToEditText(dateAsString, true)
            updateDateToEditText(dateAsString, false)
            setDatePickersMinDate(DateUtils.getMinDate())
        }
    }

    override fun setDateToEditText(year: Int, month: Int, dayOfMonth: Int, isOutbound: Boolean) {
        mView.updateDateToEditText(DateUtils.dateToString(year, month, dayOfMonth), isOutbound)
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
        mView.run {
            dismissLoadingDialog()
            showInvalidDateDialog()
        }
    }

    override fun onGetPricesResponse(listCountryPriceInfo: List<CountryPriceInfo>) {
        mView.run {
            updateListViewInboundPrices(listCountryPriceInfo)
            dismissLoadingDialog()
        }
    }
}
