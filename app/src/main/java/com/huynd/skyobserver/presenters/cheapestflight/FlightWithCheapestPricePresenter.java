package com.huynd.skyobserver.presenters.cheapestflight;

/**
 * Created by HuyND on 11/19/2017.
 */

public interface FlightWithCheapestPricePresenter {

    void initSpinnersValues();

    void setDateToEditText(int year, int month, int dayOfMonth, boolean isOutbound);
}
