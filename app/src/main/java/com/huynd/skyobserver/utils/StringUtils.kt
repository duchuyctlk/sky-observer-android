package com.huynd.skyobserver.utils

import java.text.NumberFormat

/**
 * Created by HuyND on 5/29/2019.
 */

fun formatNumber(number: Double): String = NumberFormat.getNumberInstance().format(number)

class StringUtils {
    init {
        throw AssertionError()
    }

    companion object {
        fun formatDayMonthWithZero(x: Int) = if (x < 10) "0$x" else "$x"
    }
}