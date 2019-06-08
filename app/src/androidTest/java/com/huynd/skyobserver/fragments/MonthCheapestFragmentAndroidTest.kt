package com.huynd.skyobserver.fragments

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.google.gson.Gson
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverAndroidTestApp
import com.huynd.skyobserver.actions.NumberPickerActions
import com.huynd.skyobserver.activities.MainActivity
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.models.cheapestflight.month.MonthCheapestBody
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.FileUtils.getStringFromAssets
import io.reactivex.Observable
import io.reactivex.Observable.error
import io.reactivex.Observable.just
import okhttp3.ResponseBody
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import retrofit2.Response
import java.util.*
import java.util.Calendar.YEAR
import javax.inject.Inject

/**
 * Created by HuyND on 06/08/2019
 */

class MonthCheapestFragmentAndroidTest {
    private val outbound_response = "outbound_best_price_per_day_code_200_ok_response.json"
    private val inbound_response = "inbound_best_price_per_day_code_200_ok_response.json"
    private val month_response = "month_best_price_code_200_ok_response.json"
    private val code_404_not_found = "code_404_not_found.json"

    @Rule
    @JvmField
    var mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, true)

    private lateinit var mActivity: MainActivity

    lateinit var mPricesAPI: PricesAPI
        @Inject set

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mActivity = mActivityTestRule.activity
        val app = mActivity.application as SkyObserverAndroidTestApp
        val component = app.skyObserverComponent as SkyObserverComponentAndroidTest
        component.inject(this)

        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click())
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(3).perform(click())
    }

    @Test
    fun shouldContainViewWidgets() {
        checkViewWidgetsIsDisplayed(
                R.id.spinner_src_port,
                R.id.edit_text_date_outbound,
                R.id.chk_return_trip,
                R.id.edit_text_date_inbound
        )

        onView(withId(R.id.spinner_dst_port)).check(doesNotExist())
    }

    @Test
    fun shouldSendRequestAndDisplayResponse() {
        mockApiResponse(true, true, "SGN")

        // select dates
        val currentYear = Calendar.getInstance().get(YEAR)
        onView(withId(R.id.edit_text_date_outbound)).perform(click())
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(currentYear))
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(11))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.edit_text_date_inbound)).perform(click())
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(currentYear))
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(11))
        onView(withId(android.R.id.button1)).perform(click())

        // find flight
        onView(withId(R.id.btn_find_flights)).perform(click())

        // check on result fragment
        checkViewWidgetsIsDisplayed(R.id.lst_best_destinations)

        onData(anything()).inAdapterView(withId(R.id.lst_best_destinations)).atPosition(0).perform(click())

        onData(allOf(`is`(instanceOf<CountryPriceInfo>(CountryPriceInfo::class.java)), withCountryCode("vn")))
                .inAdapterView(withId(R.id.lst_best_destinations)).atPosition(0).check(matches(isDisplayed()))
    }

    private fun checkViewWidgetsIsDisplayed(vararg ids: Int) {
        ids.forEach { id ->
            onView(withId(id)).check(matches(isDisplayed()))
        }
    }

    private fun withCountryCode(code: String): Matcher<Any> {
        val codeMather = equalTo(code)
        return object: BoundedMatcher<Any, CountryPriceInfo>(CountryPriceInfo::class.java) {
            override fun describeTo(description: Description?) {
                codeMather.describeTo(description)
            }

            override fun matchesSafely(item: CountryPriceInfo?): Boolean {
                return codeMather.matches(item?.country?.countryCode)
            }
        }
    }

    @Throws(Exception::class)
    private fun mockApiResponse(requestSuccess: Boolean, responseSuccess: Boolean, srcPort: String) {
        val outboundDay: Observable<List<PricePerDayResponse>>
        val inboundODay: Observable<List<PricePerDayResponse>>
        val monthObservableList:  Observable<List<CheapestPricePerMonthResponse>>

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
            outboundDay = error(exception)
            inboundODay = error(exception)
            monthObservableList = error(exception)
        }

        `when`(mPricesAPI.getCheapestPricePerMonth(
                anyMap(),
                any(MonthCheapestBody::class.java)
        )).thenReturn(monthObservableList)

        `when`(mPricesAPI.getPricePerDay(
                anyMap(),
                any(PricePerDayBody::class.java),
                any(String::class.java),
                eq(srcPort),
                any(String::class.java)
        )).thenReturn(outboundDay)

        `when`(mPricesAPI.getPricePerDay(
                anyMap(),
                any(PricePerDayBody::class.java),
                any(String::class.java),
                any(String::class.java),
                eq(srcPort)
        )).thenReturn(inboundODay)
    }
}
