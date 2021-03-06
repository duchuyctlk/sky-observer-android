package com.huynd.skyobserver.models.bestdates

import android.annotation.SuppressLint
import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.PricePerDay
import com.huynd.skyobserver.entities.PricePerDayBody
import com.huynd.skyobserver.entities.PricePerDayResponse
import com.huynd.skyobserver.entities.bestdates.BestDatesBody
import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.entities.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.entities.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.entities.cheapestflight.month.ResponseId
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.Constants.Companion.BEST_PRICE_DELTA
import com.huynd.skyobserver.utils.CoroutineUtils.Companion.startComputingThread
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.utils.DateUtils
import com.huynd.skyobserver.utils.RequestHelper
import com.huynd.skyobserver.utils.StringUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by HuyND on 6/18/2019.
 */

@SuppressLint("CheckResult")
class BestDatesModel(private val mPricesAPI: PricesAPI) {
    interface EventListener {
        fun onGetPricesResponse(result: List<BestDatesInfo>)
    }

    private var mListener: EventListener? = null
    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()
    private val mListAirportPriceInfo = mutableListOf<AirportPriceInfo>()
    private val mHeaders = RequestHelper.getDefaultHeaders()

    private var mIsReturnTrip: Boolean = false
    private var mTripLength: Int = 0

    private val mAllMonthResponses: MutableList<CheapestPricePerMonthResponse> = mutableListOf()
    private val mOutResponses: MutableList<CheapestPricePerMonthResponse> = mutableListOf()
    private val mInResponses: MutableList<CheapestPricePerMonthResponse> = mutableListOf()
    private var mNoOfMonths: Int = 10
    private var mNoOfReceivedMonthRequests: Int = 0

    private var isOutboundLoadingDone = false
    private var isInboundLoadingDone = false

    private var mNoOfSentOutboundRequests: Int = 0
    private var mNoOfSentInboundRequests: Int = 0
    private var mNoOfReceivedOutboundRequests: Int = 0
    private var mNoOfReceivedInboundRequests: Int = 0

    private lateinit var mSrcPort: String
    private lateinit var mDestPort: String

