package com.huynd.skyobserver.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HuyND on 8/7/2017.
 */

public class RequestHelper {
    public static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", Constants.HEADER_HOST);
        headers.put("Connection", Constants.HEADER_CONNECTION);
        headers.put("Content-Length", Constants.HEADER_CONTENT_LENGTH);
        headers.put("Request_Hash", Constants.HEADER_REQUEST_HASH);
        headers.put("Origin", Constants.HEADER_ORIGIN);
        headers.put("Request_Carrier", Constants.HEADER_REQUEST_CARRIER);
        headers.put("User-Agent", Constants.HEADER_USER_AGENT);
        headers.put("Content-Type", Constants.HEADER_CONTENT_TYPE);
        headers.put("Accept", Constants.HEADER_ACCEPT);
        headers.put("Referer", Constants.HEADER_REFERER);
        headers.put("Accept-Encoding", Constants.HEADER_ACCEPT_ENCODING);
        headers.put("Accept-Language", Constants.HEADER_ACCEPT_LANGUAGE);
        headers.put("Cookie", Constants.COOKIES_DATA);
        return headers;
    }

    public static String requestHashBuilder(String srcPort, String dstPort, String carrier,
                                            String strYear, String strMonth, String strDay) {
        return carrier + "_" + srcPort + "_" + dstPort + "_" + strYear + strMonth + "01";
    }

    public static Map<String, String> requestDataBuilder(String strYear, String strMonth, String strDay) {
        Map<String, String> data = new HashMap<>();
        data.put("departureDate", strYear + "-" + strMonth + "-" + strDay);
        data.put("returnDate", strYear + "-" + strMonth + "-" + strDay);
        data.put("isRoundTrip", "false");
        data.put("adultCount", "1");
        data.put("childCount", "0");
        data.put("infantCount", "0");
        return data;
    }
}
