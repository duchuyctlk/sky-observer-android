package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.PricePerDay;

/**
 * Created by HuyND on 9/28/2017.
 */

public class AirportPriceInfo {
    private Airport mAirport;

    private PricePerDay mPricePerDay;

    public String getAirportName() {
        return mAirport != null ? mAirport.toString() : "";
    }

    public String getAirportId() {
        return mAirport != null ? mAirport.getId() : "";
    }

    public int getBestPriceTotal() {
        return mPricePerDay != null ? mPricePerDay.getPriceTotal() : 0;
    }

    public void setAirport(Airport airport) {
        mAirport = airport;
    }

    public void setPricePerDay(PricePerDay pricePerDay) {
        mPricePerDay = pricePerDay;
    }
}
