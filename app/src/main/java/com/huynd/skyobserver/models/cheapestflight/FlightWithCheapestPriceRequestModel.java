package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.utils.CountryAirportUtils;

import java.util.List;

/**
 * Created by HuyND on 11/19/2017.
 */

public class FlightWithCheapestPriceRequestModel {

    public List<Airport> getAirports() {
        return CountryAirportUtils.getAirports();
    }
}