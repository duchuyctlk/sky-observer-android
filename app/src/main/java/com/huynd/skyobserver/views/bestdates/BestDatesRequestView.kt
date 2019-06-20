package com.huynd.skyobserver.views.bestdates

import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 6/17/2019.
 */

interface BestDatesRequestView : BaseView {
    fun updateAirports(airports: List<Airport>)

    fun updatePossibleTripLength()

    fun updateListView(data: Any)
}
