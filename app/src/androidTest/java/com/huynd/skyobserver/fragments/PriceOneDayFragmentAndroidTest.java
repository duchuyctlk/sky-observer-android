package com.huynd.skyobserver.fragments;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import com.google.gson.Gson;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverAndroidTestApp;
import com.huynd.skyobserver.actions.NumberPickerActions;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest;
import com.huynd.skyobserver.idlingResource.ChangeFragmentIdlingResource;
import com.huynd.skyobserver.entities.PricePerDayBody;
import com.huynd.skyobserver.entities.PricePerDayResponse;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.huynd.skyobserver.matchers.ImageMatcher.noDrawable;
import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by HuyND on 8/16/2017.
 */

public class PriceOneDayFragmentAndroidTest {
    private final String code_200_ok_response_normal_data = "get_prices_per_day_code_200_ok_response.json";
    private final String code_200_ok_response_prices_with_no_carrier =
            "get_prices_per_day_code_200_ok_response_prices_with_no_carrier.json";
    private final String code_404_not_found = "code_404_not_found.json";

    private String code_200_ok_response;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;

    private ChangeFragmentIdlingResource<PricePerDayFragment> mPricePerDayFragmentIdlingResource;
    private ChangeFragmentIdlingResource<PriceOneDayFragment> mPriceOneDayFragmentIdlingResource;

    @Inject
    PricesAPI mPricesAPI;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        SkyObserverAndroidTestApp app = (SkyObserverAndroidTestApp) mActivity.getApplication();
        SkyObserverComponentAndroidTest component = (SkyObserverComponentAndroidTest) app.getSkyObserverComponent();
        component.inject(this);

        mPricePerDayFragmentIdlingResource = new ChangeFragmentIdlingResource<>(PricePerDayFragment.class, mActivity);
        mPriceOneDayFragmentIdlingResource = new ChangeFragmentIdlingResource<>(PriceOneDayFragment.class, mActivity);

        Espresso.registerIdlingResources(mPricePerDayFragmentIdlingResource);

