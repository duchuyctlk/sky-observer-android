package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.PricePerDay;

/**
 * Created by HuyND on 9/28/2017.
 */

public class AirportPriceInfo {
    private Airport mAirport;

    private PricePerDay mPricePerDay;

    public String getDestination() {
        return mAirport != null ? mAirport.toString() : "";
    }

    public int getBestPrice() {
        return mPricePerDay != null ? mPricePerDay.getPrice() : 0;
    }
}
