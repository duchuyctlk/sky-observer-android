package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.BuildConfig;

/**
 * Created by HuyND on 8/6/2017.
 */

public class Constants {
    public Constants() {
        throw new AssertionError();
    }

    public static String API_URL = BuildConfig.API_URL;

    public static String COOKIES_DATA = BuildConfig.COOKIES_DATA.replace("'", "\"");

    public static String HEADER_HOST = BuildConfig.HEADER_HOST;
    public static String HEADER_CONNECTION = BuildConfig.HEADER_CONNECTION;
    public static String HEADER_CONTENT_LENGTH = BuildConfig.HEADER_CONTENT_LENGTH;
    public static String HEADER_REQUEST_HASH = BuildConfig.HEADER_REQUEST_HASH;
    public static String HEADER_ORIGIN = BuildConfig.HEADER_ORIGIN;
    public static String HEADER_REQUEST_CARRIER = BuildConfig.HEADER_REQUEST_CARRIER;
    public static String HEADER_USER_AGENT = BuildConfig.HEADER_USER_AGENT;
    public static String HEADER_CONTENT_TYPE = BuildConfig.HEADER_CONTENT_TYPE;
    public static String HEADER_ACCEPT = BuildConfig.HEADER_ACCEPT;
    public static String HEADER_REFERER = BuildConfig.HEADER_REFERER;
    public static String HEADER_ACCEPT_ENCODING = BuildConfig.HEADER_ACCEPT_ENCODING;
    public static String HEADER_ACCEPT_LANGUAGE = BuildConfig.HEADER_ACCEPT_LANGUAGE;

    public static String[] CARRIERS = new String[]{"VJ", "BL", "VN"};
    public static int CONVENIENCE_FEE_IN_K = 70;
}
