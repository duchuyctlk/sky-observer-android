package com.huynd.skyobserver.views;

import com.huynd.skyobserver.models.Airport;

import java.util.List;
import java.util.Map;

/**
 * Created by HuyND on 8/9/2017.
 */

public interface PricePerDayView extends BaseView {
    void updateAvailYears(List<Integer> years);

    void updateAvailMonths(List<Integer> months);

    void updateAirports(List<Airport> airports);
}
