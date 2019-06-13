package com.huynd.skyobserver.models.cheapestflight.month

import android.annotation.SuppressLint
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.AirportPriceInfoComparator
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.CountryAirportUtils.getCountryByCode
import com.huynd.skyobserver.utils.CountryPriceInfoComparator
import com.huynd.skyobserver.utils.RequestHelper
import com.huynd.skyobserver.utils.StringUtils.Companion.formatDayMonthWithZero
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by HuyND on 6/04/2019.
 */

class MonthCheapestModel {
    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()
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

    fun getAirports() = mAirports

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

    /* new implementation */
    private lateinit var outboundResponsesGroupedByPort: Map<String, List<CheapestPricePerMonthResponse>>
    private lateinit var inboundResponsesGroupedByPort: Map<String, List<CheapestPricePerMonthResponse>>

    private var outboundCheapestDayByPort = HashMap<String, CheapestPricePerMonthResponse>()
    private var inboundCheapestDayByPort = HashMap<String, CheapestPricePerMonthResponse>()
    private var mIsReturnTrip: Boolean = false
    private var mIsStep1OutboundLoaded: Boolean = false
    private var mIsStep1InboundLoaded: Boolean = false

    fun getPrices2(pricesAPI: PricesAPI,
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

    @SuppressLint("CheckResult")
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
            val observableList: Observable<List<CheapestPricePerMonthResponse>> =
                    pricesAPI.getCheapestPricePerMonth(headers, postData)
            observableList
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cheapestPricePerMonthResponses ->
                        if (isOutbound) {
                            outboundResponsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.destination }
                            mIsStep1OutboundLoaded = true
                        } else {
                            inboundResponsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.origin }
                            mIsStep1OutboundLoaded = true
                        }

                        if (mIsStep1OutboundLoaded && mIsStep1OutboundLoaded) {
                            findCheapestDays()
                            getDetailPrices(pricesAPI, originPort)
                        }
                    }, {
                        returnReceivedPricesWhenFull2(isOutbound, 0)
                    })
            return 0
        }
    }

    private fun findCheapestDays() {
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
    }

    private fun getDetailPrices(pricesAPI: PricesAPI, originPort: String) {
        val headers = RequestHelper.getDefaultHeaders()
        mNoOfSentOutboundRequests = outboundCheapestDayByPort.size
        outboundCheapestDayByPort.forEach { entry ->
            val destination = entry.key
            val cheapestResponse = entry.value
            val strDay = formatDayMonthWithZero(cheapestResponse.id.dayInMonth)
            val strYear = formatDayMonthWithZero(cheapestResponse.id.year)
            val strMonth = formatDayMonthWithZero(cheapestResponse.id.monthInYear)
            val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)
            val observableCheapestDay: Observable<List<PricePerDayResponse>> =
                    pricesAPI.getPricePerDay(headers, postDataForDayRequest,
                            cheapestResponse.carrier,
                            originPort,
                            destination)

            val airport: Airport = getAirportById(destination)
            var countryPriceInfo = mListCountryPriceInfo.find { it.country.countryCode == airport.countryCode }
            if (countryPriceInfo == null) {
                countryPriceInfo = CountryPriceInfo().apply {
                    country = getCountryByCode(airport.countryCode)
                    airportPriceInfos = listOf()
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

            observableCheapestDay
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ pricePerDayResponses ->
                        pricePerDayResponses.forEach { pricePerDayResponse ->
                            pricePerDayResponse.priceList?.firstOrNull()?.apply {
                                carrier = pricePerDayResponse.provider
                                setArrivalTime(pricePerDayResponse.arrivalTime)
                                setDepartureTime(pricePerDayResponse.arrivalTime)

                                val minPrice = airportPriceInfo!!.getBestPriceOutbound()
                                if (minPrice == 0 || priceTotal < minPrice) {
                                    this.day = cheapestResponse.id.dayInMonth
                                    airportPriceInfo!!.setPricePerDayOutbound(this)
                                }
                            }
                        }
                        returnReceivedPricesWhenFull2(true)
                    }, {
                        returnReceivedPricesWhenFull2(true)
                    })
        }

        if (mIsReturnTrip) {
            mNoOfSentInboundRequests = inboundCheapestDayByPort.size
            inboundCheapestDayByPort.forEach { entry ->
                val destination = entry.key
                val cheapestResponse = entry.value
                val strDay = formatDayMonthWithZero(cheapestResponse.id.dayInMonth)
                val strYear = formatDayMonthWithZero(cheapestResponse.id.year)
                val strMonth = formatDayMonthWithZero(cheapestResponse.id.monthInYear)
                val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)
                val observableCheapestDay: Observable<List<PricePerDayResponse>> =
                        pricesAPI.getPricePerDay(headers, postDataForDayRequest,
                                cheapestResponse.carrier,
                                destination,
                                originPort)

                val airport: Airport = getAirportById(destination)
                var countryPriceInfo = mListCountryPriceInfo.find { it.country.countryCode == airport.countryCode }
                if (countryPriceInfo == null) {
                    countryPriceInfo = CountryPriceInfo().apply {
                        country = getCountryByCode(airport.countryCode)
                        airportPriceInfos = listOf()
                    }
                    mListCountryPriceInfo.add(countryPriceInfo!!)
                }
                var airportPriceInfo = countryPriceInfo!!.airportPriceInfos.find { it.getAirportId() == airport.id }
                if (airportPriceInfo == null) {
                    airportPriceInfo = AirportPriceInfo().apply {
                        setAirport(airport)
                    }
                    countryPriceInfo!!.airportPriceInfos.add(airportPriceInfo)
                }

                observableCheapestDay
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ pricePerDayResponses ->
                            pricePerDayResponses.forEach { pricePerDayResponse ->
                                pricePerDayResponse.priceList?.firstOrNull()?.apply {
                                    carrier = pricePerDayResponse.provider
                                    setArrivalTime(pricePerDayResponse.arrivalTime)
                                    setDepartureTime(pricePerDayResponse.arrivalTime)

                                    val minPrice = airportPriceInfo!!.getBestPriceInbound()
                                    if (minPrice == 0 || priceTotal < minPrice) {
                                        this.day = cheapestResponse.id.dayInMonth
                                        airportPriceInfo!!.setPricePerDayInbound(this)
                                    }
                                }
                            }
                            returnReceivedPricesWhenFull2(false)
                        }, {
                            returnReceivedPricesWhenFull2(false)
                        })
            }
        }
    }

    private fun returnReceivedPricesWhenFull2(isOutbound: Boolean, noOfFinishedRequests: Int = 1) {
        if (isOutbound) {
            mNoOfReceivedOutboundRequests += noOfFinishedRequests
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == mNoOfSentOutboundRequests
        } else {
            mNoOfReceivedInboundRequests += noOfFinishedRequests
            isInboundLoadingDone = mNoOfReceivedInboundRequests == mNoOfSentInboundRequests
        }

        if (mIsReturnTrip) {
            if (isOutboundLoadingDone && isInboundLoadingDone) {
                // TODO sortListCountryPriceInfoAndReturnResponse()
            }
        } else {
            if (isOutboundLoadingDone) {
                // TODO sortListCountryPriceInfoAndReturnResponse()
            }
        }
    }
    /* new implementation end */

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
            val postData = MonthCheapestBody(strYear, strMonth, strDay).apply {
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
                                                        this.day = dayWithCheapestPrice.id.dayInMonth
                                                        dstAirportPriceInfo!!.setPricePerDayOutbound(this)
                                                    }
                                                } else {
                                                    val minPrice = dstAirportPriceInfo!!.getBestPriceInbound()
                                                    if (minPrice == 0 || priceTotal < minPrice) {
                                                        this.day = dayWithCheapestPrice.id.dayInMonth
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

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }
}
