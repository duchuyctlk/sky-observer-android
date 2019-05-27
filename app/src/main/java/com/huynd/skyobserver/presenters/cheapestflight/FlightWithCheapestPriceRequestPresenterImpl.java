package com.huynd.skyobserver.presenters.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceRequestModel;
import com.huynd.skyobserver.utils.DateUtils;
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceRequestView;

/**
 * Created by HuyND on 11/19/2017.
 */

public class FlightWithCheapestPriceRequestPresenterImpl implements FlightWithCheapestPriceRequestPresenter {

    private FlightWithCheapestPriceRequestView mView;
    private FlightWithCheapestPriceRequestModel mModel;

    public FlightWithCheapestPriceRequestPresenterImpl(FlightWithCheapestPriceRequestView view) {
        mView = view;

        mModel = new FlightWithCheapestPriceRequestModel();
    }

    @Override
    public void initSpinnersValues() {
        mView.updateAirports(mModel.getAirports());

        int year = DateUtils.Companion.getStartYear();
        int month = DateUtils.Companion.getStartMonth();
        int dayOfMonth = DateUtils.Companion.getStartDayOfMonth();
        String dateAsString = DateUtils.Companion.dateToString(year, month, dayOfMonth);
        mView.updateDatePickers(year, month, dayOfMonth);
        mView.updateDateToEditText(dateAsString, true);
        mView.updateDateToEditText(dateAsString, false);
        mView.setDatePickersMinDate(DateUtils.Companion.getMinDate());
    }

    @Override
    public void setDateToEditText(int year, int month, int dayOfMonth, boolean isOutbound) {
        mView.updateDateToEditText(DateUtils.Companion.dateToString(year, month, dayOfMonth), isOutbound);
    }
}
