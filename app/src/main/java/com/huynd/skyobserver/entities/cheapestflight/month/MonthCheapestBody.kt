package com.huynd.skyobserver.entities.cheapestflight.month

import com.huynd.skyobserver.utils.Constants.Companion.CARRIERS

/**
 * Created by HuyND on 11/18/2017.
 */
open class MonthCheapestBody() {
    var startDate: String = ""
    var endDate: String = ""
    val minPrice = 0
    val maxPrice = 900000
    val providers = CARRIERS.asList()
    val routes: MutableList<String> = mutableListOf()
    val type = "date"

    constructor(strYear: String, strMonth: String, strDay: String) : this() {
        startDate = "$strYear$strMonth$strDay"
        endDate = startDate
    }

    fun setRoutes(routes: List<String>) {
        this.routes.clear()
        this.routes.addAll(routes)
    }
}
