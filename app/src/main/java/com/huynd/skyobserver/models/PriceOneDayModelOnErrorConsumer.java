package com.huynd.skyobserver.models;

import io.reactivex.functions.Consumer;

/**
 * Created by HuyND on 9/6/2017.
 */

public class PriceOneDayModelOnErrorConsumer implements Consumer<Throwable> {
    private boolean mOutbound;
    private PriceOneDayModel mModel;

    public PriceOneDayModelOnErrorConsumer(boolean outbound, PriceOneDayModel model) {
        mOutbound = outbound;
        mModel = model;
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        mModel.onErrorConsumerAccept(mOutbound, throwable);
    }
}
