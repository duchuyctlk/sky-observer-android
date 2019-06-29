package com.huynd.skyobserver.models;

import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.CountryAirportUtils;
import com.huynd.skyobserver.utils.Constants;
import com.huynd.skyobserver.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HuyND on 8/9/2017.
 */

public class PricePerDayModel {

    public interface PricePerDayModelEventListener {
        void onGetPricesResponse(List<PricePerDay> prices);
    }

    private PricePerDayModelEventListener mListener;

    private PricePerDay[] mPrices;
    private int mNoOfReceivedRequests;

    private int mQueryingYear;
    private int mQueryingMonth;
    private int mQueryingStartDay;

    public List<Airport> getAirports() {
        return CountryAirportUtils.getAirports();
    }

    public void getPrices(PricesAPI mPricesAPI, int year, int month, String srcPort, String dstPort) {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH) + 1;

        final int startDay = year == thisYear && month == thisMonth ? cal.get(Calendar.DAY_OF_MONTH) + 1 : 1;
        cal.set(year, month - 1, startDay);
        int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String strYear = String.valueOf(year);
        String strMonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);

        mPrices = new PricePerDay[endDay - startDay + 1];
        mNoOfReceivedRequests = 0;

        mQueryingYear = year;
        mQueryingMonth = month;
        mQueryingStartDay = startDay;

        for (int day = startDay; day <= endDay; day++) {
            String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
            PricePerDayBody postData = new PricePerDayBody(strYear, strMonth, strDay);
            for (String carrier : Constants.CARRIERS) {
                Map<String, String> headers = RequestHelper.Companion.getDefaultHeaders();
                headers.put("Request_Hash", RequestHelper.Companion.requestHashBuilder(srcPort, dstPort,
                        carrier, strYear, strMonth));
                headers.put("Request_Carrier", carrier);

                Observable<List<PricePerDayResponse>> observableList = mPricesAPI
                        .getPricePerDay(headers, postData, carrier, srcPort, dstPort);

                observableList
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<PricePerDayResponse>>() {
                            @Override
                            public void accept(List<PricePerDayResponse> pricePerDayResponses) throws Exception {
                                double minPriceTotal = 1000000000;
                                double minPriceBeforeTax = 0;

                                String minCarrier = "";

                                for (int i = 0; i < pricePerDayResponses.size(); i++) {
                                    PricePerDayResponse pricePerDayResponse = pricePerDayResponses.get(i);
                                    String carrier = pricePerDayResponse.getProvider();
                                    List<PricePerDay> priceList = pricePerDayResponse.getPriceList();
                                    PricePerDay price = priceList != null && priceList.size() > 0 ? priceList.get(0) : null;
                                    if (price != null && price.getPriceTotal() < minPriceTotal) {
                                        minPriceTotal = price.getPriceTotal();
                                        minPriceBeforeTax = price.getPrice();
                                        minCarrier = carrier;
                                    }
                                }

                                String departureDate = String.valueOf(pricePerDayResponses.get(0).getDepartureDate());
                                int day = Integer.parseInt(departureDate.substring(6));

                                int targetIndex = day - startDay;
                                if (mPrices[targetIndex] == null || mPrices[targetIndex].getPriceTotal() > minPriceTotal) {
                                    PricePerDay minPricePerDay = new PricePerDay();
                                    minPricePerDay.setDay(day);
                                    minPricePerDay.setPriceTotal(minPriceTotal);
                                    minPricePerDay.setPrice(minPriceBeforeTax);
                                    minPricePerDay.setCarrier(minCarrier);
                                    mPrices[targetIndex] = minPricePerDay;
                                }
                                returnReceivedPricesWhenFull();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                returnReceivedPricesWhenFull();
                            }
                        });
            }
        }
    }

    private void returnReceivedPricesWhenFull() {
        mNoOfReceivedRequests++;
        if (mNoOfReceivedRequests == Constants.CARRIERS.length * mPrices.length) {
            List<PricePerDay> lstPrices = new ArrayList<>();
            lstPrices.addAll(Arrays.asList(mPrices));

            Calendar cal = Calendar.getInstance();
            cal.set(mQueryingYear, mQueryingMonth - 1, mQueryingStartDay);

            for (int i = 0; i < cal.get(Calendar.DAY_OF_WEEK) - 1; i++) {
                lstPrices.add(0, null);
            }

            if (mListener != null) {
                mListener.onGetPricesResponse(lstPrices);
            }
        }
    }

    public void setPricePerDayModelEventListener(PricePerDayModelEventListener listener) {
        mListener = listener;
    }
}
