package com.huynd.skyobserver.models;

/**
 * Created by HuyND on 8/11/2017.
 */

public class Airport {
    private String mId;
    private String mName;

    public Airport(String id, String name) {
        mId = id;
        mName = name;
    }


    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
