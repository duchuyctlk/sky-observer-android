package com.huynd.skyobserver.presenters.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceModel;

/**
 * Created by HuyND on 11/19/2017.
 */

public interface FlightWithCheapestPricePresenter {
    void setModel(FlightWithCheapestPriceModel model);

    void initSpinnersValues();

    void onOutboundMonthSelected(int selectedYear, int selectedMonth);

    void onInboundMonthSelected(int selectedYear, int selectedMonth);
}
