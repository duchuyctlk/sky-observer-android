package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PricePerDayModel;

/**
 * Created by HuyND on 8/9/2017.
 */

public interface PricePerDayPresenter {
    void setModel(PricePerDayModel model);

    void initSpinnersValues();

    void onYearSelected(int year);
}
