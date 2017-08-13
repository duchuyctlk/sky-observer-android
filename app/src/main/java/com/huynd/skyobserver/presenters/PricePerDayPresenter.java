package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.models.PricePerDayModel;

import java.util.List;

/**
 * Created by HuyND on 8/9/2017.
 */

public interface PricePerDayPresenter {
    void setModel(PricePerDayModel model);

    void onBtnGetPricesClick(int year, int month, String srcPort, String dstPort);

    void initSpinnersValues();

    void onYearSelected(int year);

    void onGetPricesResponse(List<PricePerDay> prices);
}
