package com.huynd.skyobserver.models;

import com.huynd.skyobserver.presenters.PricePerDayPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HuyND on 8/9/2017.
 */

public class PricePerDayModel {
    private PricePerDayPresenter mPresenter;
    private Map<Integer, List<Integer>> mSpinnerMonthValues;
    private List<Integer> mSpinnerYearValues;
    private List<Airport> mAirports;

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
}
