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
                put("Content-Type", Constants.HEADER_CONTENT_TYPE)
            }
        }

        fun requestHashBuilder(srcPort: String, dstPort: String, carrier: String,
                               strYear: String, strMonth: String) =
                "${carrier}_${srcPort}_${dstPort}_$strYear${strMonth}01"

        fun airlinesIconUrlBuilder(carrier: String) = "${Constants.ICON_URL}${carrier}0.png"
    }
}
