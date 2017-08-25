package com.huynd.skyobserver.fragments;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import com.google.gson.Gson;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverAndroidTestApp;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest;
import com.huynd.skyobserver.idlingResource.ChangeFragmentIdlingResource;
import com.huynd.skyobserver.models.PricePerDayBody;
import com.huynd.skyobserver.models.PricePerDayResponse;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by HuyND on 8/16/2017.
 */

public class PriceOneDayFragmentAndroidTest {
    private final String code_200_ok_response = "get_prices_per_day_code_200_ok_response.json";
    private final String code_404_not_found = "code_404_not_found.json";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mActivity;

    private ChangeFragmentIdlingResource<PricePerDayFragment> mPricePerDayFragmentIdlingResource;

    @Inject
    PricesAPI mPricesAPI;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        SkyObserverAndroidTestApp app = (SkyObserverAndroidTestApp) mActivity.getApplication();
        SkyObserverComponentAndroidTest component = (SkyObserverComponentAndroidTest) app.getSkyObserverComponent();
        component.inject(this);

        mPricePerDayFragmentIdlingResource = new ChangeFragmentIdlingResource<>(PricePerDayFragment.class, mActivity);
        Espresso.registerIdlingResources(mPricePerDayFragmentIdlingResource);

        setUpPriceOneDayFragment();
    }

    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(mPricePerDayFragmentIdlingResource);
    }

    @Test
    public void shouldLoadPricesSuccessfully() throws Exception {
        onView(withId(R.id.spinner_month)).perform(click());
        onData(anything()).atPosition(2).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0).perform(click());

        checkViewWidgetsIsDisplayed(R.id.txt_routine, R.id.txt_flight_date, R.id.chk_show_total_price, R.id.lst_prices);
    }

    @Test
    public void shouldLoadPricesFailed() throws Exception {
        onView(withId(R.id.spinner_month)).perform(click());
        onData(anything()).atPosition(2).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());

        mockApiResponse(false, true);

        onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(0).perform(click());

        onView(withId(R.id.lst_prices)).check(matches(isEnabled()));
        onView(withId(R.id.lst_prices)).check(matches(not(isDisplayed())));
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
