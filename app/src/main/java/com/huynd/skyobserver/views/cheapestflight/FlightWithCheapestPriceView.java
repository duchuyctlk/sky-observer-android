package com.huynd.skyobserver.views.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.views.BaseView;

import java.util.List;

/**
 * Created by HuyND on 11/19/2017.
 */

public interface FlightWithCheapestPriceView extends BaseView {

    void updateAirports(List<Airport> airports);

    void updateDatePickers(int startYear, int startMonth, int startDayOfMonth);

    void updateDateToEditText(String dateAsString, boolean isOutbound);

    void setDatePickersMinDate(long minDate);
}
