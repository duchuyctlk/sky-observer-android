package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PriceOneDayModel;
import com.huynd.skyobserver.models.PricePerDay;

import java.util.List;

/**
 * Created by HuyND on 8/15/2017.
 */

public interface PriceOneDayPresenter {
    void setModel(PriceOneDayModel model);

    void getPrices(int year, int month, int day, String srcPort, String dstPort);

    void notifyInvalidDate();

    void onGetPricesResponse(List<PricePerDay> prices);
}
