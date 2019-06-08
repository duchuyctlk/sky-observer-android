package com.huynd.skyobserver.models

import android.os.Parcel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * Created by HuyND on 6/8/2019.
 */

@RunWith(RobolectricTestRunner::class)
class CountryTest {
    @Test
    fun checkCountryIsParcelable() {
        val country = Country("vn", "Vietnam")
        val parcel = Parcel.obtain()
        country.writeToParcel(parcel, country.describeContents())
        parcel.setDataPosition(0)

        val countryCreatedFromParcel = Country.CREATOR.createFromParcel(parcel) as Country
        assertEquals(country.countryCode, countryCreatedFromParcel.countryCode)
        assertEquals(country.countryName, countryCreatedFromParcel.countryName)
    }

    @Test
    fun checkCountryCreatorCreateNewArray() {
        val countryCreatedFromParcel = Country.CREATOR.newArray(10)
        assertEquals(10, countryCreatedFromParcel.size)
    }
}
