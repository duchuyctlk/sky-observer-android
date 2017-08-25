package com.huynd.skyobserver.views;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.AvailableMonth;

import java.util.List;

/**
 * Created by HuyND on 8/22/2017.
 */

public interface ChooseOneDayView extends BaseView {
    void updateAvailOutBoundMonths(List<AvailableMonth> months);

    void updateAvailInBoundMonths(List<AvailableMonth> months);

    void updateAvailOutBoundDays(List<Integer> days);

    void updateAvailInBoundDays(List<Integer> availDays);

    void updateAirports(List<Airport> airports);
}
