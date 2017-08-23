package com.huynd.skyobserver.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.huynd.skyobserver.utils.DateUtils.getAvailableMonthsByYears;
import static com.huynd.skyobserver.utils.DateUtils.getAvailableYears;
import static com.huynd.skyobserver.utils.DateUtils.getCurrentDayOfMonth;
import static com.huynd.skyobserver.utils.DateUtils.isLeapYear;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayModel {
    private List<AvailableMonth> mSpinnerMonthValues;
    private List<Integer> mSpinnerYearValues;

    public List<AvailableMonth> getAvailMonths() {
        if (mSpinnerMonthValues == null) {
            initSpinnersValues();
        }

        return mSpinnerMonthValues;
    }

    public List<Integer> getAvailDays(int year, int month) {
        int numberOfDays = 31;

        if (month == 2) {
            if (isLeapYear(year)) {
                numberOfDays = 29;
            } else {
                numberOfDays = 28;
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            numberOfDays = 30;
        }

        List<Integer> days = new ArrayList<>();
        AvailableMonth thisMonth = mSpinnerMonthValues.get(0);
        int startDay = thisMonth.getYear() == year && thisMonth.getMonth() == month ? getCurrentDayOfMonth() + 1 : 1;
        for (int day = startDay; day <= numberOfDays; day++) {
            days.add(day);
        }

        return days;
    }

    private void initSpinnersValues() {
        mSpinnerYearValues = getAvailableYears();
        Map<Integer, List<Integer>> monthYearMap = getAvailableMonthsByYears(mSpinnerYearValues);

        mSpinnerMonthValues = new ArrayList<>();
        for (int i = 0; i < mSpinnerYearValues.size(); i++) {
            int year = mSpinnerYearValues.get(i);
            List<Integer> months = monthYearMap.get(year);
            for (int j = 0; j < months.size(); j++) {
                mSpinnerMonthValues.add(new AvailableMonth(year, months.get(j)));
            }
        }
    }
}
