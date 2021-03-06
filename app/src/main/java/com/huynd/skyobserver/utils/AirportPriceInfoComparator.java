package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo;

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
        if (airportPriceInfo1 == null && airportPriceInfo2 == null) {
            return 0;
        }
        if (airportPriceInfo1 == null) {
            return -1;
        }
        if (airportPriceInfo2 == null) {
            return 1;
        }
        double price1 = airportPriceInfo1.getBestPriceTotal();
        double price2 = airportPriceInfo2.getBestPriceTotal();
        if (price1 == price2) {
            return 0;
        }
        return price1 > price2 ? 1 : -1;
    }
}
