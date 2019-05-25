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

        val COOKIES_DATA = BuildConfig.COOKIES_DATA.replace("'", "\"")

        const val HEADER_HOST = BuildConfig.HEADER_HOST
        const val HEADER_CONNECTION = BuildConfig.HEADER_CONNECTION
        const val HEADER_CONTENT_LENGTH = BuildConfig.HEADER_CONTENT_LENGTH
        const val HEADER_REQUEST_HASH = BuildConfig.HEADER_REQUEST_HASH
        const val HEADER_ORIGIN = BuildConfig.HEADER_ORIGIN
        const val HEADER_REQUEST_CARRIER = BuildConfig.HEADER_REQUEST_CARRIER
        const val HEADER_USER_AGENT = BuildConfig.HEADER_USER_AGENT
        const val HEADER_CONTENT_TYPE = BuildConfig.HEADER_CONTENT_TYPE
        const val HEADER_ACCEPT = BuildConfig.HEADER_ACCEPT
        const val HEADER_REFERER = BuildConfig.HEADER_REFERER
        const val HEADER_ACCEPT_ENCODING = BuildConfig.HEADER_ACCEPT_ENCODING
        const val HEADER_ACCEPT_LANGUAGE = BuildConfig.HEADER_ACCEPT_LANGUAGE

        @kotlin.jvm.JvmField
        var CARRIERS: Array<String> = arrayOf("VJ", "BL", "VN")

        const val CONVENIENCE_FEE_IN_K = 70
    }
}
