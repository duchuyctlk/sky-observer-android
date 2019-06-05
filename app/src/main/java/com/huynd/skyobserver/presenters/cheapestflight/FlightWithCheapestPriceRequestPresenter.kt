package com.huynd.skyobserver.presenters.cheapestflight

/**
 * Created by HuyND on 11/19/2017.
 */

interface FlightWithCheapestPriceRequestPresenter {

    fun initSpinnersValues()

    fun setDateToEditText(year: Int, month: Int, dayOfMonth: Int, isOutbound: Boolean)

    fun getPrices(yearOutbound: Int, monthOutbound: Int, dayOutbound: Int,
                  yearInbound: Int, monthInbound: Int, dayInbound: Int,
                  port: String, isReturnTrip: Boolean)
}
