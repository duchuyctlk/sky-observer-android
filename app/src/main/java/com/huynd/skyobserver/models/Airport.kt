package com.huynd.skyobserver.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by HuyND on 8/11/2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class Airport(val mId: String = "",
                   private val mName: String = "",
                   private val mCountryCode: String = "") : Parcelable {

    val id: String
        get() = mId

    fun getCountryCode() = mCountryCode

    override fun toString() = mName
}
