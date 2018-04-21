package com.huynd.skyobserver.models;

/**
 * Created by HuyND on 12/30/2017.
 */

public class Country {
    private String mCode;
    private String mName;

    public Country(String code, String name) {
        mCode = code;
        mName = name;
    }

    public String getCountryCode() {
        return mCode;
    }

    public String getCountryName() {
        return mName;
    }
}
