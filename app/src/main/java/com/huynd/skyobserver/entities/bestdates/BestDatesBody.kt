package com.huynd.skyobserver.entities.bestdates

import com.huynd.skyobserver.entities.cheapestflight.month.MonthCheapestBody

/**
 * Created by HuyND on 06/25/2019.
 */
class BestDatesBody(startDate: String, endDate: String): MonthCheapestBody() {
    init {
        this.startDate = startDate
        this.endDate = endDate
    }
}
