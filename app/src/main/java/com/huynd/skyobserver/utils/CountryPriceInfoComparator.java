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
        double price1 = countryPriceInfo1.getBestPriceTotal();
        double price2 = countryPriceInfo2.getBestPriceTotal();
        if (price1 == price2) {
            return 0;
        }
        return price1 > price2 ? 1 : -1;
    }
}
