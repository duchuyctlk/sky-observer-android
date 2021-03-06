package com.huynd.skyobserver.models.cheapestflight.month

import android.annotation.SuppressLint
import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.PricePerDayBody
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.entities.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.entities.cheapestflight.month.MonthCheapestBody
import com.huynd.skyobserver.entities.cheapestflight.month.ResponseId
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.AirportPriceInfoComparator
import com.huynd.skyobserver.utils.CoroutineUtils.Companion.startComputingThread
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.CountryAirportUtils.getCountryByCode
import com.huynd.skyobserver.utils.CountryPriceInfoComparator
import com.huynd.skyobserver.utils.RequestHelper
import com.huynd.skyobserver.utils.StringUtils.Companion.formatDayMonthWithZero
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by HuyND on 6/04/2019.
 */

@SuppressLint("CheckResult")
class MonthCheapestModel {
    interface EventListener {
        fun notifyInvalidDate()

        fun onGetPricesResponse(listCountryPriceInfo: List<CountryPriceInfo>)
    }

    private lateinit var outboundResponsesGroupedByPort: Map<String, List<CheapestPricePerMonthResponse>>
    private lateinit var inboundResponsesGroupedByPort: Map<String, List<CheapestPricePerMonthResponse>>

    private var outboundCheapestDayByPort = HashMap<String, CheapestPricePerMonthResponse>()
    private var inboundCheapestDayByPort = HashMap<String, CheapestPricePerMonthResponse>()

    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()
    private var mListCountryPriceInfo: MutableList<CountryPriceInfo> = mutableListOf()

    private var mNoOfReceivedOutboundRequests: Int = 0
    private var mNoOfReceivedInboundRequests: Int = 0

    private var mNoOfSentOutboundRequests: Int = 0
    private var mNoOfSentInboundRequests: Int = 0

    private var mIsReturnTrip: Boolean = false
    private var mIsStep1OutboundLoaded: Boolean = false
    private var mIsStep1InboundLoaded: Boolean = false
    private var isOutboundLoadingDone: Boolean = false
    private var isInboundLoadingDone: Boolean = false

