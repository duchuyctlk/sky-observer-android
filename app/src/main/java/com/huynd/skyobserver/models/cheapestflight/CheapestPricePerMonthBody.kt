package com.huynd.skyobserver.models.cheapestflight

import com.huynd.skyobserver.utils.Constants.Companion.CARRIERS

/**
 * Created by HuyND on 11/18/2017.
 */
class CheapestPricePerMonthBody(strYear: String, strMonth: String, strDay: String) {
    var startDate: String = ""
    var endDate: String = ""
    val minPrice = 0
    val maxPrice = 900000
    val providers = CARRIERS.asList()
    val routes: MutableList<String> = mutableListOf()
    val type = "date"

    init {
        startDate = "$strYear$strMonth$strDay"
        endDate = startDate
    }

    fun setRoutes(routes: List<String>) {
        this.routes.clear()
        this.routes.addAll(routes)
    }
}
