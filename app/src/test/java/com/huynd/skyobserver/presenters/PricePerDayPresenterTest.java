package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.fragments.PricePerDayFragment;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/13/2017.
 */

public class PricePerDayPresenterTest {
    private PricePerDayPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        mPresenter = new PricePerDayPresenterImpl(null, null);
    }

    @Test
    public void onBtnGetPricesClickShouldDoNothingIfViewIsNull() throws Exception {
        try {
            mPresenter.onBtnGetPricesClick(2017, 10, "SGN", "HAN");

        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }

    @Test
    public void onBtnGetPricesClickShouldDoNothingIfModelIsNull() throws Exception {
        try {
            mPresenter = new PricePerDayPresenterImpl((PricePerDayFragment) PricePerDayFragment.newInstance(), null);
            mPresenter.setModel(null);
            mPresenter.onBtnGetPricesClick(2017, 10, "SGN", "HAN");
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }

    @Test
    public void onResponseShouldDoNothingIfViewIsNull() throws Exception {
        try {
            mPresenter.onGetPricesResponse(null);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }
}
