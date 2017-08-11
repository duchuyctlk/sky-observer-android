package com.huynd.skyobserver.models;

/**
 * Created by HuyND on 8/12/2017.
 */

public class PricePerDayBody {
    String departureDate;
    String returnDate;
    boolean isRoundTrip;
    int adultCount;
    int childCount;
    int infantCount;

    public PricePerDayBody(String strYear, String strMonth, String strDay) {
        departureDate = strYear + "-" + strMonth + "-" + strDay;
        returnDate = strYear + "-" + strMonth + "-" + strDay;
        isRoundTrip = false;
        adultCount = 1;
        childCount = 0;
        infantCount = 0;
    }
}
