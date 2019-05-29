package com.huynd.skyobserver.models.cheapestflight

import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDay

/**
 * Created by HuyND on 9/28/2017.
 */

class AirportPriceInfo {
    private var mAirport: Airport? = null
    private var mPricePerDayOutbound: PricePerDay? = null
    private var mPricePerDayInbound: PricePerDay? = null

    fun getAirportName(): String = mAirport?.toString() ?: ""

    fun getAirportId() = mAirport?.id ?: ""


    fun getBestPriceOutbound() = mPricePerDayOutbound?.priceTotal ?: 0

    fun getBestPriceInbound() = mPricePerDayInbound?.priceTotal ?: 0

    fun getBestPriceTotal() = getBestPriceOutbound() + getBestPriceInbound()

    fun setAirport(airport: Airport) {
        mAirport = airport
    }

    fun setPricePerDayOutbound(pricePerDay: PricePerDay) {
        mPricePerDayOutbound = pricePerDay
    }

    fun setPricePerDayInbound(pricePerDay: PricePerDay) {
        mPricePerDayInbound = pricePerDay
    }
}
