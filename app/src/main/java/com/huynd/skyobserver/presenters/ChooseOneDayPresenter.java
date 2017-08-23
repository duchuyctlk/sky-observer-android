package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.ChooseOneDayModel;

/**
 * Created by HuyND on 8/22/2017.
 */

public interface ChooseOneDayPresenter {
    void setModel(ChooseOneDayModel model);

    void initSpinnersValues();

    void onOutboundMonthSelected(int selectedYear, int selectedMonth);

    void onInboundMonthSelected(int selectedYear, int selectedMonth);
}
