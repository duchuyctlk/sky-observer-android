package com.huynd.skyobserver.presenters.cheapestflight.month

import com.huynd.skyobserver.models.cheapestflight.month.MonthCheapestModel
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.DateUtils
import com.huynd.skyobserver.views.date.DateCheapestRequestView

/**
 * Created by HuyND on 11/19/2017.
 */

class MonthCheapestRequestPresenterImpl(
        private val mView: DateCheapestRequestView,
        private val mPricesAPI: PricesAPI) : MonthCheapestRequestPresenter,
        MonthCheapestModel.EventListener {

    private val mModel = MonthCheapestModel()

    init {
        mModel.setEventListener(this)
    }

    override fun initSpinnersValues() {
        val year = DateUtils.getStartYear()
        val month = DateUtils.getStartMonth()
        val dayOfMonth = DateUtils.getStartDayOfMonth()
        val dateAsString = DateUtils.dateToString(year, month)

        mView.run {
            updateAirports(mModel.getAirports())
            updateDatePickers(year, month, dayOfMonth)
            updateDateToEditText(dateAsString, true)
            updateDateToEditText(dateAsString, false)
            setDatePickersMinDate(DateUtils.getMinDate())
        }
    }

    override fun setDateToEditText(year: Int, month: Int, isOutbound: Boolean) {
        mView.updateDateToEditText(DateUtils.dateToString(year, month), isOutbound)
    }

    override fun getPrices(yearOutbound: Int, monthOutbound: Int,
                           yearInbound: Int, monthInbound: Int,
                           port: String, isReturnTrip: Boolean) {
        mView.showLoadingDialog()
        mModel.getPrices(mPricesAPI,
                yearOutbound, monthOutbound,
                yearInbound, monthInbound,
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
