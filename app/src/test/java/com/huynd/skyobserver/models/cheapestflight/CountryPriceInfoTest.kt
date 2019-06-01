package com.huynd.skyobserver.models.cheapestflight

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Created by HuyND on 5/30/2019.
 */
class CountryPriceInfoTest {
    private lateinit var countryPriceInfoWithoutListOfAirportPriceInfo: CountryPriceInfo
    private lateinit var countryPriceInfoWithEmptyListOfAirportPriceInfo: CountryPriceInfo

    @Before
    fun setUp() {
        countryPriceInfoWithoutListOfAirportPriceInfo = CountryPriceInfo()
        countryPriceInfoWithEmptyListOfAirportPriceInfo = CountryPriceInfo().apply {
            airportPriceInfos = listOf()
        }
    }

    @Test
    fun airportPriceInfoCountShouldReturnZero() {
        assertEquals(0, countryPriceInfoWithoutListOfAirportPriceInfo.airportPriceInfoCount)
        assertEquals(0, countryPriceInfoWithEmptyListOfAirportPriceInfo.airportPriceInfoCount)
    }

    @Test
    fun getAirportPriceInfoShouldReturnNull() {
        assertNull(countryPriceInfoWithoutListOfAirportPriceInfo.getAirportPriceInfo(0))
        assertNull(countryPriceInfoWithEmptyListOfAirportPriceInfo.getAirportPriceInfo(0))
        assertNull(countryPriceInfoWithEmptyListOfAirportPriceInfo.getAirportPriceInfo(-1))
    }

    @Test
    fun bestPriceTotalShouldReturnZero() {
        assertEquals(0, countryPriceInfoWithoutListOfAirportPriceInfo.bestPriceTotal)
        assertEquals(0, countryPriceInfoWithEmptyListOfAirportPriceInfo.bestPriceTotal)
    }
}