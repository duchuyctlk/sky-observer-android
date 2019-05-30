package com.huynd.skyobserver.presenters.cheapestflight;

/**
 * Created by HuyND on 4/21/2018.
 */

interface FlightWithCheapestPriceResultPresenter {
    fun getPrices(yearOutbound: Int, monthOutbound: Int, dayOutbound: Int,
                  yearInbound: Int, monthInbound: Int, dayInbound: Int,
                  port: String, isReturnTrip: Boolean)
}
