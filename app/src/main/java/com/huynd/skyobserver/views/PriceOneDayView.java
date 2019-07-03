package com.huynd.skyobserver.views;

import com.huynd.skyobserver.entities.PricePerDay;

import java.util.List;

/**
 * Created by HuyND on 8/15/2017.
 */

public interface PriceOneDayView extends BaseView {
    void updateListViewOutboundPrices(List<PricePerDay> prices);

    void updateListViewInboundPrices(List<PricePerDay> prices);

    void showInvalidDateDialog();
}
