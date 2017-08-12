package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PricePerDayModel;
import com.huynd.skyobserver.views.PricePerDayView;

/**
 * Created by HuyND on 8/9/2017.
 */

public class PricePerDayPresenterImpl implements PricePerDayPresenter {
    private PricePerDayView mView;
    private PricePerDayModel mModel;

    public PricePerDayPresenterImpl(PricePerDayView view) {
        mView = view;
    }

    public void setModel(PricePerDayModel model) {
        mModel = model;
    }

    @Override
    public void initSpinnersValues() {
        mView.updateAvailYears(mModel.getAvailYears());
        mView.updateAirports(mModel.getAirports());
    }

    @Override
    public void onYearSelected(int year) {
        mView.updateAvailMonths(mModel.getAvailMonths(year));
    }

    @Override
    public void onBtnGetPricesClick(int year, int month, String srcPort, String dstPort) {
    }
}
