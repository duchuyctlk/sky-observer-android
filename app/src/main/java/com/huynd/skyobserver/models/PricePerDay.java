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

    private int day;

    public int getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(int priceTotal) {
        this.priceTotal = priceTotal;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
