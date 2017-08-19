package com.huynd.skyobserver.views;

import com.huynd.skyobserver.models.PricePerDay;

import java.util.List;

/**
 * Created by HuyND on 8/15/2017.
 */

public interface PriceOneDayView extends BaseView {
    void updateListViewPrices(List<PricePerDay> prices);

    void showInvalidDateDialog();
}
