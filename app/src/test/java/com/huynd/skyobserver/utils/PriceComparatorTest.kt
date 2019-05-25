package com.huynd.skyobserver.utils

import com.huynd.skyobserver.models.PricePerDay
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
        mPriceComparator = PriceComparator.getInstance()
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
}