        code_200_ok_response = code_200_ok_response_normal_data;
        setUpPriceOneDayFragment();
    }

    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(mPricePerDayFragmentIdlingResource);
    }

    @Test
    public void shouldLoadPricesSuccessfully() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 11);

        onView(withId(R.id.edit_text_month_year)).perform(click());
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.YEAR)));
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.MONTH)));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());

        Espresso.registerIdlingResources(mPriceOneDayFragmentIdlingResource);

        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0).perform(click());

        checkViewWidgetsIsDisplayed(R.id.txt_routine_outbound, R.id.txt_flight_date_outbound,
                R.id.chk_show_total_price_outbound, R.id.lst_prices_outbound);

        Espresso.unregisterIdlingResources(mPriceOneDayFragmentIdlingResource);
    }

    @Test
    public void shouldHidePricesWithNoCarrier() throws Exception {
        code_200_ok_response = code_200_ok_response_prices_with_no_carrier;
        mockApiResponse(true, true);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);

        onView(withId(R.id.edit_text_month_year)).perform(click());
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.YEAR)));
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.MONTH)));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0)
                .onChildView(withId(R.id.image_view_airline)).check(matches(noDrawable()));
    }

    @Test
    public void shouldSetTextViewsEmptyWhenNoData() {
        onView(withId(R.id.edit_text_month_year)).perform(click());
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(2020));
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(11));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0)
                .onChildView(withId(R.id.text_view_day)).check(matches(withText("")));
        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0)
                .onChildView(withId(R.id.text_view_price)).check(matches(withText("")));
    }

    @Test
    public void shouldLoadPricesFailed() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 11);

        onView(withId(R.id.edit_text_month_year)).perform(click());
        onView(withId(R.id.year_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.YEAR)));
        onView(withId(R.id.month_picker))
                .perform(NumberPickerActions.setNumber(calendar.get(Calendar.MONTH)));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());

        mockApiResponse(false, true);

        Espresso.registerIdlingResources(mPriceOneDayFragmentIdlingResource);

        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0).perform(click());

        onView(withId(R.id.lst_prices_outbound)).check(matches(isEnabled()));
        onView(withId(R.id.lst_prices_outbound)).check(matches(not(isDisplayed())));

        Espresso.unregisterIdlingResources(mPriceOneDayFragmentIdlingResource);
    }

    @Test
    public void shouldLoadPricesWhenOpenFromChooseOneDayFragment() throws Exception {
        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(1).perform(click());

        onView(withId(R.id.chk_return_trip)).perform(click());
        onView(withId(R.id.btn_find_flights)).perform(click());
        checkViewWidgetsIsDisplayed(R.id.txt_routine_outbound, R.id.txt_flight_date_outbound,
                R.id.chk_show_total_price_outbound, R.id.lst_prices_outbound);
    }

    @Test
    public void testSortOrder() throws Exception {
        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(1).perform(click());
        onView(withId(R.id.btn_find_flights)).perform(click());

        onView(withId(R.id.chk_show_total_price_outbound)).perform(click());

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_price_only_lowest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("500.0")));

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_price_only_highest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("900.0")));

        onView(withId(R.id.chk_show_total_price_outbound)).perform(click());

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_total_price_lowest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("1070.0")));

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_total_price_highest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("1570.0")));

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_depart_earliest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.text_view_depart_time))
                .check(matches(withText("15:00")));

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_depart_latest)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.text_view_depart_time))
                .check(matches(withText("17:00")));

        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_airlines)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("1570.0")));
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(1)
                .onChildView(withId(R.id.text_view_depart_time))
                .check(matches(withText("17:00")));
    }

    @Test
    public void shouldDisplayPricesBeforeTax() throws Exception {
        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(1).perform(click());
        onView(withId(R.id.btn_find_flights)).perform(click());
        onView(withId(R.id.menu_item_sort_order)).perform(click());
        onView(withText(R.string.sorting_order_total_price_lowest)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(0)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("1070.0")));
        onData(anything()).inAdapterView(withId(R.id.lst_prices_inbound)).atPosition(0)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("1070.0")));

        onView(withId(R.id.chk_show_total_price_outbound)).perform(click());
        onView(withId(R.id.chk_show_total_price_inbound)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lst_prices_outbound)).atPosition(0)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("500.0")));
        onData(anything()).inAdapterView(withId(R.id.lst_prices_inbound)).atPosition(0)
                .onChildView(withId(R.id.btn_select_price))
                .check(matches(withText("500.0")));
    }

    private void checkViewWidgetsIsDisplayed(int... ids) {
        for (int id : ids) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    private void setUpPriceOneDayFragment() throws Exception {
        mockApiResponse(true, true);
        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(0).perform(click());
    }

    private void mockApiResponse(final boolean requestSuccess, final boolean responseSuccess) throws Exception {
        Observable<List<PricePerDayResponse>> observableList = null;

        if (requestSuccess) {
            PricePerDayResponse[] pricePerDayResponses;
            Response<List<PricePerDayResponse>> response;
            if (responseSuccess) {
                pricePerDayResponses = new Gson().fromJson(FileUtils.getStringFromAssets(
                        getInstrumentation().getContext().getAssets(),
                        code_200_ok_response), PricePerDayResponse[].class);
                observableList = just(Arrays.asList(pricePerDayResponses));
            } else {
                response = Response.error(404,
                        ResponseBody.create(null, FileUtils.getStringFromAssets(
                                getInstrumentation().getContext().getAssets(),
                                code_404_not_found)));

                observableList = just(response.body());
            }
        } else {
            observableList = error(new Exception("Exception"));
        }

        when(mPricesAPI.getPricePerDay(any(Map.class), any(PricePerDayBody.class), any(String.class),
                any(String.class), any(String.class))).thenReturn(observableList);
    }
}
