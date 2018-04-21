package com.huynd.skyobserver.models.cheapestflight;

/**
 * Created by HuyND on 4/22/2018.
 */

public class FlightWithCheapestPriceResultModel {

    public interface EventListener {

    }

    private EventListener mEventListener;

    public void setEventListener(EventListener listener) {
        mEventListener = listener;
    }
}
