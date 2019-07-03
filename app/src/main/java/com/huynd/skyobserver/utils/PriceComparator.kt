package com.huynd.skyobserver.utils

import com.huynd.skyobserver.entities.PricePerDay
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.AIRLINES
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.DEPART_EARLIEST
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.DEPART_LATEST
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.PRICE_ONLY_HIGHEST
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.PRICE_ONLY_LOWEST
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.TOTAL_PRICE_HIGHEST
import com.huynd.skyobserver.utils.PriceComparator.SortOrder.TOTAL_PRICE_LOWEST
import java.util.*

/**
 * Created by HuyND on 10/11/2017.
 */

class PriceComparator : Comparator<PricePerDay> {
    enum class SortOrder {
        PRICE_ONLY_LOWEST,
        PRICE_ONLY_HIGHEST,
        TOTAL_PRICE_LOWEST,
        TOTAL_PRICE_HIGHEST,
        DEPART_EARLIEST,
        DEPART_LATEST,
        AIRLINES,
        UNKNOWN
    }

    companion object {
        val instance = PriceComparator()
    }

    private var mOrder = DEPART_EARLIEST

    override fun compare(price1: PricePerDay?, price2: PricePerDay?): Int {
        if (price1 == null && price2 == null) {
            return 0
        }

        if (price1 == null) {
            return -1
        }

        if (price2 == null) {
            return 1
        }

        return when (mOrder) {
            PRICE_ONLY_LOWEST -> price1.price.compareTo(price2.price)
            PRICE_ONLY_HIGHEST -> price2.price.compareTo(price1.price)
            TOTAL_PRICE_LOWEST -> price1.priceTotal.compareTo(price2.priceTotal)
            TOTAL_PRICE_HIGHEST -> price2.priceTotal.compareTo(price1.priceTotal)
            DEPART_EARLIEST -> compareTwoNullableDate(price1.getDepartureTime(), price2.getDepartureTime())
            DEPART_LATEST -> compareTwoNullableDate(price2.getDepartureTime(), price1.getDepartureTime())
            AIRLINES -> price1.carrier.compareTo(price2.carrier)
            else -> 0
        }
    }

    private fun compareTwoNullableDate(date1: Date?, date2: Date?): Int {
        return if (date1 == null && date2 == null) {
            0
        } else if (date1 == null) {
            -1
        } else if (date2 == null) {
            1
        } else {
            date1.compareTo(date2)
        }
    }

    fun setSortOrder(order: SortOrder) {
        mOrder = order
    }
}
