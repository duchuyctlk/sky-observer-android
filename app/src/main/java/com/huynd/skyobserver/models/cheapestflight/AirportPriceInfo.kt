package com.huynd.skyobserver.models.cheapestflight

import android.annotation.SuppressLint
import android.os.Parcelable
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDay
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by HuyND on 9/28/2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
class AirportPriceInfo(
        private var mAirport: Airport? = null,
        private var mPricePerDayOutbound: PricePerDay? = null,
        private var mPricePerDayInbound: PricePerDay? = null
) : Parcelable {

    fun getAirportName(): String = mAirport?.toString() ?: ""

    fun getAirportId() = mAirport?.id ?: ""

    fun getBestPriceOutbound() = mPricePerDayOutbound?.priceTotal ?: 0.0

    fun getBestPriceInbound() = mPricePerDayInbound?.priceTotal ?: 0.0

    fun getBestPriceTotal() = getBestPriceOutbound() + getBestPriceInbound()

    fun getOutboundCarrier() = mPricePerDayOutbound?.carrier

    fun getInboundCarrier() = mPricePerDayInbound?.carrier

    fun getOutboundDepartureTime(): Date = mPricePerDayOutbound?.getDepartureTime() ?: Date()

    fun getInboundDepartureTime(): Date = mPricePerDayInbound?.getDepartureTime() ?: Date()

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
