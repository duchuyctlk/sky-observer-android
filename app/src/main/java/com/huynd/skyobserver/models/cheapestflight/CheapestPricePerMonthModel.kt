package com.huynd.skyobserver.models.cheapestflight

import android.annotation.SuppressLint
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.AirportPriceInfoComparator
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirports
import com.huynd.skyobserver.utils.CountryAirportUtils.getCountryByCode
import com.huynd.skyobserver.utils.CountryPriceInfoComparator
import com.huynd.skyobserver.utils.RequestHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by HuyND on 6/04/2019.
 */

class CheapestPricePerMonthModel {
    private var mAirports: List<Airport> = getAirports()
    private var mListCountryPriceInfo: MutableList<CountryPriceInfo> = mutableListOf()

    private var mNoOfReceivedOutboundRequests: Int = 0
    private var mNoOfReceivedInboundRequests: Int = 0

    private var mNoOfSentOutboundRequests: Int = 0
    private var mNoOfSentInboundRequests: Int = 0

    private var isOutboundLoadingDone: Boolean = false
    private var isInboundLoadingDone: Boolean = false
    private var willLoadInbound: Boolean = false

    private var mListener: EventListener? = null

    interface EventListener {
        fun notifyInvalidDate()

        fun onGetPricesResponse(listCountryPriceInfo: List<CountryPriceInfo>)
    }

    fun getPrices(pricesAPI: PricesAPI,
                  yearOutbound: Int, monthOutbound: Int,
                  yearInbound: Int, monthInbound: Int,
                  port: String, isReturnTrip: Boolean) {
        // reset counts
        mListCountryPriceInfo.clear()

        mNoOfReceivedOutboundRequests = 0
        mNoOfReceivedInboundRequests = 0

        isOutboundLoadingDone = false
        isInboundLoadingDone = false
        willLoadInbound = isReturnTrip

        // get outbound prices
        val outboundSent = getPrices(pricesAPI, yearOutbound, monthOutbound, port, true)

        // get inbound prices
        if (outboundSent == 0 && isReturnTrip) {
            getPrices(pricesAPI, yearInbound, monthInbound, port, false)
        }
    }

