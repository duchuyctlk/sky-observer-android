package com.huynd.skyobserver.fragments

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import com.google.gson.Gson
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverAndroidTestApp
import com.huynd.skyobserver.activities.MainActivity
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.entities.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.FileUtils.getStringFromAssets
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by HuyND on 6/29/2019.
 */

class BestDatesFragmentAndroidTest {
    private val outbound_response = "best_dates_outbound_best_price_per_day_code_200_ok_response.json"
    private val inbound_response = "best_dates_inbound_best_price_per_day_code_200_ok_response.json"
    private val month_response = "best_dates_month_best_price_code_200_ok_response.json"
    private val code_404_not_found = "code_404_not_found.json"

    @Rule
    @JvmField
    var mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, true)

    private lateinit var mActivity: MainActivity

    lateinit var mPricesAPI: PricesAPI
        @Inject set

    @Before
    fun setUp() {
        mActivity = mActivityTestRule.activity
        val app = mActivity.application as SkyObserverAndroidTestApp
        val component = app.skyObserverComponent as SkyObserverComponentAndroidTest
        component.inject(this)

        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click())
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(4).perform(click())
    }

    @Test
    fun shouldContainViewWidgets() {
        checkViewWidgetsIsDisplayed(
                R.id.spinner_src_port,
                R.id.spinner_dest_port,
                R.id.chk_return_trip,
                R.id.spinner_trip_length,
                R.id.btn_find_flights
        )
    }

    @Test
    fun shouldSendReturnRequestAndDisplayResponse() {
        mockApiResponse(true, true, true, "SGN")

        // find flight
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
        onData(anything()).inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).perform(click())
        onData(allOf(`is`(instanceOf(List::class.java))))
                .inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).check(matches(isDisplayed()))
        onData(allOf(`is`(instanceOf(BestDatesInfo::class.java))))
                .inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).check(matches(isDisplayed()))
    }

    @Test
    fun shouldSendReturnRequestAndHandleEmptyMonthResponse() {
        mockApiResponse(true, false, false, "SGN")

        // find flight
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
    }

    @Test
    fun shouldSendReturnRequestAndHandleEmptyDayResponse() {
        mockApiResponse(true, true, false, "SGN")

        // find flight
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
    }

    @Test
    fun shouldSendOneWayRequestAndDisplayResponse() {
        mockApiResponse(true, true, true, "SGN")

        // find flight
        onView(withId(R.id.chk_return_trip)).perform(click())
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
        onData(anything()).inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).perform(click())
        onData(allOf(`is`(instanceOf(List::class.java))))
                .inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).check(matches(isDisplayed()))
        onData(allOf(`is`(instanceOf(BestDatesInfo::class.java))))
                .inAdapterView(withId(R.id.lst_best_dates)).atPosition(0).check(matches(isDisplayed()))
    }

    @Test
    fun shouldSendOneWayRequestAndHandleEmptyMonthResponse() {
        mockApiResponse(true, false, false, "SGN")

        // find flight
        onView(withId(R.id.chk_return_trip)).perform(click())
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
    }

    @Test
    fun shouldSendOneWayRequestAndHandleEmptyDayResponse() {
        mockApiResponse(true, true, false, "SGN")

        // find flight
        onView(withId(R.id.chk_return_trip)).perform(click())
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_dates)
    }

    private fun checkViewWidgetsIsDisplayed(vararg ids: Int) {
        ids.forEach { id ->
            onView(withId(id)).check(matches(isDisplayed()))
        }
    }

    @Throws(Exception::class)
    private fun mockApiResponse(requestSuccess: Boolean,
                                monthResponseSuccess: Boolean,
                                dayResponseSuccess: Boolean,
                                srcPort: String) {
        if (requestSuccess) {
            val gson = Gson()
            val assetManager = getInstrumentation().context.assets
            val targetClass = Array<PricePerDayResponse>::class.java
            val targetMonthClass = Array<CheapestPricePerMonthResponse>::class.java

            if (dayResponseSuccess) {
                val outboundDay: List<PricePerDayResponse>
                val inboundODay: List<PricePerDayResponse>

                val outRes = gson.fromJson(getStringFromAssets(assetManager, outbound_response), targetClass)
                outboundDay = outRes.toList()

                val inRes = gson.fromJson(getStringFromAssets(assetManager, inbound_response), targetClass)
                inboundODay = inRes.toList()

                mock<PricesAPI> {
                    runBlocking {
                        `when`(mPricesAPI.getListPricePerDay(
                                anyMap(),
                                any(),
                                any(),
                                eq(srcPort),
                                any()
                        )).thenReturn(outboundDay)

                        `when`(mPricesAPI.getListPricePerDay(
                                anyMap(),
                                any(),
                                any(),
                                any(),
                                eq(srcPort)
                        )).thenReturn(inboundODay)
                    }
                }
            } else {
                val resBody = ResponseBody.create(
                        null,
                        getStringFromAssets(getInstrumentation().context.assets, code_404_not_found)
                )
                val responseDay: Response<List<PricePerDayResponse>> = Response.error(404, resBody)
                val dayException = HttpException(responseDay)

                mock<PricesAPI> {
                    runBlocking {
                        `when`(mPricesAPI.getListPricePerDay(
                                anyMap(),
                                any(),
                                any(),
                                eq(srcPort),
                                any()
                        )).thenThrow(dayException)

                        `when`(mPricesAPI.getListPricePerDay(
                                anyMap(),
                                any(),
                                any(),
                                any(),
                                eq(srcPort)
                        )).thenThrow(dayException)
                    }
                }
            }

            if (monthResponseSuccess) {
                val monthList: List<CheapestPricePerMonthResponse>

                val monthRes = gson.fromJson(getStringFromAssets(assetManager, month_response), targetMonthClass)
                monthList = monthRes.toList()

                mock<PricesAPI> {
                    runBlocking {
                        Mockito.`when`(mPricesAPI.getListCheapestPricePerMonth(
                                anyMap(),
                                any()
                        )).thenReturn(monthList)
                    }
                }
            } else {
                val resBody = ResponseBody.create(
                        null,
                        getStringFromAssets(getInstrumentation().context.assets, code_404_not_found)
                )
                val responseMonth: Response<List<CheapestPricePerMonthResponse>> = Response.error(404, resBody)

                val monthException = HttpException(responseMonth)

                mock<PricesAPI> {
                    runBlocking {
                        `when`(mPricesAPI.getListCheapestPricePerMonth(
                                anyMap(),
                                any()
                        )).thenThrow(monthException)
                    }
                }
            }
        } else {
            val exception = Exception("Exception")

            mock<PricesAPI> {
                runBlocking {
                    `when`(mPricesAPI.getListPricePerDay(
                            anyMap(),
                            any(),
                            any(),
                            eq(srcPort),
                            any()
                    )).thenThrow(exception)

                    `when`(mPricesAPI.getListPricePerDay(
                            anyMap(),
                            any(),
                            any(),
                            any(),
                            eq(srcPort)
                    )).thenThrow(exception)

                    Mockito.`when`(mPricesAPI.getListCheapestPricePerMonth(
                            anyMap(),
                            any()
                    )).thenThrow(exception)
                }
            }
        }
    }
}
