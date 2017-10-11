package com.huynd.skyobserver.presenters;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/16/2017.
 */

public class PriceOneDayPresenterTest {
    private PriceOneDayPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        mPresenter = new PriceOneDayPresenterImpl(null, null);
    }

    @Test
    public void onBtnGetPricesClickShouldDoNothingIfViewIsNull() throws Exception {
        try {
            mPresenter.getPrices(2017, 10, 01, "SGN", "HAN", true);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }

    @Test
    public void onResponseShouldDoNothingIfViewIsNull() throws Exception {
        try {
            ((PriceOneDayPresenterImpl) mPresenter).onGetPricesResponse(null, true);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }
}
