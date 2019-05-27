package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.utils.DateUtils;
import com.huynd.skyobserver.views.ChooseOneDayView;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayPresenterImpl implements ChooseOneDayPresenter {
    private ChooseOneDayView mView;
    private ChooseOneDayModel mModel;

    public ChooseOneDayPresenterImpl(ChooseOneDayView view) {
        mView = view;
    }

    @Override
    public void setModel(ChooseOneDayModel model) {
        mModel = model;
    }

    @Override
    public void initSpinnersValues() {
        mView.updateAirports(mModel.getAirports());

        int year = DateUtils.Companion.getStartYear();
        int month = DateUtils.Companion.getStartMonth();
        int dayOfMonth = DateUtils.Companion.getStartDayOfMonth();
        String dateAsString = DateUtils.Companion.dateToString(year, month, dayOfMonth);
        mView.updateDatePickers(year, month, dayOfMonth);
        mView.updateDateToEditText(dateAsString, true);
        mView.updateDateToEditText(dateAsString, false);
        mView.setDatePickersMinDate(DateUtils.Companion.getMinDate());
    }

    @Override
    public void setDateToEditText(int year, int month, int dayOfMonth, boolean isOutbound) {
        mView.updateDateToEditText(DateUtils.Companion.dateToString(year, month, dayOfMonth), isOutbound);
    }
}
