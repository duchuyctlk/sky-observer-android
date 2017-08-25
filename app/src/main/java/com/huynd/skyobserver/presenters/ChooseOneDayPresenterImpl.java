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
        mView.updateAvailOutBoundMonths(mModel.getAvailMonths());
        mView.updateAvailInBoundMonths(mModel.getAvailMonths());
    }

    @Override
    public void onOutboundMonthSelected(int year, int month) {
        mView.updateAvailOutBoundDays(mModel.getAvailDays(year, month));
    }

    @Override
    public void onInboundMonthSelected(int year, int month) {
        mView.updateAvailInBoundDays(mModel.getAvailDays(year, month));
    }
}
