package com.huynd.skyobserver.models;

import android.text.format.DateFormat;

import com.huynd.skyobserver.utils.AirportUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayModel {
    private Calendar mCalendar;

    public ChooseOneDayModel() {
        mCalendar = Calendar.getInstance();
    }

    public int getStartYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getStartMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getStartDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public String dateToString(int year, int month, int dayOfMonth) {
        GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
        String strDate = DateFormat.format("dd/MM/yyyy", date).toString();
        return strDate;
    }

    public List<Airport> getAirports() {
        return AirportUtils.getAirports();
    }

    public long getMinDate() {
        return mCalendar.getTimeInMillis();
    }
}
