package com.huynd.skyobserver.views.bestdates;

import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 4/21/2018.
 */

interface BestDatesResultView : BaseView {
    fun updateListViewData(data: List<BestDatesInfo>)
}
