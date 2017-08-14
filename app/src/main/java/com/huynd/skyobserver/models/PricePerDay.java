package com.huynd.skyobserver.models;

import com.google.gson.annotations.SerializedName;
import com.huynd.skyobserver.utils.DateUtils;

import java.util.Date;

/**
 * Created by HuyND on 8/7/2017.
 */

public class PricePerDay implements Comparable<PricePerDay> {
    @SerializedName("priceTotal")
    private int priceTotal;

    @SerializedName("price")
    private int price;

    private int day;

    private String arrivalTime;

    private String departureTime;

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

    public Date getArrivalTime() {
        return DateUtils.convertStringToDate(arrivalTime);
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return DateUtils.convertStringToDate(departureTime);
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public int compareTo(PricePerDay price) {
        return getDepartureTime().compareTo(price.getDepartureTime());
    }
}
