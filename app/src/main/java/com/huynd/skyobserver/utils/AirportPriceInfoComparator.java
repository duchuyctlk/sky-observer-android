package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo;

import java.util.Comparator;

/**
 * Created by HuyND on 5/10/2018.
 */

public class AirportPriceInfoComparator implements Comparator<AirportPriceInfo> {

    private static AirportPriceInfoComparator sInstance;

    public static AirportPriceInfoComparator getInstance() {
        if (sInstance == null) {
            sInstance = new AirportPriceInfoComparator();
        }
        return sInstance;
    }

    @Override
    public int compare(AirportPriceInfo airportPriceInfo1, AirportPriceInfo airportPriceInfo2) {
        return airportPriceInfo1.getBestPriceTotal() - airportPriceInfo2.getBestPriceTotal();
    }
}
