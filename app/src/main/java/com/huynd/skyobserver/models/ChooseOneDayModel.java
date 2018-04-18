package com.huynd.skyobserver.models;

import com.huynd.skyobserver.utils.AirportUtils;

import java.util.List;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayModel {
    public List<Airport> getAirports() {
        return AirportUtils.getAirports();
    }
}
