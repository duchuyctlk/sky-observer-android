package com.huynd.skyobserver.utils

import java.text.NumberFormat

/**
 * Created by HuyND on 5/29/2019.
 */

fun formatNumber(number: Int): String = NumberFormat.getNumberInstance().format(number)
