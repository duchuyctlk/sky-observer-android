package com.huynd.skyobserver.fragments.cheapestflight

import android.os.Bundle

/**
 * Created by HuyND on 9/16/2017.
 */

interface CheapestFlightListener {
    fun showDateInfo(priceInfo: Bundle?)

    fun showMonthInfo(priceInfo: Bundle?)

    fun showBestDates(bundle: Bundle?)
}
