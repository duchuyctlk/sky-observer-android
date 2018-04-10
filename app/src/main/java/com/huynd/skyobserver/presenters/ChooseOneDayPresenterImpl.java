package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.ChooseOneDayModel;
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

        int year = mModel.getStartYear();
        int month = mModel.getStartMonth();
        int dayOfMonth = mModel.getStartDayOfMonth();
        String dateAsString = mModel.dateToString(year, month, dayOfMonth);
        mView.updateDatePickers(year, month, dayOfMonth);
        mView.updateDateToEditText(dateAsString, true);
        mView.updateDateToEditText(dateAsString, false);
        mView.setDatePickersMinDate(mModel.getMinDate());
    }

    @Override
    public void setDateToEditText(int year, int month, int dayOfMonth, boolean isOutbound) {
        mView.updateDateToEditText(mModel.dateToString(year, month, dayOfMonth), isOutbound);
    }
}
