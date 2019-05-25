package com.huynd.skyobserver.presenters;

/**
 * Created by HuyND on 8/9/2017.
 */

public interface PricePerDayPresenter {
    void onBtnGetPricesClick(int year, int month, String srcPort, String dstPort);

    void initSpinnersValues();
}
