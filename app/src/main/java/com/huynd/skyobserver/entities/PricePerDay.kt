package com.huynd.skyobserver.entities

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.huynd.skyobserver.utils.DateUtils
import kotlinx.android.parcel.Parcelize


/**
 * Created by HuyND on 8/7/2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
class PricePerDay(@SerializedName("priceTotal")
                  var priceTotal: Double = 0.0,
                  @SerializedName("price")
                  var price: Double = 0.0,
                  var day: Int = 0,
                  private var arrivalTime: String = "",
                  private var departureTime: String = "",
                  var carrier: String = "") : Parcelable {

    fun getArrivalTime() = DateUtils.convertStringToDate(arrivalTime)

    fun setArrivalTime(arrivalTime: String) {
        this.arrivalTime = arrivalTime
    }

    fun getDepartureTime() = DateUtils.convertStringToDate(departureTime)

    fun setDepartureTime(departureTime: String) {
        this.departureTime = departureTime
    }
}
