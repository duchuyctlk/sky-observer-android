package com.huynd.skyobserver.models.bestdates

import android.annotation.SuppressLint
import android.os.Parcelable
import com.huynd.skyobserver.models.cheapestflight.month.ResponseId
import kotlinx.android.parcel.Parcelize

/**
 * Created by HuyND on 6/29/2019.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class BestDatesInfo(
        var outboundId: ResponseId = ResponseId(),
        var inboundId: ResponseId = ResponseId(),
        var outboundTotalPrice: Int = 0,
        var inboundTotalPrice: Int = 0,
        var outboundCarrier: String = "",
        var inboundCarrier: String = ""
) : Parcelable
