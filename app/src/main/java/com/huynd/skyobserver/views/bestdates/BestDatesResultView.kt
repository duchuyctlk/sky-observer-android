package com.huynd.skyobserver.views.bestdates;

import com.huynd.skyobserver.models.bestdates.BestDatesInfo
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 4/21/2018.
 */

interface BestDatesResultView : BaseView {
    fun updateListViewData(data: List<BestDatesInfo>)
}
