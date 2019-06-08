package com.huynd.skyobserver.models.cheapestflight.month

import com.google.gson.annotations.SerializedName

/**
 * Created by HuyND on 6/3/2019.
 */
data class CheapestPricePerMonthResponse(
        @SerializedName("_id")
        val id: ResponseId = ResponseId(),
        @SerializedName("c")
        val cheapestPrice: Int = 0,
        @SerializedName("p")
        val carrier: String = ""
)

data class ResponseId(
        @SerializedName("dim")
        val dayInMonth: Int = 1,
        @SerializedName("m")
        val monthInYear: Int = 1,
        @SerializedName("y")
        val year: Int = 0,
        @SerializedName("o")
        val origin: String = "",
        @SerializedName("d")
        val destination: String = ""
)
