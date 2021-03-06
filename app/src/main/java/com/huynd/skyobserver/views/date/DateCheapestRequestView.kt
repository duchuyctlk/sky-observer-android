package com.huynd.skyobserver.views.date

import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 11/19/2017.
 */

interface DateCheapestRequestView : BaseView {

    fun updateAirports(airports: List<Airport>)

    fun updateDatePickers(startYear: Int, startMonth: Int, startDayOfMonth: Int)

    fun updateDateToEditText(dateAsString: String, isOutbound: Boolean)

    fun setDatePickersMinDate(minDate: Long)

    fun showInvalidDateDialog()

    fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>)
}
