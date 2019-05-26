package com.huynd.skyobserver.models;

import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.Constants;
import com.huynd.skyobserver.utils.PriceComparator;
import com.huynd.skyobserver.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HuyND on 8/15/2017.
 */

public class PriceOneDayModel {

    public interface PriceOneDayModelEventListener {
        void notifyInvalidDate();

        void onGetPricesResponse(List<PricePerDay> prices, boolean outbound);
    }

    private PriceOneDayModelEventListener mListener;

    private List<PricePerDay> mOutboundPrices;
    private int mNoOfReceivedOutboundRequests;

    private List<PricePerDay> mInboundPrices;
    private int mNoOfReceivedInboundRequests;

    private boolean isOutboundLoadingDone, isInboundLoadingDone, willLoadInbound;

    private PriceComparator.SortOrder mSortOrder = PriceComparator.SortOrder.DEPART_EARLIEST;

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
            if (mListener != null) {
                mListener.notifyInvalidDate();
            }
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
                Map<String, String> headers = RequestHelper.Companion.getDefaultHeaders();

                Observable<List<PricePerDayResponse>> observableList = mPricesAPI
                        .getPricePerDay(headers, postData, carrier, srcPort, dstPort);

                observableList
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<PricePerDayResponse>>() {
                            @Override
                            public void accept(List<PricePerDayResponse> pricePerDayResponses) throws Exception {
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
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                returnReceivedPricesWhenFull(outbound);
                            }
                        });
            }
        }
    }

    private void returnReceivedPricesWhenFull(boolean outbound) {
        if (outbound) {
            mNoOfReceivedOutboundRequests++;
            if (mNoOfReceivedOutboundRequests == Constants.CARRIERS.length) {
                isOutboundLoadingDone = true;
                PriceComparator priceComparator = PriceComparator.Companion.getInstance();
                priceComparator.setSortOrder(mSortOrder);
                Collections.sort(mOutboundPrices, priceComparator);
                if (mListener != null) {
                    mListener.onGetPricesResponse(mOutboundPrices, outbound);
                }
            }
        } else {
            mNoOfReceivedInboundRequests++;
            if (mNoOfReceivedInboundRequests == Constants.CARRIERS.length) {
                isInboundLoadingDone = true;
                PriceComparator priceComparator = PriceComparator.Companion.getInstance();
                priceComparator.setSortOrder(mSortOrder);
                Collections.sort(mInboundPrices, priceComparator);
                if (mListener != null) {
                    mListener.onGetPricesResponse(mInboundPrices, outbound);
                }
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

    public void setSortOrder(PriceComparator.SortOrder sortOrder) {
        mSortOrder = sortOrder;
        PriceComparator priceComparator = PriceComparator.Companion.getInstance();
        priceComparator.setSortOrder(mSortOrder);

        if (mListener != null) {
            if (mOutboundPrices != null && mOutboundPrices.size() > 0) {
                Collections.sort(mOutboundPrices, priceComparator);
                mListener.onGetPricesResponse(mOutboundPrices, true);
            }
            if (mInboundPrices != null && mInboundPrices.size() > 0) {
                Collections.sort(mInboundPrices, priceComparator);
                mListener.onGetPricesResponse(mInboundPrices, false);
            }
        }
    }

    public void setPriceOneDayModelEventListener(PriceOneDayModelEventListener listener) {
        mListener = listener;
    }
}
