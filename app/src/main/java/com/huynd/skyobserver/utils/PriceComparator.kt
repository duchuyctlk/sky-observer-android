package com.huynd.skyobserver.utils

import com.huynd.skyobserver.models.PricePerDay

import java.util.Comparator

import com.huynd.skyobserver.utils.PriceComparator.SortOrder.*

/**
 * Created by HuyND on 10/11/2017.
 */

class PriceComparator: Comparator<PricePerDay> {
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
            PRICE_ONLY_LOWEST -> price1.price - price2.price
            PRICE_ONLY_HIGHEST -> price2.price - price1.price
            TOTAL_PRICE_LOWEST -> price1.priceTotal - price2.priceTotal
            TOTAL_PRICE_HIGHEST -> price2.priceTotal - price1.priceTotal
            DEPART_EARLIEST -> price1.departureTime.compareTo(price2.departureTime)
            DEPART_LATEST -> price2.departureTime.compareTo(price1.departureTime)
            AIRLINES -> price1.carrier.compareTo(price2.carrier)
            else -> 0
        }
    }

    fun setSortOrder(order: SortOrder) {
        mOrder = order
    }
}
