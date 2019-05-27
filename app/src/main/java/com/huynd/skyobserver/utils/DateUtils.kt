package com.huynd.skyobserver.utils;

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by HuyND on 8/15/2017.
 */

class DateUtils {
    init {
        throw AssertionError()
    }

    companion object {
        fun convertStringToDate(strDate: String?): Date? =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        .let {
                            try {
                                it.parse(strDate)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                                null
                            }
                        }

        private fun isLeapYear(year: Int) =
                if (year % 100 == 0) {
                    year % 400 == 0
                } else {
                    year % 4 == 0
                }

        fun getNumberOfDaysInMonth(year: Int, month: Int) =
                if (month == 2) {
                    if (isLeapYear(year)) {
                        29
                    } else {
                        28
                    }
                } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                    30
                } else {
                    31
                }

        fun getStartYear() = Calendar.getInstance().get(Calendar.YEAR)

        fun getStartMonth() = Calendar.getInstance().get(Calendar.MONTH)

        fun getStartDayOfMonth() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        fun getMinDate() = Calendar.getInstance().timeInMillis

        fun dateToString(year: Int, month: Int): String =
                GregorianCalendar(year, month, 1).let {
                    DateFormat.format("MM/yyyy", it).toString()
                }

        fun dateToString(year: Int, month: Int, dayOfMonth: Int): String =
                GregorianCalendar(year, month, dayOfMonth).let {
                    DateFormat.format("dd/MM/yyyy", it).toString()
                }
    }
}
