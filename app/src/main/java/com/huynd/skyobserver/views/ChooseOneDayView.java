package com.huynd.skyobserver.views;

import com.huynd.skyobserver.entities.Airport;

import java.util.List;

/**
 * Created by HuyND on 8/22/2017.
 */

public interface ChooseOneDayView extends BaseView {

    void updateAirports(List<Airport> airports);

    void updateDatePickers(int startYear, int startMonth, int startDayOfMonth);

    void updateDateToEditText(String dateAsString, boolean isOutbound);

    void setDatePickersMinDate(long minDate);
}
