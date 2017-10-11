package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.utils.PriceComparator;

/**
 * Created by HuyND on 8/15/2017.
 */

public interface PriceOneDayPresenter {
    void getPrices(int year, int month, int day, String srcPort, String dstPort, boolean outbound);

    void setSortOrder(PriceComparator.SortOrder sortOrder);
}
