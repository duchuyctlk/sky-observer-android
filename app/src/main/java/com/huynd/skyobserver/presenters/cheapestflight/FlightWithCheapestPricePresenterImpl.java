package com.huynd.skyobserver.presenters.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceModel;
import com.huynd.skyobserver.utils.DateUtils;
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

        int year = DateUtils.getStartYear();
        int month = DateUtils.getStartMonth();
        int dayOfMonth = DateUtils.getStartDayOfMonth();
        String dateAsString = DateUtils.dateToString(year, month, dayOfMonth);
        mView.updateDatePickers(year, month, dayOfMonth);
        mView.updateDateToEditText(dateAsString, true);
        mView.updateDateToEditText(dateAsString, false);
        mView.setDatePickersMinDate(DateUtils.getMinDate());
    }

    @Override
    public void setDateToEditText(int year, int month, int dayOfMonth, boolean isOutbound) {
        mView.updateDateToEditText(DateUtils.dateToString(year, month, dayOfMonth), isOutbound);

    }
}
