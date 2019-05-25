package com.huynd.skyobserver.utils

/**
 * Created by HuyND on 8/7/2017.
 */

class RequestHelper {
    init {
        throw AssertionError()
    }

    companion object {
        fun getDefaultHeaders(): Map<String, String> {
            return HashMap<String, String>().apply {
                put("Host", Constants.HEADER_HOST)
                put("Connection", Constants.HEADER_CONNECTION)
                put("Content-Length", Constants.HEADER_CONTENT_LENGTH)
                put("Request_Hash", Constants.HEADER_REQUEST_HASH)
                put("Origin", Constants.HEADER_ORIGIN)
                put("Request_Carrier", Constants.HEADER_REQUEST_CARRIER)
                put("User-Agent", Constants.HEADER_USER_AGENT)
                put("Content-Type", Constants.HEADER_CONTENT_TYPE)
                put("Accept", Constants.HEADER_ACCEPT)
                put("Referer", Constants.HEADER_REFERER)
                put("Accept-Encoding", Constants.HEADER_ACCEPT_ENCODING)
                put("Accept-Language", Constants.HEADER_ACCEPT_LANGUAGE)
                put("Cookie", Constants.COOKIES_DATA)
            }
        }

        fun requestHashBuilder(srcPort: String, dstPort: String, carrier: String,
                               strYear: String, strMonth: String) =
                "${carrier}_${srcPort}_${dstPort}_$strYear${strMonth}01"

        fun airlinesIconUrlBuilder(carrier: String) = "${Constants.ICON_URL}$carrier.jpg"
    }
}
