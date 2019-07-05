package com.huynd.skyobserver.fragments

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withClassName
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.widget.DatePicker
import com.google.gson.Gson
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverAndroidTestApp
import com.huynd.skyobserver.activities.MainActivity
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.FileUtils.getStringFromAssets
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mockito.`when`
import retrofit2.Response
import java.util.*
import java.util.Calendar.YEAR
import javax.inject.Inject

/**
 * Created by HuyND on 8/26/2017.
 */

class DateCheapestFragmentAndroidTest {

    private val outbound_response = "outbound_best_price_per_day_code_200_ok_response.json"
    private val inbound_response = "inbound_best_price_per_day_code_200_ok_response.json"
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
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(2).perform(click())
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
    fun shouldShowInvalidDateDialog() {
        onView(withId(R.id.edit_text_date_outbound)).perform(click())
        onView(withClassName(equalTo<String>(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(Calendar.getInstance().get(YEAR), 1, 1))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.btn_find_flights)).perform(click())

        onView(withText(R.string.invalid_date_message)).inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test
    fun shouldSendRequestAndDisplayResponse() {
        mockApiResponse(true, true, "SGN")

        // select dates
        val currentYear = Calendar.getInstance().get(YEAR)
        onView(withId(R.id.edit_text_date_outbound)).perform(click())
        onView(withClassName(equalTo<String>(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(currentYear, 12, 1))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.edit_text_date_inbound)).perform(click())
        onView(withClassName(equalTo<String>(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(currentYear, 12, 31))
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
        return object : BoundedMatcher<Any, CountryPriceInfo>(CountryPriceInfo::class.java) {
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
        if (requestSuccess) {
            val outboundList: List<PricePerDayResponse>
            val inboundList: List<PricePerDayResponse>

            val gson = Gson()
            val assetManager = getInstrumentation().context.assets
            val targetClass = Array<PricePerDayResponse>::class.java

            if (responseSuccess) {
                val outRes = gson.fromJson(getStringFromAssets(assetManager, outbound_response), targetClass)
                outboundList = outRes.toList()

                val inRes = gson.fromJson(getStringFromAssets(assetManager, inbound_response), targetClass)
                inboundList = inRes.toList()
            } else {
                val responseBody = ResponseBody.create(
                        null,
                        getStringFromAssets(getInstrumentation().context.assets, code_404_not_found)
                )
                val response: Response<List<PricePerDayResponse>> = Response.error(404, responseBody)
                val resBody = response.body()!!

                outboundList = resBody
                inboundList = resBody
            }

            mock<PricesAPI> {
                runBlocking {
                    `when`(mPricesAPI.getListPricePerDay(
                            anyMap(),
                            any(),
                            any(),
                            eq(srcPort),
                            any()
                    )).thenReturn(outboundList)

                    `when`(mPricesAPI.getListPricePerDay(
                            anyMap(),
                            any(),
                            any(),
                            any(),
                            eq(srcPort)
                    )).thenReturn(inboundList)
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
                }
            }
        }
    }
}
