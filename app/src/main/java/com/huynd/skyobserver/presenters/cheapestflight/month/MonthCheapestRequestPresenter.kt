package com.huynd.skyobserver.presenters.cheapestflight.month

/**
 * Created by HuyND on 11/19/2017.
 */

interface MonthCheapestRequestPresenter {
    fun initSpinnersValues()

    fun setDateToEditText(year: Int, month: Int, isOutbound: Boolean)

    fun getPrices(yearOutbound: Int, monthOutbound: Int,
                  yearInbound: Int, monthInbound: Int,
                  port: String, isReturnTrip: Boolean)
}
