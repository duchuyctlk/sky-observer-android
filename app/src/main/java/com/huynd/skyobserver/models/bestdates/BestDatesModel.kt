package com.huynd.skyobserver.models.bestdates

import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.utils.CountryAirportUtils

/**
 * Created by HuyND on 6/18/2019.
 */
class BestDatesModel {
    interface EventListener {
        fun onGetPricesResponse(result: Any)
    }

    private var mListener: EventListener? = null
    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }

    fun getAirports(): List<Airport> = mAirports

    fun getPrices(srcPort: String, destPort: String) {
        // TODO
        mListener?.onGetPricesResponse(Any())
    }
}
