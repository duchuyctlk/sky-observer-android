package com.huynd.skyobserver.utils

import com.huynd.skyobserver.BuildConfig
import java.lang.AssertionError

/**
 * Created by HuyND on 8/6/2017.
 */

class Constants {
    init {
        throw AssertionError()
    }
    
    companion object {
        const val API_URL = BuildConfig.API_URL
        const val ICON_URL = BuildConfig.ICON_URL
        const val HEADER_CONTENT_TYPE = BuildConfig.HEADER_CONTENT_TYPE

        @kotlin.jvm.JvmField
        var CARRIERS: Array<String> = arrayOf("VJ", "BL", "VN")

        const val CONVENIENCE_FEE_IN_K = 70

        const val MAX_TRIP_LENGTH = 10

        const val BUNDLE_KEY_FLIGHT_WITH_CHEAPEST_PRICE = "flightWithCheapestPrice"

        const val CHOOSE_ONE_DAY_FRAGMENT_SUFFIX_WITH_DST = "_CHOOSE_ONE_DAY_FRAGMENT_WITH_DST"
        const val CHOOSE_ONE_DAY_FRAGMENT_SUFFIX_WITHOUT_DST = "_CHOOSE_ONE_DAY_FRAGMENT_WITHOUT_DST"

        const val BEST_PRICE_DELTA = 100000.0
    }
}
