package com.huynd.skyobserver.services;

import com.huynd.skyobserver.models.PricePerDayResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by HuyND on 8/6/2017.
 */

public interface PricesAPI {
    @POST("{carrier}/{srcPort}/{dstPort}")
    Call<List<PricePerDayResponse>> getPricePerDay(@HeaderMap Map<String, String> headers,
                                                   @FieldMap Map<String, String> postData,
                                                   @Path("carrier") String carrier);
}
