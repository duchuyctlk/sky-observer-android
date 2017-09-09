package com.huynd.skyobserver.fragments;

import android.support.test.rule.ActivityTestRule;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverAndroidTestApp;
import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.dagger.component.SkyObserverComponentAndroidTest;
import com.huynd.skyobserver.models.PricePerDayBody;
import com.huynd.skyobserver.models.PricePerDayResponse;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.FileUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragmentAndroidTest {
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

        setUpPricePerDayFragment();
    }

    @Test
    public void shouldContainViewWidgets() throws Exception {
        checkViewWidgetsIsDisplayed(R.id.spinner_month, R.id.spinner_year, R.id.btn_get_prices);
        onView(withId(R.id.grid_view_price)).check(matches(isEnabled()));
    }

    @Test
    public void shouldDisplayCorrectAirports() throws Exception {
        checkViewWidgetsIsDisplayed(R.id.spinner_src_port, R.id.spinner_dst_port);
        onView(withId(R.id.spinner_src_port)).check(matches(withSpinnerText("Tân Sơn Nhất")));
        onView(withId(R.id.spinner_dst_port)).check(matches(withSpinnerText("Nội Bài")));

        onView(withId(R.id.spinner_src_port)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.spinner_src_port)).check(matches(withSpinnerText("Côn Đảo")));
    }

    @Test
    public void shouldBtnGetPricesClickSuccessfully() throws Exception {
        mockApiResponse(true, true);

        onView(withId(R.id.spinner_month)).perform(click());
        onData(anything()).atPosition(1).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());
        onView(withId(R.id.grid_view_price)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldBtnGetPricesClickFailed() throws Exception {
        mockApiResponse(false, true);

        onView(withId(R.id.spinner_month)).perform(click());
        onData(anything()).atPosition(1).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());
        onView(withId(R.id.grid_view_price)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.grid_view_price))
                .atPosition(0)
                .onChildView(withId(R.id.text_view_day))
                .check(matches(withText("")));
    }

    @Test
    public void shouldCoverOnNothingSelected() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((PricePerDayFragment) mActivity.getCurrentFragment()).updateAvailYears(new ArrayList<Integer>());
                } catch (Exception exception) {
                    fail("Unexpected behavior happened.");
                }
            }
        });
    }

    @Test
    public void shouldClassCatchCastException() throws Exception {
        mockApiResponse(true, true);

        onView(withId(R.id.spinner_month)).perform(click());
        onData(anything()).atPosition(2).perform(click());

        onView(withId(R.id.btn_get_prices)).perform(click());

        PricePerDayFragment fragment = (PricePerDayFragment) mActivity.getCurrentFragment();
        ArrayAdapter<Integer> adapter = spy(fragment.mSpinnerYearAdapter);
        when(adapter.getItem(any(int.class))).thenThrow(new ClassCastException());
        fragment.mSpinnerYearAdapter = adapter;

        try {
            onData(anything()).inAdapterView(withId(R.id.grid_view_price)).atPosition(3).perform(click());
        } catch (Exception exception) {
            fail("Unexpected behavior happened.");
        }
    }

    private void checkViewWidgetsIsDisplayed(int... ids) {
        for (int id : ids) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    private void setUpPricePerDayFragment() throws Exception {
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
