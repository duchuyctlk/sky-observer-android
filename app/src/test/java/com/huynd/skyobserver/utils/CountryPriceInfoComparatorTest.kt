package com.huynd.skyobserver.utils

import com.huynd.skyobserver.entities.PricePerDay
import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by HuyND on 7/1/2019.
 */

class CountryPriceInfoComparatorTest {
    private lateinit var mComparator: CountryPriceInfoComparator
    private lateinit var mCountryPriceInfo: CountryPriceInfo

    @Before
    fun setUp() {
        mComparator = CountryPriceInfoComparator.getInstance()
        val airportPriceInfo = AirportPriceInfo()
        airportPriceInfo.setPricePerDayOutbound(PricePerDay(priceTotal = 100.0))
        mCountryPriceInfo = CountryPriceInfo()
        mCountryPriceInfo.airportPriceInfos = listOf(airportPriceInfo)
    }

    @Test
    fun testComparing2NullValues() {
        assertEquals(0, mComparator.compare(null, null))
    }

    @Test
    fun testComparingNullValueAgainstObject() {
        assertEquals(-1, mComparator.compare(null, mCountryPriceInfo))
        assertEquals(1, mComparator.compare(mCountryPriceInfo, null))
    }

    @Test
    fun testComparing2Objects() {
        val airportPriceInfo = AirportPriceInfo()
        airportPriceInfo.setPricePerDayOutbound(PricePerDay(priceTotal = 100.0))
        val countryPriceInfo2 = CountryPriceInfo()
        countryPriceInfo2.airportPriceInfos = listOf(airportPriceInfo)
        assertEquals(0, mComparator.compare(mCountryPriceInfo, countryPriceInfo2))
    }
}
