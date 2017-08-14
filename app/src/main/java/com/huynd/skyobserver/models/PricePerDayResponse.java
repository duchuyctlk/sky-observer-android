package com.huynd.skyobserver.models;

import com.google.gson.annotations.SerializedName;
import com.huynd.skyobserver.utils.DateUtils;

import java.util.Date;
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

    @SerializedName("arrivalTime")
    private String arrivalTime;

    @SerializedName("departureTime")
    private String departureTime;

    public PricePerDayResponse() {
    }

    public List<PricePerDay> getPriceList() {
        return priceList;
    }

    public int getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }
}
