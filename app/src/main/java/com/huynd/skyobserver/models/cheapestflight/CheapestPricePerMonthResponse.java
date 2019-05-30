package com.huynd.skyobserver.models.cheapestflight;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HuyND on 11/18/2017.
 */
public class CheapestPricePerMonthResponse {
    @SerializedName("_id")
    private CheapeastPricePerMonthId id;

    @SerializedName("c")
    private int cheapeastPrice;

    @SerializedName("p")
    private String carrier;

    public static class CheapeastPricePerMonthId {
        @SerializedName("m")
        private int month;

        @SerializedName("y")
        private int year;

        @SerializedName("o")
        private String origin;

        @SerializedName("d")
        private String destination;
    }
}
