package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Country;

import java.util.List;

/**
 * Created by HuyND on 9/28/2017.
 */

public class CountryPriceInfo {
    private Country mCountry;
    private List<AirportPriceInfo> mAirportPriceInfos;

    public Country getCountry() {
        return mCountry;
    }

    public void setCountry(Country country) {
        mCountry = country;
    }

    public void setAirportPriceInfos(List<AirportPriceInfo> airportPriceInfos) {
        mAirportPriceInfos = airportPriceInfos;
    }

    public int getAirportPriceInfoCount() {
        return mAirportPriceInfos != null ? mAirportPriceInfos.size() : 0;
    }

    public AirportPriceInfo getAirportPriceInfo(int index) {
        return mAirportPriceInfos != null ? mAirportPriceInfos.get(index) : null;
    }
}
