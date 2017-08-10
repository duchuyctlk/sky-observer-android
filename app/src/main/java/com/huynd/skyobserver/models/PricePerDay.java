package com.huynd.skyobserver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HuyND on 8/7/2017.
 */

public class PricePerDay {
    @SerializedName("priceTotal")
    private int priceTotal;

    @SerializedName("price")
    private int price;
}
