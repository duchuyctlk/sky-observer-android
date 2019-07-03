package com.huynd.skyobserver.utils

import com.huynd.skyobserver.entities.PricePerDay
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by HuyND on 10/12/2017.
 */

class PriceComparatorTest {
    private lateinit var mPriceComparator: PriceComparator
    private lateinit var mPricePerDay: PricePerDay

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mPriceComparator = PriceComparator.instance
        mPriceComparator.setSortOrder(PriceComparator.SortOrder.DEPART_EARLIEST)
        mPricePerDay = PricePerDay()
    }

    @Test
    fun testComparing2NullValues() {
        assertEquals(0, mPriceComparator.compare(null, null))
    }

    @Test
    fun testComparingNullValueAgainstObject() {
        assertEquals(-1, mPriceComparator.compare(null, mPricePerDay))
        assertEquals(1, mPriceComparator.compare(mPricePerDay, null))
    }

    @Test
    fun testDefaultFlow() {
        mPriceComparator.setSortOrder(PriceComparator.SortOrder.UNKNOWN)
        assertEquals(0, mPriceComparator.compare(PricePerDay(), mPricePerDay))
    }

    @Test
    fun testComparing2NullableDates() {
        val pricePerDay2 = PricePerDay()

        // 2 nulls
        pricePerDay2.setDepartureTime("06/06/2019")
        mPricePerDay.setDepartureTime("06/06/2019")
        assertEquals(0, mPriceComparator.compare(mPricePerDay, pricePerDay2))

        // object vs null
        mPricePerDay.setDepartureTime("2019-06-06T00:00:00.000Z")
        assertEquals(1, mPriceComparator.compare(mPricePerDay, pricePerDay2))

        // null vs object
        pricePerDay2.setDepartureTime("2019-06-06T00:00:00.000Z")
        mPricePerDay.setDepartureTime("06/06/2019")
        assertEquals(-1, mPriceComparator.compare(mPricePerDay, pricePerDay2))

        // 2 objects
        pricePerDay2.setDepartureTime("2019-06-06T00:00:00.000Z")
        mPricePerDay.setDepartureTime("2018-06-06T00:00:00.000Z")
        assertEquals(-1, mPriceComparator.compare(mPricePerDay, pricePerDay2))
    }
}
