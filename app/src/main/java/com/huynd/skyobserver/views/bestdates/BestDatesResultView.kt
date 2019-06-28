package com.huynd.skyobserver.views.bestdates;

import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 4/21/2018.
 */

interface BestDatesResultView : BaseView {
    fun updateListViewData(outData: List<CheapestPricePerMonthResponse>, inData: List<CheapestPricePerMonthResponse>)
}
