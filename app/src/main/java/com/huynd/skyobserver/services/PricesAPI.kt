package com.huynd.skyobserver.services

import com.huynd.skyobserver.entities.PricePerDayBody
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.entities.cheapestflight.month.MonthCheapestBody
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by HuyND on 8/6/2017.
 */

interface PricesAPI {
    @POST("sapi/getprices/{carrier}/{srcPort}/{dstPort}")
    fun getPricePerDay(
            @HeaderMap headers: Map<String, String>,
            @Body body: PricePerDayBody,
            @Path("carrier") carrier: String,
            @Path("srcPort") srcPort: String,
            @Path("dstPort") dstPort: String): Observable<List<PricePerDayResponse>>

    // new approach
    @POST("sapi/getprices/{carrier}/{srcPort}/{dstPort}")
    suspend fun getListPricePerDay(
            @HeaderMap headers: Map<String, String>,
            @Body body: PricePerDayBody,
            @Path("carrier") carrier: String,
            @Path("srcPort") srcPort: String,
            @Path("dstPort") dstPort: String): List<PricePerDayResponse>

    @POST("addon/prodash/getlist")
    suspend fun getListCheapestPricePerMonth(
            @HeaderMap headers: Map<String, String>,
            @Body body: MonthCheapestBody): List<CheapestPricePerMonthResponse>
}
