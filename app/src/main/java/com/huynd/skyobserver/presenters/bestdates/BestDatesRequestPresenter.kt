package com.huynd.skyobserver.presenters.bestdates

/**
 * Created by HuyND on 6/17/2019.
 */

interface BestDatesRequestPresenter {
    fun initSpinnersValues()

    fun getPrices(srcPort: String, destPort: String, isReturnTrip: Boolean, tripLength: Int)
}
