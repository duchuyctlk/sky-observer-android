package com.huynd.skyobserver.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HuyND on 8/7/2017.
 */

public class PricePerDayResponse {
    @SerializedName("departureDate")
    private int departureDate;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("priceList")
    private List<PricePerDay> priceList;

    public List<PricePerDay> getPriceList() {
        return priceList;
    }

    public int getDepartureDate() {
        return departureDate;
    }
}
