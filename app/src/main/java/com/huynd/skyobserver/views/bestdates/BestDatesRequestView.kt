package com.huynd.skyobserver.views.bestdates

import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 6/17/2019.
 */

interface BestDatesRequestView : BaseView {
    fun updateAirports(airports: List<Airport>)

    fun updatePossibleTripLength()

    fun updateListView(data: List<BestDatesInfo>)
}
