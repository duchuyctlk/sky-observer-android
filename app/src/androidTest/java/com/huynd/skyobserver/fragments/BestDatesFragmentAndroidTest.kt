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
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.models.bestdates.BestDatesInfo
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.models.cheapestflight.month.MonthCheapestBody
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.FileUtils.getStringFromAssets
import io.reactivex.Observable
import io.reactivex.Observable.just
import okhttp3.ResponseBody
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
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
        mockApiResponse(true, true, "SGN")

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
    fun shouldSendOneWayRequestAndDisplayResponse() {
        mockApiResponse(true, true, "SGN")

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

    private fun checkViewWidgetsIsDisplayed(vararg ids: Int) {
        ids.forEach { id ->
            onView(withId(id)).check(matches(isDisplayed()))
        }
    }

    @Throws(Exception::class)
    private fun mockApiResponse(requestSuccess: Boolean, responseSuccess: Boolean, srcPort: String) {
        val outboundDay: Observable<List<PricePerDayResponse>>
        val inboundODay: Observable<List<PricePerDayResponse>>
        val monthObservableList: Observable<List<CheapestPricePerMonthResponse>>

        if (requestSuccess) {
            val gson = Gson()
            val assetManager = getInstrumentation().context.assets
            val targetClass = Array<PricePerDayResponse>::class.java
            val targetMonthClass = Array<CheapestPricePerMonthResponse>::class.java

            if (responseSuccess) {
                val outRes = gson.fromJson(getStringFromAssets(assetManager, outbound_response), targetClass)
                outboundDay = just<List<PricePerDayResponse>>(outRes.toList())

                val inRes = gson.fromJson(getStringFromAssets(assetManager, inbound_response), targetClass)
                inboundODay = just<List<PricePerDayResponse>>(inRes.toList())

                val monthRes = gson.fromJson(getStringFromAssets(assetManager, month_response), targetMonthClass)
                monthObservableList = just<List<CheapestPricePerMonthResponse>>(monthRes.toList())
            } else {
                val responseDay: Response<List<PricePerDayResponse>> = Response.error(404,
                        ResponseBody.create(
                                null,
                                getStringFromAssets(getInstrumentation().context.assets, code_404_not_found)
                        ))
                val resBody = responseDay.body()!!

                val responseMonth: Response<List<CheapestPricePerMonthResponse>> = Response.error(404,
                        ResponseBody.create(
                                null,
                                getStringFromAssets(getInstrumentation().context.assets, code_404_not_found)
                        ))

                outboundDay = just<List<PricePerDayResponse>>(resBody)
                inboundODay = just<List<PricePerDayResponse>>(resBody)
                monthObservableList = just<List<CheapestPricePerMonthResponse>>(responseMonth.body())
            }
        } else {
            val exception = Exception("Exception")
            outboundDay = Observable.error(exception)
            inboundODay = Observable.error(exception)
            monthObservableList = Observable.error(exception)
        }

        Mockito.`when`(mPricesAPI.getCheapestPricePerMonth(
                anyMap(),
                any(MonthCheapestBody::class.java)
        )).thenReturn(monthObservableList)

        Mockito.`when`(mPricesAPI.getPricePerDay(
                anyMap(),
                any(PricePerDayBody::class.java),
                any(String::class.java),
                eq(srcPort),
                any(String::class.java)
        )).thenReturn(outboundDay)

        Mockito.`when`(mPricesAPI.getPricePerDay(
                anyMap(),
                any(PricePerDayBody::class.java),
                any(String::class.java),
                any(String::class.java),
                eq(srcPort)
        )).thenReturn(inboundODay)
    }
}
