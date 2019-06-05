package com.huynd.skyobserver.views.cheapestflight

import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 11/19/2017.
 */

interface FlightWithCheapestPriceRequestView : BaseView {

    fun updateAirports(airports: List<Airport>)

    fun updateDatePickers(startYear: Int, startMonth: Int, startDayOfMonth: Int)

    fun updateDateToEditText(dateAsString: String, isOutbound: Boolean)

    fun setDatePickersMinDate(minDate: Long)

    fun showInvalidDateDialog()

    fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>)
}
