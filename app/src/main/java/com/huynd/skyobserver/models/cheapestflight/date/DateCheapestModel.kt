package com.huynd.skyobserver.models.cheapestflight.date

import android.annotation.SuppressLint
import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.PricePerDayBody
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.AirportPriceInfoComparator
import com.huynd.skyobserver.utils.Constants
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.CountryAirportUtils.getCountryByCode
import com.huynd.skyobserver.utils.CountryPriceInfoComparator
import com.huynd.skyobserver.utils.RequestHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by HuyND on 11/19/2017.
 */

class DateCheapestModel {
    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()
    private var mCountryPriceInfos: MutableList<CountryPriceInfo> = mutableListOf()

    private var mNoOfReceivedOutboundRequests: Int = 0
    private var mNoOfReceivedInboundRequests: Int = 0

    private var isOutboundLoadingDone: Boolean = false
    private var isInboundLoadingDone: Boolean = false
    private var willLoadInbound: Boolean = false

    private var mListener: EventListener? = null

    interface EventListener {
        fun notifyInvalidDate()

        fun onGetPricesResponse(listCountryPriceInfo: List<CountryPriceInfo>)
    }

    fun getPrices(pricesAPI: PricesAPI,
                  yearOutbound: Int, monthOutbound: Int, dayOutbound: Int,
                  yearInbound: Int, monthInbound: Int, dayInbound: Int,
                  port: String, isReturnTrip: Boolean) {
        // reset counts
        mCountryPriceInfos.clear()

        mNoOfReceivedOutboundRequests = 0
        mNoOfReceivedInboundRequests = 0

        isOutboundLoadingDone = false
        isInboundLoadingDone = false
        willLoadInbound = isReturnTrip

        // get outbound prices
        val outboundSent = getPrices(pricesAPI, yearOutbound, monthOutbound, dayOutbound, port, true)

        // get inbound prices
        if (outboundSent == 0 && isReturnTrip) {
            getPrices(pricesAPI, yearInbound, monthInbound, dayInbound, port, false)
        }
    }

