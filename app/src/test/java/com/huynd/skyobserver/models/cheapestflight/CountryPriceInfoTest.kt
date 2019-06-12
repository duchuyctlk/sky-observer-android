package com.huynd.skyobserver.models.cheapestflight

import android.os.Parcel
import com.huynd.skyobserver.models.Country
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Created by HuyND on 5/30/2019.
 */

@RunWith(RobolectricTestRunner::class)
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
    @Test
    fun checkCountryIsParcelable() {
        val country = Country("vn", "Vietnam")
        val info = CountryPriceInfo().apply {
            setCountry(country)
            airportPriceInfos = listOf()
        }
        val parcel = Parcel.obtain()
        info.writeToParcel(parcel, info.describeContents())
        parcel.setDataPosition(0)

        val infoCreatedFromParcel = CountryPriceInfo.CREATOR.createFromParcel(parcel) as CountryPriceInfo
        assertEquals(info.country.countryCode, infoCreatedFromParcel.country.countryCode)
        assertEquals(info.country.countryName, infoCreatedFromParcel.country.countryName)
        assertEquals(info.airportPriceInfos.size, infoCreatedFromParcel.airportPriceInfos.size)
    }

    @Test
    fun checkCountryCreatorCreateNewArray() {
        val countryPriceInfoCreatedFromParcel = CountryPriceInfo.CREATOR.newArray(10)
        assertEquals(10, countryPriceInfoCreatedFromParcel.size)
    }
}