    @SuppressLint("CheckResult")
    private fun getPrices(pricesAPI: PricesAPI, year: Int, month: Int,
                          originPort: String, isOutbound: Boolean): Int {
        val cal = Calendar.getInstance()
        val thisYear = cal.get(Calendar.YEAR)
        val thisMonth = cal.get(Calendar.MONTH) + 1
        val today = cal.get(Calendar.DAY_OF_MONTH)

        val invalidDate = (year < thisYear)
                || (year == thisYear && month < thisMonth)

        if (invalidDate) {
            mListener?.notifyInvalidDate()
            return -1
        } else {
            val strYear = "$year"
            val strMonth = formatDayMonthWithZero(month)
            val strDay = formatDayMonthWithZero(today)

            val headers = RequestHelper.getDefaultHeaders()
            val postData = CheapestPricePerMonthBody(strYear, strMonth, strDay).apply {
                setRoutes(mAirports
                        .filter { it.id != originPort }
                        .map { if (isOutbound) "$originPort${it.id}" else "${it.id}$originPort" }
                )
            }
            val observableList: Observable<List<CheapestPricePerMonthResponse>> =
                    pricesAPI.getCheapestPricePerMonth(headers, postData)
            observableList
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cheapestPricePerMonthResponses ->
                        val groupResponseByPort: Map<String, List<CheapestPricePerMonthResponse>>
                        if (isOutbound) {
                            groupResponseByPort = cheapestPricePerMonthResponses.groupBy { it.id.destination }
                            mNoOfSentOutboundRequests = groupResponseByPort.size
                        } else {
                            groupResponseByPort = cheapestPricePerMonthResponses.groupBy { it.id.origin }
                            mNoOfSentInboundRequests = groupResponseByPort.size
                        }

                        groupResponseByPort.values.forEach { listResponseByPort ->
                            val dayWithCheapestPrice = listResponseByPort.minBy { it.cheapestPrice }!!
                            val sourcePort = dayWithCheapestPrice.id.origin
                            val destinationPort = dayWithCheapestPrice.id.destination

                            val trueDestinationPort = if (isOutbound) destinationPort else sourcePort
                            val country = getCountryByCode(getAirportById(trueDestinationPort).countryCode)

                            // get AirportPriceInfo for destination port
                            var dstAirportPriceInfo: AirportPriceInfo? = null
                            var listAirportPriceInfo: MutableList<AirportPriceInfo>? = null
                            val dstCountryPriceInfo = mListCountryPriceInfo.firstOrNull { countryPriceInfo ->
                                countryPriceInfo.country.countryCode == country.countryCode
                            }
                            dstCountryPriceInfo?.run {
                                listAirportPriceInfo = this.airportPriceInfos
                                dstAirportPriceInfo = listAirportPriceInfo?.firstOrNull { info ->
                                    info.getAirportId() == trueDestinationPort
                                }
                            }
                            // add new entry for destination (country & airport)
                            if (dstAirportPriceInfo == null) {
                                val airportPriceInfo = AirportPriceInfo().apply {
                                    setAirport(getAirportById(trueDestinationPort))
                                }

                                if (listAirportPriceInfo == null) {
                                    listAirportPriceInfo = mutableListOf()
                                }
                                listAirportPriceInfo!!.add(airportPriceInfo)

                                if (dstCountryPriceInfo == null) {
                                    mListCountryPriceInfo.add(CountryPriceInfo()
                                            .apply {
                                                this.airportPriceInfos = listAirportPriceInfo
                                                this.country = country
                                            })
                                }

                                dstAirportPriceInfo = airportPriceInfo
                            }

                            val cheapestDay = dayWithCheapestPrice.id.dayInMonth
                            val strCheapestDay = formatDayMonthWithZero(cheapestDay)
                            val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strCheapestDay)
                            val observableCheapestDay: Observable<List<PricePerDayResponse>> =
                                    pricesAPI.getPricePerDay(headers, postDataForDayRequest,
                                            dayWithCheapestPrice.carrier,
                                            sourcePort,
                                            destinationPort)
                            observableCheapestDay
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ pricePerDayResponses ->
                                        pricePerDayResponses.forEach { pricePerDayResponse ->
                                            pricePerDayResponse.priceList?.firstOrNull()?.apply {
                                                carrier = pricePerDayResponse.provider
                                                setArrivalTime(pricePerDayResponse.arrivalTime)
                                                setDepartureTime(pricePerDayResponse.arrivalTime)

                                                if (isOutbound) {
                                                    val minPrice = dstAirportPriceInfo!!.getBestPriceOutbound()
                                                    if (minPrice == 0 || priceTotal < minPrice) {
                                                        dstAirportPriceInfo!!.setPricePerDayOutbound(this)
                                                    }
                                                } else {
                                                    val minPrice = dstAirportPriceInfo!!.getBestPriceInbound()
                                                    if (minPrice == 0 || priceTotal < minPrice) {
                                                        dstAirportPriceInfo!!.setPricePerDayInbound(this)
                                                    }
                                                }
                                            }
                                        }
                                        returnReceivedPricesWhenFull(isOutbound)
                                    }, {
                                        returnReceivedPricesWhenFull(isOutbound)
                                    })
                        }
                    }, {
                        returnReceivedPricesWhenFull(isOutbound,
                                if (isOutbound) mNoOfSentOutboundRequests else mNoOfSentInboundRequests)
                    })
            return 0
        }
    }

    private fun returnReceivedPricesWhenFull(isOutbound: Boolean, noOfFinishedRequests: Int = 1) {
        if (isOutbound) {
            mNoOfReceivedOutboundRequests += noOfFinishedRequests
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == mNoOfSentOutboundRequests
        } else {
            mNoOfReceivedInboundRequests += noOfFinishedRequests
            isInboundLoadingDone = mNoOfReceivedInboundRequests == mNoOfSentInboundRequests
        }

        if (willLoadInbound) {
            if (isOutboundLoadingDone && isInboundLoadingDone) {
                sortListCountryPriceInfoAndReturnResponse()
            }
        } else {
            if (isOutboundLoadingDone) {
                sortListCountryPriceInfoAndReturnResponse()
            }
        }
    }

    private fun sortListCountryPriceInfoAndReturnResponse() {
        mListener?.run {
            mListCountryPriceInfo.forEach { countryPriceInfo ->
                countryPriceInfo.airportPriceInfos =
                        countryPriceInfo.airportPriceInfos.filter { airportPriceInfo ->
                            airportPriceInfo.getOutboundCarrier() != null
                                    && airportPriceInfo.getInboundCarrier() != null
                        }.toMutableList().apply {
                            sortWith(AirportPriceInfoComparator.getInstance())
                        }
            }
            onGetPricesResponse(
                    mListCountryPriceInfo.filter { countryPriceInfo ->
                        countryPriceInfo.airportPriceInfoCount > 0
                    }.toMutableList().apply {
                        sortWith(CountryPriceInfoComparator.getInstance())
                    }
            )
        }
    }

    private fun formatDayMonthWithZero(x: Int) = if (x < 10) "0$x" else "$x"

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }
}
