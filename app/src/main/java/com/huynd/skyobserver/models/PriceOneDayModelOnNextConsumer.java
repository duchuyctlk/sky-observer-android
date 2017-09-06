package com.huynd.skyobserver.models;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by HuyND on 9/6/2017.
 */

public class PriceOneDayModelOnNextConsumer implements Consumer<List<PricePerDayResponse>> {
    private boolean mOutbound;
    private PriceOneDayModel mModel;

    public PriceOneDayModelOnNextConsumer(boolean outbound, PriceOneDayModel model) {
        mOutbound = outbound;
        mModel = model;
    }

    @Override
    public void accept(List<PricePerDayResponse> pricePerDayResponses) throws Exception {
        mModel.onNextConsumerAccept(mOutbound, pricePerDayResponses);
    }
}
