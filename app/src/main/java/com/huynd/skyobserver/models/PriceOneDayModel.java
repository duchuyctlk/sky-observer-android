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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HuyND on 8/15/2017.
 */

public class PriceOneDayModel {
    private PriceOneDayPresenter mPresenter;

    private List<PricePerDay> mPrices;
    private int mNoOfReceivedRequests;

    public PriceOneDayModel(PriceOneDayPresenter presenter) {
        mPresenter = presenter;
    }

    public void getPrices(PricesAPI mPricesAPI, int year, int month, int day, String srcPort, String dstPort) {
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

            mNoOfReceivedRequests = 0;
            mPrices = new ArrayList<>();

            for (String carrier : Constants.CARRIERS) {
                Map<String, String> headers = RequestHelper.getDefaultHeaders();
                headers.put("Request_Hash", RequestHelper.requestHashBuilder(srcPort, dstPort,
                        carrier, strYear, strMonth));
                headers.put("Request_Carrier", carrier);

                mPricesAPI.getPricePerDay(headers, postData, carrier, srcPort, dstPort)
                        .enqueue(new Callback<List<PricePerDayResponse>>() {

                            @Override
                            public void onResponse(Call<List<PricePerDayResponse>> call, Response<List<PricePerDayResponse>> response) {
                                if (response.isSuccessful()) {
                                    List<PricePerDayResponse> pricePerDayResponses = response.body();

                                    String departureDate = String.valueOf(pricePerDayResponses.get(0).getDepartureDate());
                                    int day = Integer.parseInt(departureDate.substring(6));

                                    for (int i = 0; i < pricePerDayResponses.size(); i++) {
                                        PricePerDayResponse pricePerDayResponse = pricePerDayResponses.get(i);
                                        String departureTime = pricePerDayResponse.getDepartureTime();
                                        String arrivalTime = pricePerDayResponse.getArrivalTime();
                                        List<PricePerDay> priceList = pricePerDayResponse.getPriceList();
                                        PricePerDay price = priceList != null && priceList.size() > 0 ? priceList.get(0) : null;
                                        if (price != null) {
                                            price.setDay(day);
                                            price.setDepartureTime(departureTime);
                                            price.setArrivalTime(arrivalTime);
                                            mPrices.add(price);
                                        }
                                    }
                                }
                                returnReceivedPricesWhenFull();
                            }

                            @Override
                            public void onFailure(Call<List<PricePerDayResponse>> call, Throwable t) {
                                returnReceivedPricesWhenFull();
                            }
                        });
            }
        }
    }

    private void returnReceivedPricesWhenFull() {
        mNoOfReceivedRequests++;
        if (mNoOfReceivedRequests == Constants.CARRIERS.length) {
            Collections.sort(mPrices);
            mPresenter.onGetPricesResponse(mPrices);
        }
    }
}
