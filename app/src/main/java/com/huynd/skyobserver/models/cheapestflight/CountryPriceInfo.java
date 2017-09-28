package com.huynd.skyobserver.models.cheapestflight;

import java.util.List;

/**
 * Created by HuyND on 9/28/2017.
 */

public class CountryPriceInfo {
    private List<AirportPriceInfo> mAirportPriceInfos;

    public int getAirportPriceInfoCount() {
        return mAirportPriceInfos != null ? mAirportPriceInfos.size() : 0;
    }

    public AirportPriceInfo getAirportPriceInfo(int index) {
        return mAirportPriceInfos != null ? mAirportPriceInfos.get(index) : null;
    }
}
