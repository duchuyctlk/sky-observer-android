package com.huynd.skyobserver.models;

/**
 * Created by HuyND on 8/23/2017.
 */

public class AvailableMonth {
    private int mMonth, mYear;

    public AvailableMonth(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    @Override
    public String toString() {
        return String.valueOf(mMonth) + " / " + String.valueOf(mYear);
    }
}
