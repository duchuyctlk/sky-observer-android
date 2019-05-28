package com.huynd.skyobserver.services;

import com.huynd.skyobserver.models.PricePerDayBody;
import com.huynd.skyobserver.models.PricePerDayResponse;
import com.huynd.skyobserver.models.cheapestflight.CheapestPricePerMonthBody;
import com.huynd.skyobserver.models.cheapestflight.CheapestPricePerMonthResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by HuyND on 8/6/2017.
 */

public interface PricesAPI {
    @POST("{carrier}/{srcPort}/{dstPort}")
    Observable<List<PricePerDayResponse>> getPricePerDay(@HeaderMap Map<String, String> headers,
                                                         @Body PricePerDayBody body,
                                                         @Path("carrier") String carrier,
                                                         @Path("srcPort") String srcPort,
                                                         @Path("dstPort") String dstPort);

    @POST("addon/prodash/getlist")
    Observable<List<CheapestPricePerMonthResponse>> getCheapestPricePerMonth(
            @HeaderMap Map<String, String> headers,
            @Body CheapestPricePerMonthBody body);
}
