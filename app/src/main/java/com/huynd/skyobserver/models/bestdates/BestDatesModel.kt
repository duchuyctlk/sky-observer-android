package com.huynd.skyobserver.models.bestdates

import android.annotation.SuppressLint
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDay
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.models.cheapestflight.month.ResponseId
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.Constants.Companion.BEST_PRICE_DELTA
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.utils.DateUtils
import com.huynd.skyobserver.utils.RequestHelper
import com.huynd.skyobserver.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by HuyND on 6/18/2019.
 */

@SuppressLint("CheckResult")
class BestDatesModel(private val mPricesAPI: PricesAPI) {
    interface EventListener {
        fun onGetPricesResponse(result: List<BestDatesInfo>)

        fun onGetPricesError(throwable: Throwable)
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
        Observable.fromCallable {
            prepareGetBestDayInMonth(monthIndex)
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { observableList ->
                    subscribeGetBestDayInMonth(observableList)
                }
    }

    private fun prepareGetBestDayInMonth(monthIndex: Int): Observable<List<CheapestPricePerMonthResponse>> {
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
        return mPricesAPI.getCheapestPricePerMonth(mHeaders, postData)
    }

    private fun subscribeGetBestDayInMonth(observable: Observable<List<CheapestPricePerMonthResponse>>) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cheapestPricePerMonthResponses ->
                    handleGetBestDayInMonthResult(cheapestPricePerMonthResponses)
                }, { throwable ->
                    handleGetBestDayInMonthError(throwable)
                })
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

    private fun handleGetBestDayInMonthError(throwable: Throwable) {
        mListener?.onGetPricesError(throwable)
    }

    private fun findOneWayCheapestResponse() {
        Observable.fromCallable {
            prepareFindOneWayCheapestResponse()
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cheapestResponses ->
                    handleFindOneWayCheapestResponseResult(cheapestResponses)
                }
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
        Observable.fromCallable {
            prepareFindReturnCheapestResponses()
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pairResponses ->
                    handleFindReturnCheapestResponsesResult(pairResponses)
                }
    }

    private fun prepareFindReturnCheapestResponses()
            : Pair<List<CheapestPricePerMonthResponse>, List<CheapestPricePerMonthResponse>> {
        val pairResponses = Pair(mutableListOf<CheapestPricePerMonthResponse>(), mutableListOf<CheapestPricePerMonthResponse>())

        val responsesGroupedByPort = mAllMonthResponses.groupBy { it.id.origin }
        val groupedBySrcPort = responsesGroupedByPort[mSrcPort]
        if (groupedBySrcPort == null || groupedBySrcPort.isEmpty()) {
            return Pair(listOf(), listOf())
        }
        val groupedByDestPort = responsesGroupedByPort[mDestPort]
        if (groupedByDestPort == null || groupedByDestPort.isEmpty()) {
            return Pair(listOf(), listOf())
        }

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
            Observable.fromCallable {
                prepareGetDetailPrices(cheapestResponse, isOutbound)
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { observableCheapestDay ->
                        subscribeGetDetailPrices(observableCheapestDay, cheapestResponse, isOutbound)
                    }
        }
    }

    private fun prepareGetDetailPrices(cheapestResponse: CheapestPricePerMonthResponse,
                                       isOutbound: Boolean): Observable<List<PricePerDayResponse>> {
        val destination = if (isOutbound) mDestPort else mSrcPort
        val origin = if (isOutbound) mSrcPort else mDestPort

        val cheapestResponseId = cheapestResponse.id
        val strDay = StringUtils.formatDayMonthWithZero(cheapestResponseId.dayInMonth)
        val strYear = StringUtils.formatDayMonthWithZero(cheapestResponseId.year)
        val strMonth = StringUtils.formatDayMonthWithZero(cheapestResponseId.monthInYear)
        val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)

        return mPricesAPI.getPricePerDay(mHeaders, postDataForDayRequest, cheapestResponse.carrier, origin, destination)
    }

    private fun subscribeGetDetailPrices(observable: Observable<List<PricePerDayResponse>>,
                                         cheapestResponse: CheapestPricePerMonthResponse,
                                         isOutbound: Boolean) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pricePerDayResponses ->
                    handleGetDetailPricesResult(cheapestResponse, pricePerDayResponses, isOutbound)
                }, { throwable ->
                    handleGetDetailPricesError(throwable)
                })
    }

    private fun handleGetDetailPricesResult(cheapestResponse: CheapestPricePerMonthResponse,
                                            pricePerDayResponses: List<PricePerDayResponse>,
                                            isOutbound: Boolean) {
        Observable.fromCallable {
            prepareGetPricePerDay(cheapestResponse, pricePerDayResponses, isOutbound)
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pricePerDay ->
                    returnReceivedPricesWhenFull(pricePerDay, isOutbound)
                }
    }

    private fun handleGetDetailPricesError(throwable: Throwable) {
        mListener?.onGetPricesError(throwable)
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

    private fun returnReceivedPricesWhenFull(pricePerDay: PricePerDay, isOutbound: Boolean) {
        if (isOutbound) {
            mPriceCache[mSrcPort]!![pricePerDay.price] = pricePerDay.priceTotal
            mNoOfReceivedOutboundRequests++
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == mNoOfSentOutboundRequests
        } else {
            mPriceCache[mDestPort]!![pricePerDay.price] = pricePerDay.priceTotal
            mNoOfReceivedInboundRequests++
            isInboundLoadingDone = mNoOfReceivedInboundRequests == mNoOfSentInboundRequests
        }

        val isLoadingDone = if (mIsReturnTrip) isOutboundLoadingDone && isInboundLoadingDone else isOutboundLoadingDone

        if (isLoadingDone) {
            Observable.fromCallable {
                prepareBestDatesInfo()
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { data ->
                        mListener?.onGetPricesResponse(data)
                    }
        }
    }

    private fun prepareBestDatesInfo(): List<BestDatesInfo> {
        val data = mutableListOf<BestDatesInfo>()
        for (i in 0 until mOutResponses.size) {
            val outResponse = mOutResponses[i]
            val inResponse = mInResponses[i]

            val srcPostPriceCache = mPriceCache[mSrcPort]!!
            val destPostPriceCache = mPriceCache[mDestPort]!!

            if (!srcPostPriceCache.containsKey(outResponse.cheapestPrice)
                    || !destPostPriceCache.containsKey(inResponse.cheapestPrice)) {
                continue
            }

            val info = BestDatesInfo().apply {
                outboundId = outResponse.id
                outboundCarrier = outResponse.carrier
                outboundTotalPrice = srcPostPriceCache[outResponse.cheapestPrice]!!

                inboundId = inResponse.id
                inboundCarrier = inResponse.carrier
                inboundTotalPrice = destPostPriceCache[inResponse.cheapestPrice]!!

            }
            data.add(info)
        }

        return data
    }
}
