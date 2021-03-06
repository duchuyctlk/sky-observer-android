package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PriceOneDayModel;
import com.huynd.skyobserver.entities.PricePerDay;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.PriceComparator;
import com.huynd.skyobserver.views.PriceOneDayView;

import java.util.List;

/**
 * Created by HuyND on 8/15/2017.
 */

public class PriceOneDayPresenterImpl implements
        PriceOneDayPresenter,
        PriceOneDayModel.PriceOneDayModelEventListener {

    private PriceOneDayView mView;
    private PriceOneDayModel mModel;
    private PricesAPI mPricesAPI;

    public PriceOneDayPresenterImpl(PriceOneDayView view, PricesAPI pricesAPI) {
        mView = view;
        mPricesAPI = pricesAPI;

        mModel = new PriceOneDayModel();
        mModel.setPriceOneDayModelEventListener(this);
    }

    @Override
    public void getPrices(int year, int month, int day, String srcPort, String dstPort, boolean outbound) {
        if (mView == null) {
            return;
        }

        mView.showLoadingDialog();
        mModel.getPrices(mPricesAPI, year, month, day, srcPort, dstPort, outbound);
    }

    @Override
    public void setSortOrder(PriceComparator.SortOrder sortOrder) {
        mModel.setSortOrder(sortOrder);
    }

    @Override
    public void notifyInvalidDate() {
        mView.showInvalidDateDialog();
    }

    @Override
    public void onGetPricesResponse(List<PricePerDay> prices, boolean outbound) {
        if (mView == null) {
            return;
        }

        if (outbound) {
            mView.updateListViewOutboundPrices(prices);
        } else {
            mView.updateListViewInboundPrices(prices);
        }

        if (mModel.isLoadingDone()) {
            mView.dismissLoadingDialog();
        }
    }
}
