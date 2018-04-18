package com.huynd.skyobserver.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        }

        if (year % 100 == 0 && year % 400 != 0) {
            return false;
        }

        return true;
    }

    public static int getNumberOfDaysInMonth(int year, int month) {
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

        return numberOfDays;
    }

    public static int getStartYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getStartMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int getStartDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static long getMinDate() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String dateToString(int year, int month) {
        GregorianCalendar date = new GregorianCalendar(year, month, 01);
        String strDate = DateFormat.format("MM/yyyy", date).toString();
        return strDate;
    }

    public static String dateToString(int year, int month, int dayOfMonth) {
        GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
        String strDate = DateFormat.format("dd/MM/yyyy", date).toString();
        return strDate;
    }
}
