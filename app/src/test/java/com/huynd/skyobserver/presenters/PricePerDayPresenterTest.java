package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.fragments.PricePerDayFragment;
import com.huynd.skyobserver.models.PricePerDayModel;

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
    public void onResponseShouldDoNothingIfViewIsNull() throws Exception {
        try {
            ((PricePerDayModel.PricePerDayModelEventListener)mPresenter).onGetPricesResponse(null);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }

    @Test
    public void initSpinnersValuesShouldDoNothingIfViewIsNull() throws Exception {
        try {
            mPresenter.initSpinnersValues();
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }

    @Test
    public void onYearSelectedShouldDoNothingIfViewIsNull() throws Exception {
        try {
            mPresenter.onYearSelected(2020);
        } catch (Exception e) {
            fail("Unexpected behavior happened.");
        }
    }
}
