package com.huynd.skyobserver.models;

import com.huynd.skyobserver.presenters.PriceOneDayPresenter;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.Constants;
import com.huynd.skyobserver.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HuyND on 8/15/2017.
 */

public class PriceOneDayModel {
    private PriceOneDayPresenter mPresenter;

    private List<PricePerDay> mOutboundPrices;
    private int mNoOfReceivedOutboundRequests;

    private List<PricePerDay> mInboundPrices;
    private int mNoOfReceivedInboundRequests;

    private boolean isOutboundLoadingDone, isInboundLoadingDone, willLoadInbound;

    public PriceOneDayModel(PriceOneDayPresenter presenter) {
        mPresenter = presenter;
    }

    public void getPrices(PricesAPI mPricesAPI, int year, int month, int day,
                          String srcPort, String dstPort, final boolean outbound) {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH) + 1;
        int today = cal.get(Calendar.DAY_OF_MONTH);

        boolean invalidDate = (year < thisYear)
                || (year == thisYear && month < thisMonth)
                || (year == thisYear && month == thisMonth && day < today);

        if (invalidDate) {
            mPresenter.notifyInvalidDate();
        } else {
            String strYear = String.valueOf(year);
            String strMonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
            String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);

            PricePerDayBody postData = new PricePerDayBody(strYear, strMonth, strDay);
            if (outbound) {
                isOutboundLoadingDone = false;
                mNoOfReceivedOutboundRequests = 0;
                mOutboundPrices = new ArrayList<>();
            } else {
                isInboundLoadingDone = false;
                willLoadInbound = true;
                mNoOfReceivedInboundRequests = 0;
                mInboundPrices = new ArrayList<>();
            }

            for (String carrier : Constants.CARRIERS) {
                Map<String, String> headers = RequestHelper.getDefaultHeaders();
                headers.put("Request_Hash", RequestHelper.requestHashBuilder(srcPort, dstPort,
                        carrier, strYear, strMonth));
                headers.put("Request_Carrier", carrier);

                Observable<List<PricePerDayResponse>> observableList = mPricesAPI
                        .getPricePerDay(headers, postData, carrier, srcPort, dstPort);

                observableList
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new PriceOneDayModelOnNextConsumer(outbound, this),
                                new PriceOneDayModelOnErrorConsumer(outbound, this));
            }
        }
    }

    private void returnReceivedPricesWhenFull(boolean outbound) {
        if (outbound) {
            mNoOfReceivedOutboundRequests++;
            if (mNoOfReceivedOutboundRequests == Constants.CARRIERS.length) {
                isOutboundLoadingDone = true;
                Collections.sort(mOutboundPrices);
                mPresenter.onGetPricesResponse(mOutboundPrices, outbound);
            }
        } else {
            mNoOfReceivedInboundRequests++;
            if (mNoOfReceivedInboundRequests == Constants.CARRIERS.length) {
                isInboundLoadingDone = true;
                Collections.sort(mInboundPrices);
                mPresenter.onGetPricesResponse(mInboundPrices, outbound);
                willLoadInbound = false;
            }
        }
    }

    public boolean isLoadingDone() {
        if (willLoadInbound) {
            return isOutboundLoadingDone && isInboundLoadingDone;
        } else {
            return isOutboundLoadingDone;
        }
    }

    protected void onNextConsumerAccept(boolean outbound, List<PricePerDayResponse> pricePerDayResponses) throws Exception {
        String departureDate = String.valueOf(pricePerDayResponses.get(0).getDepartureDate());
        int day = Integer.parseInt(departureDate.substring(6));

        for (int i = 0; i < pricePerDayResponses.size(); i++) {
            PricePerDayResponse pricePerDayResponse = pricePerDayResponses.get(i);
            String departureTime = pricePerDayResponse.getDepartureTime();
            String arrivalTime = pricePerDayResponse.getArrivalTime();
            String carrier = pricePerDayResponse.getProvider();
            List<PricePerDay> priceList = pricePerDayResponse.getPriceList();
            PricePerDay price = priceList != null && priceList.size() > 0 ? priceList.get(0) : null;
            if (price != null) {
                price.setDay(day);
                price.setDepartureTime(departureTime);
                price.setArrivalTime(arrivalTime);
                price.setCarrier(carrier);
                if (outbound) {
                    mOutboundPrices.add(price);
                } else {
                    mInboundPrices.add(price);
                }
            }
        }

        returnReceivedPricesWhenFull(outbound);
    }

    protected void onErrorConsumerAccept(boolean outbound, Throwable throwable) throws Exception {
        returnReceivedPricesWhenFull(outbound);
    }
}