    private var mListener: EventListener? = null

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }

    fun getAirports() = mAirports

    fun getPrices(pricesAPI: PricesAPI,
                  yearOutbound: Int, monthOutbound: Int,
                  yearInbound: Int, monthInbound: Int,
                  port: String, isReturnTrip: Boolean) {
        // reset counts
        mListCountryPriceInfo.clear()
        mNoOfReceivedOutboundRequests = 0
        mNoOfReceivedInboundRequests = 0
        outboundCheapestDayByPort.clear()
        inboundCheapestDayByPort.clear()
        mIsStep1OutboundLoaded = false
        mIsStep1InboundLoaded = false
        isOutboundLoadingDone = false
        isInboundLoadingDone = false
        mIsReturnTrip = isReturnTrip

        // get outbound prices
        val outboundSent = getListOfAllDay(pricesAPI, yearOutbound, monthOutbound, port, true)

        // get inbound prices
        if (outboundSent == 0 && isReturnTrip) {
            getListOfAllDay(pricesAPI, yearInbound, monthInbound, port, false)
        }
    }

    private fun getListOfAllDay(pricesAPI: PricesAPI, year: Int, month: Int,
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
            CoroutineScope(Dispatchers.Default).launch {
                val strYear = "$year"
                val strMonth = formatDayMonthWithZero(month)
                val strDay = formatDayMonthWithZero(today)

                val headers = RequestHelper.getDefaultHeaders()
                val postData = MonthCheapestBody(strYear, strMonth, strDay).apply {
                    setRoutes(mAirports
                            .filter { it.id != originPort }
                            .map { if (isOutbound) "$originPort${it.id}" else "${it.id}$originPort" }
                    )
                }

                withContext(Dispatchers.IO) {
                    var cheapestPricePerMonthResponses: List<CheapestPricePerMonthResponse>? = null
                    try {
                        cheapestPricePerMonthResponses = pricesAPI.getListCheapestPricePerMonth(headers, postData)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                    withContext(Dispatchers.Main) {
                        if (cheapestPricePerMonthResponses != null) {
                            if (isOutbound) {
                                outboundResponsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.destination }
                                mIsStep1OutboundLoaded = true
                            } else {
                                inboundResponsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.origin }
                                mIsStep1InboundLoaded = true
                            }

                            val step1Done =
                                    if (mIsReturnTrip) mIsStep1OutboundLoaded && mIsStep1InboundLoaded else mIsStep1OutboundLoaded

                            if (step1Done) {
                                findCheapestDays(pricesAPI, originPort)
                            }
                        } else {
                            returnReceivedPricesWhenFull(isOutbound, 0)
                        }
                    }
                }
            }
            return 0
        }
    }

    private fun findCheapestDays(pricesAPI: PricesAPI, originPort: String) {
        startComputingThread({
            if (mIsReturnTrip) {
                val listOutboundPorts = outboundResponsesGroupedByPort.keys
                val listInboundPorts = outboundResponsesGroupedByPort.keys
                val validPorts = listOutboundPorts.intersect(listInboundPorts)
                validPorts.forEach { port ->
                    val listOutboundResponseAllDays = outboundResponsesGroupedByPort[port]
                    val listInboundResponseAllDays = inboundResponsesGroupedByPort[port]

                    listOutboundResponseAllDays?.forEach { outboundOneDayResponse ->
                        val inboundOneDayResponse = listInboundResponseAllDays
                                ?.filter { it.id.dayInMonth > outboundOneDayResponse.id.dayInMonth }
                                ?.minBy { it.cheapestPrice }

                        if (inboundOneDayResponse != null) {
                            if (outboundCheapestDayByPort[port] == null
                                    || inboundCheapestDayByPort[port] == null) {
                                outboundCheapestDayByPort[port] = outboundOneDayResponse
                                inboundCheapestDayByPort[port] = inboundOneDayResponse
                            } else {
                                val currentCheapestPrice = outboundCheapestDayByPort[port]!!.cheapestPrice + inboundCheapestDayByPort[port]!!.cheapestPrice
                                val calculatingPrice = outboundOneDayResponse.cheapestPrice + inboundOneDayResponse.cheapestPrice
                                if (currentCheapestPrice > calculatingPrice) {
                                    outboundCheapestDayByPort[port] = outboundOneDayResponse
                                    inboundCheapestDayByPort[port] = inboundOneDayResponse
                                }
                            }
                        }
                    }
                }
            } else {
                outboundCheapestDayByPort = HashMap()
                outboundResponsesGroupedByPort.forEach { entry ->
                    val port = entry.key
                    val listResponse = entry.value
                    val cheapestResponse = listResponse.minBy { it.cheapestPrice }

                    cheapestResponse?.run {
                        outboundCheapestDayByPort[port] = cheapestResponse
                    }
                }
            }
        }, {
            getDetailPrices(pricesAPI, originPort)
        })
    }

    private fun getDetailPricesOnePortOneDay(pricesAPI: PricesAPI,
                                             headers: Map<String, String>,
                                             cheapestResponse: CheapestPricePerMonthResponse,
                                             originPort: String, destination: String,
                                             isOutbound: Boolean) {
        val cheapestResponseId = cheapestResponse.id
        val strDay = formatDayMonthWithZero(cheapestResponseId.dayInMonth)
        val strYear = formatDayMonthWithZero(cheapestResponseId.year)
        val strMonth = formatDayMonthWithZero(cheapestResponseId.monthInYear)
        val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)

        val airport: Airport = getAirportById(destination)
        var countryPriceInfo = mListCountryPriceInfo.find { it.country.countryCode == airport.getCountryCode() }
        if (countryPriceInfo == null) {
            countryPriceInfo = CountryPriceInfo().apply {
                country = getCountryByCode(airport.getCountryCode())
                airportPriceInfos = mutableListOf()
            }
            mListCountryPriceInfo.add(countryPriceInfo)
        }
        var airportPriceInfo = countryPriceInfo.airportPriceInfos.find { it.getAirportId() == airport.id }
        if (airportPriceInfo == null) {
            airportPriceInfo = AirportPriceInfo().apply {
                setAirport(airport)
            }
            countryPriceInfo.airportPriceInfos.add(airportPriceInfo)
        }

        CoroutineScope(Dispatchers.IO).launch {
            var pricePerDayResponses: List<PricePerDayResponse>? = null
            try {
                pricePerDayResponses = if (isOutbound) {
                    pricesAPI.getListPricePerDay(headers, postDataForDayRequest,
                            cheapestResponse.carrier,
                            originPort,
                            destination)
                } else {
                    pricesAPI.getListPricePerDay(headers, postDataForDayRequest,
                            cheapestResponse.carrier,
                            destination,
                            originPort)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                try {
                    pricePerDayResponses?.run {
                        handleGetDetailPricesOnePortOneDayResult(this, cheapestResponseId, airportPriceInfo, isOutbound)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                returnReceivedPricesWhenFull(isOutbound)
            }
        }
    }

    private fun handleGetDetailPricesOnePortOneDayResult(pricePerDayResponses: List<PricePerDayResponse>,
                                                         cheapestResponseId: ResponseId,
                                                         airportPriceInfo: AirportPriceInfo,
                                                         isOutbound: Boolean) {
        pricePerDayResponses.forEach { pricePerDayResponse ->
            pricePerDayResponse.priceList?.firstOrNull()?.apply {
                carrier = pricePerDayResponse.provider
                setArrivalTime(pricePerDayResponse.arrivalTime)
                setDepartureTime(pricePerDayResponse.arrivalTime)

                if (isOutbound) {
                    val minPrice = airportPriceInfo.getBestPriceOutbound()
                    if (minPrice == 0.0 || priceTotal < minPrice) {
                        this.day = cheapestResponseId.dayInMonth
                        airportPriceInfo.setPricePerDayOutbound(this)
                    }
                } else {
                    val minPrice = airportPriceInfo.getBestPriceInbound()
                    if (minPrice == 0.0 || priceTotal < minPrice) {
                        this.day = cheapestResponseId.dayInMonth
                        airportPriceInfo.setPricePerDayInbound(this)
                    }
                }
            }
        }
    }

    private fun getDetailPrices(pricesAPI: PricesAPI, originPort: String) {
        val headers = RequestHelper.getDefaultHeaders()
        mNoOfSentOutboundRequests = outboundCheapestDayByPort.size
        outboundCheapestDayByPort.forEach { entry ->
            getDetailPricesOnePortOneDay(pricesAPI, headers, entry.value, originPort, entry.key, true)
        }

        if (mIsReturnTrip) {
            mNoOfSentInboundRequests = inboundCheapestDayByPort.size
            inboundCheapestDayByPort.forEach { entry ->
                getDetailPricesOnePortOneDay(pricesAPI, headers, entry.value, originPort, entry.key, false)
            }
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

        val isLoadingDone =
                if (mIsReturnTrip) isOutboundLoadingDone && isInboundLoadingDone else isOutboundLoadingDone

        if (isLoadingDone) {
            sortListCountryPriceInfoAndReturnResponse()
        }
    }

    private fun sortListCountryPriceInfoAndReturnResponse() {
        mListener?.run {
            startComputingThread({
                val airportPriceInfoComparator = AirportPriceInfoComparator.getInstance()
                mListCountryPriceInfo.forEach { countryPriceInfo ->
                    var listAirportPriceInfo = countryPriceInfo.airportPriceInfos.filter { it.getOutboundCarrier() != null }
                    if (mIsReturnTrip) {
                        listAirportPriceInfo = listAirportPriceInfo.filter { it.getInboundCarrier() != null }
                    }

                    countryPriceInfo.airportPriceInfos = listAirportPriceInfo
                            .toMutableList()
                            .apply {
                                sortWith(airportPriceInfoComparator)
                            }
                }
                mListCountryPriceInfo
                        .filter { it.airportPriceInfoCount > 0 }
                        .toMutableList()
                        .apply {
                            sortWith(CountryPriceInfoComparator.getInstance())
                        }
            }, { listCountryPriceInfo ->
                mListener?.onGetPricesResponse(listCountryPriceInfo)
            })
        }
    }
}
