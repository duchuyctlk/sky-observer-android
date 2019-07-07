package com.huynd.skyobserver.views.date;

import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.views.BaseView;

/**
 * Created by HuyND on 4/21/2018.
 */

interface DateCheapestResultView : BaseView {
    fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>)

    fun showInvalidDateDialog()
}
