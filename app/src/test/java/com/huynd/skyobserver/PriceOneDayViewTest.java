package com.huynd.skyobserver;

import com.huynd.skyobserver.activities.MainActivity;
import com.huynd.skyobserver.fragments.PriceOneDayFragment;
import com.huynd.skyobserver.presenters.PriceOneDayPresenter;
import com.huynd.skyobserver.presenters.PriceOneDayPresenterImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/16/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = LOLLIPOP)
public class PriceOneDayViewTest {
    private MainActivity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testShowInvalidDateDialog() throws Exception {
        try {
            PriceOneDayFragment fragment = (PriceOneDayFragment) PriceOneDayFragment.newInstance();
            PriceOneDayPresenter presenter =
                    new PriceOneDayPresenterImpl(fragment, null);
            mActivity.setFragment(fragment, PriceOneDayFragment.TAG, false);
            presenter.getPrices(2015, 1, 1, "SGN", "HAN", true);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }
}