    @SuppressLint("CheckResult")
    private fun getPrices(pricesAPI: PricesAPI, year: Int, month: Int, day: Int,
                          originPort: String, isOutbound: Boolean): Int {
        val cal = Calendar.getInstance()
        val thisYear = cal.get(Calendar.YEAR)
        val thisMonth = cal.get(Calendar.MONTH) + 1
        val today = cal.get(Calendar.DAY_OF_MONTH)

        val invalidDate = (year < thisYear)
                || (year == thisYear && month < thisMonth)
                || (year == thisYear && month == thisMonth && day < today)

        if (invalidDate) {
            mListener?.notifyInvalidDate()
            return -1
        } else {
            val strYear = "$year"
            val strMonth = if (month < 10) "0$month" else "$month"
            val strDay = if (day < 10) "0$day" else "$day"

            val postData = PricePerDayBody(strYear, strMonth, strDay)
            val headers = RequestHelper.getDefaultHeaders()

            mAirports.forEach { airport ->
                if (originPort == airport.id) {
                    return@forEach
                }

                Constants.CARRIERS.forEach { carrier ->
                    val srcPort = if (isOutbound) originPort else airport.id
                    val dstPort = if (isOutbound) airport.id else originPort

                    CoroutineScope(Dispatchers.IO).launch {
                        var pricePerDayResponses: List<PricePerDayResponse>? = null
                        try {
                            pricePerDayResponses = pricesAPI.getListPricePerDay(headers, postData, carrier, srcPort, dstPort)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        withContext(Dispatchers.Main) {
                            pricePerDayResponses?.run {
                                handleGetPricePerDayResult(this, airport, isOutbound)
                            }
                            returnReceivedPricesWhenFull(isOutbound)
                        }
                    }
                }
            }
            return 0
        }
    }

    private fun handleGetPricePerDayResult(pricePerDayResponses: List<PricePerDayResponse>,
                                           airport: Airport, isOutbound: Boolean) {
        pricePerDayResponses.forEach { pricePerDayResponse ->
            if (pricePerDayResponse.provider == null) {
                return@forEach
            }

            // determine destination port
            val destinationPort = if (isOutbound)
                pricePerDayResponse.destinationCode else
                pricePerDayResponse.originCode

            val country = getCountryByCode(getAirportById(destinationPort).getCountryCode())

            // get AirportPriceInfo for destination port
            var dstAirportPriceInfo: AirportPriceInfo? = null
            var listAirportPriceInfo: MutableList<AirportPriceInfo>? = null
            val dstCountryPriceInfo = mCountryPriceInfos.firstOrNull { countryPriceInfo ->
                countryPriceInfo.country.countryCode == country.countryCode
            }

            dstCountryPriceInfo?.run {
                listAirportPriceInfo = this.airportPriceInfos
                dstAirportPriceInfo = listAirportPriceInfo?.firstOrNull { info ->
                    info.getAirportId() == destinationPort
                }
            }

            // add new entry for destination (country & airport)
            if (dstAirportPriceInfo == null) {
                val airportPriceInfo = AirportPriceInfo().apply {
                    setAirport(airport)
                }

                if (listAirportPriceInfo == null) {
                    listAirportPriceInfo = mutableListOf()
                }
                listAirportPriceInfo!!.add(airportPriceInfo)

                if (dstCountryPriceInfo == null) {
                    mCountryPriceInfos.add(CountryPriceInfo()
                            .apply {
                                this.airportPriceInfos = listAirportPriceInfo
                                this.country = country
                            })
                }

                dstAirportPriceInfo = airportPriceInfo
            }

            pricePerDayResponse.priceList?.firstOrNull()?.apply {
                this.carrier = pricePerDayResponse.provider
                setArrivalTime(pricePerDayResponse.arrivalTime)
                setDepartureTime(pricePerDayResponse.arrivalTime)

                if (isOutbound) {
                    val minPrice = dstAirportPriceInfo!!.getBestPriceOutbound()
                    if (minPrice == 0.0 || priceTotal < minPrice) {
                        dstAirportPriceInfo!!.setPricePerDayOutbound(this)
                    }
                } else {
                    val minPrice = dstAirportPriceInfo!!.getBestPriceInbound()
                    if (minPrice == 0.0 || priceTotal < minPrice) {
                        dstAirportPriceInfo!!.setPricePerDayInbound(this)
                    }
                }
            }
        }
    }

    private fun returnReceivedPricesWhenFull(isOutbound: Boolean) {
        val numOfOneWayRequests = Constants.CARRIERS.size * (mAirports.size - 1)
        if (isOutbound) {
            mNoOfReceivedOutboundRequests++
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == numOfOneWayRequests
        } else {
            mNoOfReceivedInboundRequests++
            isInboundLoadingDone = mNoOfReceivedInboundRequests == numOfOneWayRequests
        }

        val isLoadingDone =
                if (willLoadInbound) isOutboundLoadingDone && isInboundLoadingDone else isOutboundLoadingDone

        if (isLoadingDone) {
            sortCountryPriceInfosAndReturnResponse()
        }
    }

    private fun sortCountryPriceInfosAndReturnResponse() {
        mListener?.run {
            mCountryPriceInfos.forEach { countryPriceInfo ->
                countryPriceInfo.airportPriceInfos =
                        countryPriceInfo.airportPriceInfos.filter { airportPriceInfo ->
                            airportPriceInfo.getOutboundCarrier() != null
                                    && airportPriceInfo.getInboundCarrier() != null
                        }.toMutableList().apply {
                            sortWith(AirportPriceInfoComparator.getInstance())
                        }
            }
            onGetPricesResponse(
                    mCountryPriceInfos.filter { countryPriceInfo ->
                        countryPriceInfo.airportPriceInfoCount > 0
                    }.toMutableList().apply {
                        sortWith(CountryPriceInfoComparator.getInstance())
                    }
            )
        }
    }

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }

    fun getAirports(): List<Airport> = mAirports
}
