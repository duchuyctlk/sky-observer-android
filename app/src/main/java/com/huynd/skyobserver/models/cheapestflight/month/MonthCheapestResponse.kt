package com.huynd.skyobserver.models.cheapestflight.month

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by HuyND on 6/3/2019.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class CheapestPricePerMonthResponse(
        @SerializedName("_id")
        val id: ResponseId = ResponseId(),
        @SerializedName("c")
        val cheapestPrice: Int = 0,
        var cheapestTotalPrice: Int = 0,
        @SerializedName("p")
        val carrier: String = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
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
) : Parcelable
