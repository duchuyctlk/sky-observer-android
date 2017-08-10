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
}
