package com.huynd.skyobserver.models;

import android.util.Log;

import com.huynd.skyobserver.presenters.PricePerDayPresenter;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.Constants;
import com.huynd.skyobserver.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HuyND on 8/9/2017.
 */

public class PricePerDayModel {
    private PricePerDayPresenter mPresenter;
    private Map<Integer, List<Integer>> mSpinnerMonthValues;
    private List<Integer> mSpinnerYearValues;
    private List<Airport> mAirports;
    private PricePerDay[] mPrices;
    private int mNoOfReceivedRequests;

    private int mQueryingYear;
    private int mQueryingMonth;
    private int mQueryingStartDay;

    public PricePerDayModel(PricePerDayPresenter presenter) {
        mPresenter = presenter;
    }

    public List<Integer> getAvailYears() {
        if (mSpinnerMonthValues == null) {
            initSpinnersValues();
        }

        return mSpinnerYearValues;
    }

    public List<Integer> getAvailMonths(int year) {
        if (mSpinnerMonthValues == null) {
            initSpinnersValues();
        }

        return mSpinnerMonthValues.get(year);
    }

    private void initSpinnersValues() {
        mSpinnerMonthValues = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);

        mSpinnerYearValues = new ArrayList<>();
        mSpinnerYearValues.add(thisYear);
        mSpinnerYearValues.add(thisYear + 1);

        int noOfMonthsInOneYear = 12;
        List<Integer> thisYearAvailMonths = new ArrayList<>();
        for (int i = thisMonth; i < noOfMonthsInOneYear; i++) {
            thisYearAvailMonths.add(i + 1);
        }

        List<Integer> nextYearAvailMonths = new ArrayList<>();
        for (int i = 0; i < noOfMonthsInOneYear; i++) {
            nextYearAvailMonths.add(i + 1);
        }

        mSpinnerMonthValues.put(thisYear, thisYearAvailMonths);
        mSpinnerMonthValues.put(thisYear + 1, nextYearAvailMonths);
    }

    public List<Airport> getAirports() {
        if (mAirports == null) {
            mAirports = new ArrayList<>();

            mAirports.add(new Airport("SGN", "Tân Sơn Nhất"));
            mAirports.add(new Airport("HAN", "Nội Bài"));
            mAirports.add(new Airport("VCS", "Côn Đảo"));
            mAirports.add(new Airport("UIH", "Phù Cát"));
            mAirports.add(new Airport("CAH", "Cà Mau"));
            mAirports.add(new Airport("VCA", "Cần Thơ"));
            mAirports.add(new Airport("BMV", "Buôn Ma Thuột"));
            mAirports.add(new Airport("DAD", "Đà Nẵng"));
            mAirports.add(new Airport("DIN", "Điện Biên Phủ"));
            mAirports.add(new Airport("PXU", "Pleiku"));
            mAirports.add(new Airport("HPH", "Cát Bi"));
            mAirports.add(new Airport("CXR", "Cam Ranh"));
            mAirports.add(new Airport("VKG", "Rạch Giá"));
            mAirports.add(new Airport("PQC", "Phú Quốc"));
            mAirports.add(new Airport("DLI", "Liên Khương"));
            mAirports.add(new Airport("TBB", "Tuy Hòa"));
            mAirports.add(new Airport("VDH", "Đồng Hới"));
            mAirports.add(new Airport("VCL", "Chu Lai"));
            mAirports.add(new Airport("THD", "Thọ Xuân"));
            mAirports.add(new Airport("HUI", "Phú Bài"));
            mAirports.add(new Airport("VII", "Vinh"));
        }

        return mAirports;
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
                Map<String, String> headers = RequestHelper.getDefaultHeaders();
                headers.put("Request_Hash", RequestHelper.requestHashBuilder(srcPort, dstPort,
                        carrier, strYear, strMonth));
                headers.put("Request_Carrier", carrier);

                mPricesAPI.getPricePerDay(headers, postData, carrier, srcPort, dstPort)
                        .enqueue(new Callback<List<PricePerDayResponse>>() {
                            @Override
                            public void onResponse(Call<List<PricePerDayResponse>> call,
                                                   Response<List<PricePerDayResponse>> response) {
                                if (response.isSuccessful()) {
                                    int minPriceTotal = 1000000000;
                                    int minPriceBeforeTax = 0;

                                    List<PricePerDayResponse> pricePerDayResponses = response.body();
                                    for (int i = 0; i < pricePerDayResponses.size(); i++) {
                                        PricePerDayResponse pricePerDayResponse = pricePerDayResponses.get(i);
                                        List<PricePerDay> priceList = pricePerDayResponse.getPriceList();
                                        PricePerDay price = priceList != null && priceList.size() > 0 ? priceList.get(0) : null;
                                        if (price != null && price.getPriceTotal() < minPriceTotal) {
                                            minPriceTotal = price.getPriceTotal();
                                            minPriceBeforeTax = price.getPrice();
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
                                        mPrices[targetIndex] = minPricePerDay;
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
        if (mNoOfReceivedRequests == Constants.CARRIERS.length * mPrices.length) {
            List<PricePerDay> lstPrices = new ArrayList<>();
            lstPrices.addAll(Arrays.asList(mPrices));

            Calendar cal = Calendar.getInstance();
            cal.set(mQueryingYear, mQueryingMonth - 1, mQueryingStartDay);

            for (int i = 0; i < cal.get(Calendar.DAY_OF_WEEK) - 1; i++) {
                lstPrices.add(0, null);
            }

            mPresenter.onGetPricesResponse(lstPrices);
        }
    }
}
