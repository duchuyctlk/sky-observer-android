package com.huynd.skyobserver.views.bestdates

import com.huynd.skyobserver.models.Airport

/**
 * Created by HuyND on 6/17/2019.
 */

interface BestDatesRequestView {
    fun updateAirports(airports: List<Airport>)

    fun updatePossibleTripLength()
}
