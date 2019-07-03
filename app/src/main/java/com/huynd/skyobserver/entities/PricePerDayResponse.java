package com.huynd.skyobserver.entities;

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

    @SerializedName("arrivalTime")
    private String arrivalTime;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("provider")
    private String provider;

    @SerializedName("originCode")
    private String originCode;

    @SerializedName("destinationCode")
    private String destinationCode;

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

    public String getProvider() {
        return provider;
    }

    public String getOriginCode() {
        return originCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }
}
