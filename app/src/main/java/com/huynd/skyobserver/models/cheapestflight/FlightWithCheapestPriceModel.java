package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.utils.AirportUtils;

import java.util.List;

/**
 * Created by HuyND on 11/19/2017.
 */

public class FlightWithCheapestPriceModel {

    public List<Airport> getAirports() {
        return AirportUtils.getAirports();
    }
}
