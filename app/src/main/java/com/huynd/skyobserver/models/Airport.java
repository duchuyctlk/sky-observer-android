package com.huynd.skyobserver.models;

/**
 * Created by HuyND on 8/11/2017.
 */

public class Airport {
    private String mId;
    private String mName;
    private String mCountryCode;

    public Airport(String id, String name, String countryCode) {
        mId = id;
        mName = name;
        mCountryCode = countryCode;
    }

    public String getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mName;
    }
}
