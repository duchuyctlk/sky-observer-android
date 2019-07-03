package com.huynd.skyobserver.utils

import com.huynd.skyobserver.entities.bestdates.BestDatesInfo

/**
 * Created by HuyND on 6/29/2019.
 */

class BestDatesInfoComparator : Comparator<BestDatesInfo> {
    companion object {
        val instance = BestDatesInfoComparator()
    }
    override fun compare(object1: BestDatesInfo?,
                         object2: BestDatesInfo?): Int {
        if (object1 == null && object2 == null) {
            return 0
        }
        if (object1 == null) {
            return -1
        }
        if (object2 == null) {
            return 1
        }

        val year1 = object1.outboundId.year
        val year2 = object2.outboundId.year
        if (year1 == year2) {
            val month1 = object1.outboundId.monthInYear
            val month2 = object2.outboundId.monthInYear
            return month1.compareTo(month2)
        }

        return year1.compareTo(year2)
    }
}
