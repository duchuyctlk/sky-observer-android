package com.huynd.skyobserver.views.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.AvailableMonth;
import com.huynd.skyobserver.views.BaseView;

import java.util.List;

/**
 * Created by HuyND on 11/19/2017.
 */

public interface FlightWithCheapestPriceView extends BaseView {
    void updateAvailOutBoundMonths(List<AvailableMonth> months);

    void updateAvailInBoundMonths(List<AvailableMonth> months);

    void updateAvailOutBoundDays(List<Integer> days);

    void updateAvailInBoundDays(List<Integer> availDays);

    void updateAirports(List<Airport> airports);
}
