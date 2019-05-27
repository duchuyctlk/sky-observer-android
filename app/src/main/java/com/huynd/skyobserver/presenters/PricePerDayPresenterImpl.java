package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.models.PricePerDayModel;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.DateUtils;
import com.huynd.skyobserver.views.PricePerDayView;

import java.util.List;

/**
 * Created by HuyND on 8/9/2017.
 */

public class PricePerDayPresenterImpl implements
        PricePerDayPresenter,
        PricePerDayModel.PricePerDayModelEventListener {

    private PricePerDayView mView;
    private PricePerDayModel mModel;
    private PricesAPI mPricesAPI;

    public PricePerDayPresenterImpl(PricePerDayView view, PricesAPI pricesAPI) {
        mView = view;
        mPricesAPI = pricesAPI;

        mModel = new PricePerDayModel();
        mModel.setPricePerDayModelEventListener(this);
    }

    @Override
    public void initSpinnersValues() {
        if (mView == null) {
            return;
        }

        mView.updateAirports(mModel.getAirports());

        int year = DateUtils.Companion.getStartYear();
        int month = DateUtils.Companion.getStartMonth();
        String dateAsString = DateUtils.Companion.dateToString(year, month);
        mView.updateDateToEditText(dateAsString);
    }

    @Override
    public void onBtnGetPricesClick(int year, int month, String srcPort, String dstPort) {
        if (mView == null) {
            return;
        }

        mView.showLoadingDialog();
        mModel.getPrices(mPricesAPI, year, month, srcPort, dstPort);
    }

    @Override
    public void onGetPricesResponse(List<PricePerDay> prices) {
        if (mView == null) {
            return;
        }

        mView.updateGridViewPrices(prices);
        mView.dismissLoadingDialog();
    }
}