    private val mPriceCache: HashMap<String, HashMap<Double, Double>> = hashMapOf()

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }

    fun getAirports(): List<Airport> = mAirports

    fun getPrices(srcPort: String, destPort: String, isReturnTrip: Boolean, tripLength: Int) {
        // reset counts
        mIsReturnTrip = isReturnTrip
        mTripLength = tripLength

        mAllMonthResponses.clear()

        mNoOfReceivedMonthRequests = 0

        isOutboundLoadingDone = false
        isInboundLoadingDone = false
        mNoOfSentOutboundRequests = 0
        mNoOfSentInboundRequests = 0
        mNoOfReceivedOutboundRequests = 0
        mNoOfReceivedInboundRequests = 0

        mSrcPort = srcPort
        mDestPort = destPort

        mPriceCache.clear()
        mPriceCache[mSrcPort] = hashMapOf()
        mPriceCache[mDestPort] = hashMapOf()

        for (monthIndex in 0 until mNoOfMonths) {
            getBestDayInMonth(monthIndex)
        }
    }

    private fun getBestDayInMonth(monthIndex: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val payloadDatePattern = "yyyyMMdd"
            val routes = mutableListOf("$mSrcPort$mDestPort")
            if (mIsReturnTrip) {
                routes.add("$mDestPort$mSrcPort")
            }
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, monthIndex)
            val startDate = cal.let {
                it.set(Calendar.DAY_OF_MONTH, 1)
                DateUtils.dateToString(it.time, payloadDatePattern)
            }
            val endDate = cal.let {
                it.set(Calendar.DAY_OF_MONTH, it.getActualMaximum(Calendar.DAY_OF_MONTH))
                DateUtils.dateToString(it.time, payloadDatePattern)
            }
            val postData = BestDatesBody(startDate, endDate)
            postData.setRoutes(routes)

            withContext(Dispatchers.IO) {
                var cheapestPricePerMonthResponses: List<CheapestPricePerMonthResponse>? = null
                var throwable: Throwable? = null
                try {
                    cheapestPricePerMonthResponses = mPricesAPI.getListCheapestPricePerMonth(mHeaders, postData)
                } catch (ex: Exception) {
                    throwable = ex
                    ex.printStackTrace()
                }

                withContext(Dispatchers.Main) {
                    cheapestPricePerMonthResponses?.run {
                        handleGetBestDayInMonthResult(this)
                    }
                    throwable?.run {
                        handleGetBestDayInMonthResult(listOf())
                    }
                }
            }
        }
    }

    private fun handleGetBestDayInMonthResult(cheapestPricePerMonthResponses: List<CheapestPricePerMonthResponse>) {
        mAllMonthResponses.addAll(cheapestPricePerMonthResponses)
        mNoOfReceivedMonthRequests++
        if (mNoOfReceivedMonthRequests == mNoOfMonths) {
            if (!mIsReturnTrip) {
                findOneWayCheapestResponse()
            } else {
                findReturnCheapestResponses()
            }
        }
    }

    private fun findOneWayCheapestResponse() {
        startComputingThread({ prepareFindOneWayCheapestResponse() }, { cheapestResponses ->
            handleFindOneWayCheapestResponseResult(cheapestResponses)
        })
    }

    private fun prepareFindOneWayCheapestResponse(): List<CheapestPricePerMonthResponse> {
        val responsesGroupedByPort = mAllMonthResponses.groupBy { it.id.origin }
        val groupedBySrcPort = responsesGroupedByPort[mSrcPort]
        return if (groupedBySrcPort == null || groupedBySrcPort.isEmpty()) {
            listOf()
        } else {
            val cheapestPrice = groupedBySrcPort.minBy { it.cheapestPrice }!!.cheapestPrice
            groupedBySrcPort.filter { it.cheapestPrice - cheapestPrice <= BEST_PRICE_DELTA }
        }
    }

    private fun handleFindOneWayCheapestResponseResult(cheapestResponses: List<CheapestPricePerMonthResponse>) {
        if (cheapestResponses.isEmpty()) {
            mListener?.onGetPricesResponse(listOf())
        } else {
            mOutResponses.clear()
            mOutResponses.addAll(cheapestResponses)

            val distinctRes = cheapestResponses.distinctBy { it.cheapestPrice }
            mNoOfSentOutboundRequests += distinctRes.size
            getDetailPrices(distinctRes, true)
        }
    }

    private fun findReturnCheapestResponses() {
        startComputingThread({ prepareFindReturnCheapestResponses() }, { pairResponses ->
            handleFindReturnCheapestResponsesResult(pairResponses)
        })
    }

    private fun prepareFindReturnCheapestResponses()
            : Pair<List<CheapestPricePerMonthResponse>, List<CheapestPricePerMonthResponse>> {
        val pairResponses = Pair(mutableListOf<CheapestPricePerMonthResponse>(), mutableListOf<CheapestPricePerMonthResponse>())

        val responsesGroupedByPort = mAllMonthResponses.groupBy { it.id.origin }
        val groupedBySrcPort = responsesGroupedByPort[mSrcPort]

        if (groupedBySrcPort == null || groupedBySrcPort.isEmpty()) return Pair(listOf(), listOf())
        val groupedByDestPort = responsesGroupedByPort[mDestPort]
        if (groupedByDestPort == null || groupedByDestPort.isEmpty()) return Pair(listOf(), listOf())

        // find cheapestPrice
        var cheapestPrice = 0.0
        groupedBySrcPort.forEach { outboundResponse ->
            val inboundResponse = findMatchingInbound(outboundResponse.id, groupedByDestPort)
            inboundResponse?.run {
                val currentBestPrice = outboundResponse.cheapestPrice + inboundResponse.cheapestPrice
                if (cheapestPrice == 0.0 || cheapestPrice > currentBestPrice) {
                    cheapestPrice = currentBestPrice
                }
            }
        }

        pairResponses.first.clear()
        pairResponses.second.clear()
        groupedBySrcPort.forEach { outboundResponse ->
            val inboundResponse = findMatchingInbound(outboundResponse.id, groupedByDestPort)
            inboundResponse?.run {
                val currentBestPrice = outboundResponse.cheapestPrice + inboundResponse.cheapestPrice
                if (currentBestPrice - cheapestPrice <= BEST_PRICE_DELTA) {
                    pairResponses.first.add(outboundResponse)
                    pairResponses.second.add(inboundResponse)
                }
            }
        }
        return pairResponses
    }

    private fun findMatchingInbound(outboundId: ResponseId,
                                    groupedByDestPort: List<CheapestPricePerMonthResponse>)
            : CheapestPricePerMonthResponse? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, outboundId.year)
        calendar.set(Calendar.MONTH, outboundId.monthInYear - 1)
        calendar.set(Calendar.DAY_OF_MONTH, outboundId.dayInMonth)
        calendar.add(Calendar.DAY_OF_MONTH, mTripLength)

        return groupedByDestPort.find {
            it.id.year == calendar.get(Calendar.YEAR)
                    && it.id.monthInYear == calendar.get(Calendar.MONTH) + 1
                    && it.id.dayInMonth == calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    private fun handleFindReturnCheapestResponsesResult(
            pairResponses: Pair<List<CheapestPricePerMonthResponse>, List<CheapestPricePerMonthResponse>>) {
        val outboundResponses = pairResponses.first
        val inboundResponses = pairResponses.second

        if (outboundResponses.isEmpty() || inboundResponses.isEmpty()) {
            mListener?.onGetPricesResponse(listOf())
        } else {
            mOutResponses.clear()
            mOutResponses.addAll(outboundResponses)

            mInResponses.clear()
            mInResponses.addAll(inboundResponses)

            val distinctOutRes = outboundResponses.distinctBy { it.cheapestPrice }
            val distinctInRes = inboundResponses.distinctBy { it.cheapestPrice }

            mNoOfSentOutboundRequests += distinctOutRes.size
            mNoOfSentInboundRequests += distinctInRes.size
            getDetailPrices(distinctOutRes, true)
            getDetailPrices(distinctInRes, false)
        }
    }

    private fun getDetailPrices(cheapestResponses: List<CheapestPricePerMonthResponse>,
                                isOutbound: Boolean) {
        cheapestResponses.forEach { cheapestResponse ->
            CoroutineScope(Dispatchers.Default).launch {
                val destination = if (isOutbound) mDestPort else mSrcPort
                val origin = if (isOutbound) mSrcPort else mDestPort

                val cheapestResponseId = cheapestResponse.id
                val strDay = StringUtils.formatDayMonthWithZero(cheapestResponseId.dayInMonth)
                val strYear = StringUtils.formatDayMonthWithZero(cheapestResponseId.year)
                val strMonth = StringUtils.formatDayMonthWithZero(cheapestResponseId.monthInYear)
                val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)

                withContext(Dispatchers.IO) {
                    var pricePerDayResponses: List<PricePerDayResponse>? = null
                    var throwable: Throwable? = null
                    try {
                        pricePerDayResponses = mPricesAPI.getListPricePerDay(mHeaders, postDataForDayRequest, cheapestResponse.carrier, origin, destination)
                    } catch (ex: Exception) {
                        throwable = ex
                        ex.printStackTrace()
                    }
                    withContext(Dispatchers.Main) {
                        pricePerDayResponses?.run {
                            handleGetDetailPricesResult(cheapestResponse, this, isOutbound)
                        }
                        throwable?.run {
                            returnReceivedPricesWhenFull(null, isOutbound)
                        }
                    }
                }
            }
        }
    }

    private fun handleGetDetailPricesResult(cheapestResponse: CheapestPricePerMonthResponse,
                                            pricePerDayResponses: List<PricePerDayResponse>,
                                            isOutbound: Boolean) {
        startComputingThread({ prepareGetPricePerDay(cheapestResponse, pricePerDayResponses, isOutbound) }, { pricePerDay ->
            returnReceivedPricesWhenFull(pricePerDay, isOutbound)
        })
    }

    private fun prepareGetPricePerDay(cheapestResponse: CheapestPricePerMonthResponse,
                                      pricePerDayResponses: List<PricePerDayResponse>,
                                      isOutbound: Boolean): PricePerDay {
        val cheapestResponseId = cheapestResponse.id
        val destination = if (isOutbound) cheapestResponseId.destination else cheapestResponseId.origin
        var airportPriceInfo = mListAirportPriceInfo.find { it.getAirportId() == destination }
        if (airportPriceInfo == null) {
            airportPriceInfo = AirportPriceInfo().apply {
                setAirport(CountryAirportUtils.getAirportById(destination))
            }
            mListAirportPriceInfo.add(airportPriceInfo)
        }
        val pricePerDayResponse = pricePerDayResponses
                .filter {
                    it.priceList != null && it.priceList.isNotEmpty() && it.provider == cheapestResponse.carrier
                }
                .minBy { it.priceList.first().priceTotal }

        return pricePerDayResponse?.priceList!!.first().apply {
            carrier = pricePerDayResponse.provider
            setArrivalTime(pricePerDayResponse.arrivalTime)
            setDepartureTime(pricePerDayResponse.arrivalTime)
            this.day = cheapestResponseId.dayInMonth

            if (isOutbound) {
                airportPriceInfo.setPricePerDayOutbound(this)
            } else {
                airportPriceInfo.setPricePerDayInbound(this)
            }
        }
    }

    private fun returnReceivedPricesWhenFull(pricePerDay: PricePerDay?, isOutbound: Boolean) {
        if (isOutbound) {
            pricePerDay?.run {
                mPriceCache[mSrcPort]!![pricePerDay.price] = pricePerDay.priceTotal
            }
            mNoOfReceivedOutboundRequests++
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == mNoOfSentOutboundRequests
        } else {
            pricePerDay?.run {
                mPriceCache[mDestPort]!![pricePerDay.price] = pricePerDay.priceTotal
            }
            mNoOfReceivedInboundRequests++
            isInboundLoadingDone = mNoOfReceivedInboundRequests == mNoOfSentInboundRequests
        }

        val isLoadingDone = if (mIsReturnTrip) isOutboundLoadingDone && isInboundLoadingDone else isOutboundLoadingDone

        if (isLoadingDone) {
            startComputingThread({ prepareBestDatesInfo() }, { data ->
                mListener?.onGetPricesResponse(data)
            })
        }
    }

    private fun prepareBestDatesInfo(): List<BestDatesInfo> {
        val data = mutableListOf<BestDatesInfo>()
        for (i in 0 until mOutResponses.size) {
            val outResponse = mOutResponses[i]
            val srcPostPriceCache = mPriceCache[mSrcPort]!!

            if (srcPostPriceCache.containsKey(outResponse.cheapestPrice)) {
                val info = BestDatesInfo().apply {
                    outboundId = outResponse.id
                    outboundCarrier = outResponse.carrier
                    outboundTotalPrice = srcPostPriceCache[outResponse.cheapestPrice]!!
                }

                if (mIsReturnTrip) {
                    val inResponse = mInResponses[i]
                    val destPostPriceCache = mPriceCache[mDestPort]!!

                    if (destPostPriceCache.containsKey(inResponse.cheapestPrice)) {
                        info.apply {
                            inboundId = inResponse.id
                            inboundCarrier = inResponse.carrier
                            inboundTotalPrice = destPostPriceCache[inResponse.cheapestPrice]!!
                        }
                    }
                }
                data.add(info)
            }
        }
        return data
    }
}
