package com.twinkle94.monthyearpicker.picker;

import android.content.Context;

import com.huynd.skyobserver.utils.DateUtils;

import java.lang.reflect.Field;

/**
 * Created by HuyND on 4/18/2018.
 */

public class SkyObserverYearMonthPickerDialog extends YearMonthPickerDialog {

    public SkyObserverYearMonthPickerDialog(Context context, OnDateSetListener onDateSetListener) {
        super(context, onDateSetListener);
    }

    public int getYear() {
        int year = DateUtils.Companion.getStartYear();

        try {
            Field fieldYear = getClass().getSuperclass().getDeclaredField("mYear");
            fieldYear.setAccessible(true);
            year = fieldYear.getInt(this);
            fieldYear.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return year;
    }

    public int getMonth() {
        int month = DateUtils.Companion.getStartMonth();

        try {
            Field fieldMonth = getClass().getSuperclass().getDeclaredField("mMonth");
            fieldMonth.setAccessible(true);
            month = fieldMonth.getInt(this);
            fieldMonth.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return month;
    }
}
