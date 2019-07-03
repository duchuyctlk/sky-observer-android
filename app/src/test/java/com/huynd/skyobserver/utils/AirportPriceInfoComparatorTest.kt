package com.huynd.skyobserver.utils

import com.huynd.skyobserver.entities.PricePerDay
import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by HuyND on 7/1/2019.
 */
class AirportPriceInfoComparatorTest {
    private lateinit var mComparator: AirportPriceInfoComparator
    private lateinit var mAirportPriceInfo: AirportPriceInfo

    @Before
    fun setUp() {
        mComparator = AirportPriceInfoComparator.getInstance()
        mAirportPriceInfo = AirportPriceInfo()
        mAirportPriceInfo.setPricePerDayOutbound(PricePerDay(priceTotal = 100.0))
    }

    @Test
    fun testComparing2NullValues() {
        assertEquals(0, mComparator.compare(null, null))
    }

    @Test
    fun testComparingNullValueAgainstObject() {
        assertEquals(-1, mComparator.compare(null, mAirportPriceInfo))
        assertEquals(1, mComparator.compare(mAirportPriceInfo, null))
    }

    @Test
    fun testComparing2Objects() {
        val airportPriceInfo2 = AirportPriceInfo()

        // same price
        airportPriceInfo2.setPricePerDayOutbound(PricePerDay(priceTotal = 100.0))
        assertEquals(0, mComparator.compare(mAirportPriceInfo, airportPriceInfo2))

        // different prices
        airportPriceInfo2.setPricePerDayOutbound(PricePerDay(priceTotal = 50.0))
        assertEquals(1, mComparator.compare(mAirportPriceInfo, airportPriceInfo2))
        airportPriceInfo2.setPricePerDayOutbound(PricePerDay(priceTotal = 150.0))
        assertEquals(-1, mComparator.compare(mAirportPriceInfo, airportPriceInfo2))
    }
}
