package com.huynd.skyobserver.fragments;

import android.support.test.rule.ActivityTestRule;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverAndroidTestApp;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest;
import com.huynd.skyobserver.models.AvailableMonth;
import com.huynd.skyobserver.models.PricePerDayBody;
import com.huynd.skyobserver.models.PricePerDayResponse;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.FileUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
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
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by HuyND on 8/26/2017.
 */

public class ChooseOneDayAndroidTest {
    private final String code_200_ok_response = "get_prices_per_day_code_200_ok_response.json";
    private final String code_404_not_found = "code_404_not_found.json";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;

    @Inject
    PricesAPI mPricesAPI;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        SkyObserverAndroidTestApp app = (SkyObserverAndroidTestApp) mActivity.getApplication();
        SkyObserverComponentAndroidTest component = (SkyObserverComponentAndroidTest) app.getSkyObserverComponent();
        component.inject(this);

        onView(withContentDescription(mActivity.getString(R.string.drawer_open))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listview_left_drawer)).atPosition(1).perform(click());
    }

    @Test
    public void shouldContainViewWidgets() throws Exception {
        checkViewWidgetsIsDisplayed(R.id.spinner_src_port, R.id.spinner_dst_port,
                R.id.spinner_month_outbound, R.id.spinner_day_outbound, R.id.chk_return_trip,
                R.id.spinner_month_inbound, R.id.spinner_day_inbound);
    }

    @Test
    public void shouldLoadPricesSuccessfully() throws Exception {
        mockApiResponse(true, true);

        onView(withId(R.id.spinner_month_outbound)).perform(click());
        onData(anything()).atPosition(2).perform(click());

        onView(withId(R.id.spinner_month_inbound)).perform(click());
        onData(anything()).atPosition(3).perform(click());

        onView(withId(R.id.btn_find_flights)).perform(click());

        checkViewWidgetsIsDisplayed(R.id.txt_routine_inbound, R.id.txt_flight_date_inbound,
                R.id.chk_show_total_price_inbound, R.id.lst_prices_inbound);

        onView(withId(R.id.lst_prices_outbound)).check(matches(isEnabled()));
        onView(withId(R.id.lst_prices_inbound)).check(matches(isEnabled()));
    }

    @Test
    public void shouldCoverOnNothingSelected() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((ChooseOneDayFragment) mActivity.getCurrentFragment())
                            .updateAvailOutBoundMonths(new ArrayList<AvailableMonth>());
                } catch (Exception exception) {
                    fail("Unexpected behavior happened.");
                }
            }
        });
    }

    @Test
    public void shouldClassCatchCastException() throws Exception {
        ChooseOneDayFragment fragment = (ChooseOneDayFragment) mActivity.getCurrentFragment();
        ArrayAdapter<AvailableMonth> adapter = spy(fragment.mSpinnerOutboundMonthAdapter);
        when(adapter.getItem(any(int.class))).thenThrow(new ClassCastException());
        fragment.mSpinnerOutboundMonthAdapter = adapter;
        try {
            onView(withId(R.id.btn_find_flights)).perform(click());
        } catch (Exception exception) {
            fail("Unexpected behavior happened.");
        }
    }

    private void checkViewWidgetsIsDisplayed(int... ids) {
        for (int id : ids) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    private void mockApiResponse(final boolean requestSuccess, final boolean responseSuccess) {
        Call<PricePerDayResponse> mockCall = mock(Call.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Callback<List<PricePerDayResponse>> callback =
                        (Callback<List<PricePerDayResponse>>) invocationOnMock.getArguments()[0];
                if (requestSuccess) {
                    PricePerDayResponse[] pricePerDayResponses;
                    Response<List<PricePerDayResponse>> response;
                    if (responseSuccess) {
                        pricePerDayResponses = new Gson().fromJson(FileUtils.getStringFromAssets(
                                getInstrumentation().getContext().getAssets(),
                                code_200_ok_response), PricePerDayResponse[].class);
                        response = Response.success(Arrays.asList(pricePerDayResponses));
                    } else {
                        response = Response.error(404,
                                ResponseBody.create(null, FileUtils.getStringFromAssets(
                                        getInstrumentation().getContext().getAssets(),
                                        code_404_not_found)));
                    }
                    callback.onResponse(null, response);
                } else {
                    callback.onFailure(null, new Exception("Exception"));
                }
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        when(mPricesAPI.getPricePerDay(any(Map.class), any(PricePerDayBody.class), any(String.class),
                any(String.class), any(String.class))).thenReturn(mockCall);
    }
}