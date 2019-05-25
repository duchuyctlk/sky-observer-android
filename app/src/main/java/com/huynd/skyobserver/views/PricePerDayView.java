package com.huynd.skyobserver.views;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.PricePerDay;

import java.util.List;

/**
 * Created by HuyND on 8/9/2017.
 */

public interface PricePerDayView extends BaseView {
    void updateAirports(List<Airport> airports);

    void updateGridViewPrices(List<PricePerDay> prices);

    void updateDateToEditText(String dateAsString);
}
