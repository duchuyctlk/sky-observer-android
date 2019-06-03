package com.huynd.skyobserver.models.cheapestflight

import com.huynd.skyobserver.utils.Constants.Companion.CARRIERS

/**
 * Created by HuyND on 11/18/2017.
 */
data class CheapestPricePerMonthBody(val startDate: String, val endDate: String,
                                     val routes: List<String>) {
    val minPrice = 0
    val maxPrice = 900000
    val providers = CARRIERS.asList()
    val type = "date"
}
