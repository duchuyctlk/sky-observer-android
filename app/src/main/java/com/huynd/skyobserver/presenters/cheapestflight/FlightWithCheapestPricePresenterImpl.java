package com.huynd.skyobserver.presenters.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceModel;
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceView;

/**
 * Created by HuyND on 11/19/2017.
 */

public class FlightWithCheapestPricePresenterImpl implements FlightWithCheapestPricePresenter {
    private FlightWithCheapestPriceView mView;
    private FlightWithCheapestPriceModel mModel;

    public FlightWithCheapestPricePresenterImpl(FlightWithCheapestPriceView view) {
        mView = view;
    }

    @Override
    public void setModel(FlightWithCheapestPriceModel model) {
        mModel = model;
    }

    @Override
    public void initSpinnersValues() {
        mView.updateAirports(mModel.getAirports());
        mView.updateAvailOutBoundMonths(mModel.getAvailMonths());
        mView.updateAvailInBoundMonths(mModel.getAvailMonths());
    }

    @Override
    public void onOutboundMonthSelected(int year, int month) {
        mView.updateAvailOutBoundDays(mModel.getAvailDays(year, month));
    }

    @Override
    public void onInboundMonthSelected(int year, int month) {
        mView.updateAvailInBoundDays(mModel.getAvailDays(year, month));
    }
}
