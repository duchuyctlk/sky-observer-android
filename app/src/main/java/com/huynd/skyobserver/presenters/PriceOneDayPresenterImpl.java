package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.PriceOneDayModel;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.PriceOneDayView;

/**
 * Created by HuyND on 8/15/2017.
 */

public class PriceOneDayPresenterImpl implements PriceOneDayPresenter {
    private PriceOneDayView mView;
    private PriceOneDayModel mModel;
    private PricesAPI mPricesAPI;

    public PriceOneDayPresenterImpl(PriceOneDayView view, PricesAPI pricesAPI) {
        mView = view;
        mPricesAPI = pricesAPI;
    }

    public void setModel(PriceOneDayModel model) {
        mModel = model;
    }
}
