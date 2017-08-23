package com.huynd.skyobserver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by HuyND on 8/15/2017.
 */

public class DateUtils {
    public DateUtils() {
        throw new AssertionError();
    }

    public static Date convertStringToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static List<Integer> getAvailableYears() {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);

        List<Integer> yearValues = new ArrayList<>();
        yearValues.add(thisYear);
        yearValues.add(thisYear + 1);

        return yearValues;
    }

    public static Map<Integer, List<Integer>> getAvailableMonthsByYears(List<Integer> years) {
        Map<Integer, List<Integer>> monthValues = new HashMap<>();
        for (int i = 0; i < years.size(); i++) {
            int year = years.get(i);
            monthValues.put(year, getAvailMonthsByYear(year));
        }

        return monthValues;
    }

    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        }

        if (year % 100 == 0 && year % 400 != 0) {
            return false;
        }

        return true;
    }

    private static List<Integer> getAvailMonthsByYear(int year) {
        List<Integer> availMonths = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);

        int noOfMonthsInOneYear = 12;
        if (year == thisYear) {
            int thisMonth = cal.get(Calendar.MONTH);
            for (int i = thisMonth; i < noOfMonthsInOneYear; i++) {
                availMonths.add(i + 1);
            }
        } else if (year > thisYear) {
            for (int i = 0; i < noOfMonthsInOneYear; i++) {
                availMonths.add(i + 1);
            }
        }

        return availMonths;
    }

    public static int getCurrentDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
