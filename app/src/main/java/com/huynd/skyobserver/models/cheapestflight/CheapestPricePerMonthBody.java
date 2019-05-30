package com.huynd.skyobserver.models.cheapestflight;

import static com.huynd.skyobserver.utils.Constants.CARRIERS;

/**
 * Created by HuyND on 11/18/2017.
 */
public class CheapestPricePerMonthBody {
    String startDate;
    String endDate;
    int minPrice;
    int maxPrice;
    String[] providers;
    String[] routes;
    String type;

    public CheapestPricePerMonthBody(String startDate, String endDate, String[] routes) {
        this.startDate = startDate;
        this.endDate = endDate;
        minPrice = 0;
        maxPrice = 700000;
        providers = CARRIERS;
        this.routes = routes;
        type = "month";
    }
}
