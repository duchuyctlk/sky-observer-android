package com.huynd.skyobserver.presenters.cheapestflight;

import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceResultModel;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView;

/**
 * Created by HuyND on 4/21/2018.
 */

public class FlightWithCheapestPriceResultPresenterImpl implements
        FlightWithCheapestPriceResultPresenter,
        FlightWithCheapestPriceResultModel.EventListener {

    private FlightWithCheapestPriceResultView mView;
    private FlightWithCheapestPriceResultModel mModel;
    private PricesAPI mPricesAPI;

    public FlightWithCheapestPriceResultPresenterImpl(FlightWithCheapestPriceResultView view,
                                                      PricesAPI pricesAPI) {
        mView = view;
        mPricesAPI = pricesAPI;

        mModel = new FlightWithCheapestPriceResultModel();
        mModel.setEventListener(this);
    }
}
