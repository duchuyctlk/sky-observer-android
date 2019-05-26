package com.huynd.skyobserver.utils

import com.huynd.skyobserver.BuildConfig
import java.lang.AssertionError

/**
 * Created by HuyND on 8/6/2017.
 */

class Constants {
    init {
        throw AssertionError()
    }
    
    companion object {
        const val API_URL = BuildConfig.API_URL
        const val ICON_URL = BuildConfig.ICON_URL
        const val HEADER_CONTENT_TYPE = BuildConfig.HEADER_CONTENT_TYPE

        @kotlin.jvm.JvmField
        var CARRIERS: Array<String> = arrayOf("VJ", "BL", "VN")

        const val CONVENIENCE_FEE_IN_K = 70
    }
}
