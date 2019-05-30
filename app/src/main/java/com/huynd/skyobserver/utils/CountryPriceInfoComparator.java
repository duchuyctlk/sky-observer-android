package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo;

import java.util.Comparator;

/**
 * Created by HuyND on 5/10/2018.
 */

public class CountryPriceInfoComparator implements Comparator<CountryPriceInfo> {

    private static CountryPriceInfoComparator sInstance;

    public static CountryPriceInfoComparator getInstance() {
        if (sInstance == null) {
            sInstance = new CountryPriceInfoComparator();
        }
        return sInstance;
    }

    @Override
    public int compare(CountryPriceInfo countryPriceInfo1, CountryPriceInfo countryPriceInfo2) {
        return countryPriceInfo1.getBestPriceTotal() - countryPriceInfo2.getBestPriceTotal();
    }
}
