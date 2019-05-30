package com.huynd.skyobserver.views.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.views.BaseView;

/**
 * Created by HuyND on 4/21/2018.
 */

interface FlightWithCheapestPriceResultView : BaseView {
    fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>)

    fun showInvalidDateDialog()
}
