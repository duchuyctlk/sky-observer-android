package com.huynd.skyobserver.services;

import com.huynd.skyobserver.models.PricePerDayResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

/**
 * Created by HuyND on 8/6/2017.
 */

public interface PricesAPI {
    @GET("{carrier}/{srcPort}/{dstPort}")
    Call<List<PricePerDayResponse>> getPricePerDay(@HeaderMap Map<String, String> headers, @Path("carrier") String carrier);
}
