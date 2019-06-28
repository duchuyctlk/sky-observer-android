package com.huynd.skyobserver.utils

import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse

/**
 * Created by HuyND on 6/27/2019.
 */

class CheapestPricePerMonthResponseComparator : Comparator<CheapestPricePerMonthResponse> {
    override fun compare(object1: CheapestPricePerMonthResponse?,
                         object2: CheapestPricePerMonthResponse?): Int {
        if (object1 == null && object2 == null) {
            return 0
        }
        if (object1 == null) {
            return -1
        }
        if (object2 == null) {
            return 1
        }

        val year1 = object1.id.year
        val year2 = object2.id.year
        if (year1 == year2) {
            val month1 = object1.id.monthInYear
            val month2 = object2.id.monthInYear
            return month1.compareTo(month2)
        }

        return year1.compareTo(year2)
    }
}
