package com.huynd.skyobserver.models.bestdates

import android.annotation.SuppressLint
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.services.PricesAPI
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
class BestDatesModel {
    interface EventListener {
        fun onGetPricesResponse(result: Any)
    }

    private var mListener: EventListener? = null
    private var mAirports: List<Airport> = CountryAirportUtils.getAirports()
    private val mListAirportPriceInfo = mutableListOf<AirportPriceInfo>()
    private val mHeaders = RequestHelper.getDefaultHeaders()

    private var mIsReturnTrip: Boolean = false
    private var mTripLength: Int = 0

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }

    fun getAirports(): List<Airport> = mAirports

    fun getPrices(pricesAPI: PricesAPI,
                  srcPort: String,
                  destPort: String,
                  isReturnTrip: Boolean,
                  tripLength: Int) {
        // reset counts
        mIsReturnTrip = isReturnTrip
        mTripLength = tripLength

        for (i in 0..9) {
            prepareData(pricesAPI, srcPort, destPort, i)
        }
    }

    private fun prepareData(pricesAPI: PricesAPI,
                            srcPort: String,
                            destPort: String,
                            monthIndex: Int) {
        Observable.fromCallable {
            val payloadDatePattern = "yyyyMMdd"
            val routes = mutableListOf("$srcPort$destPort")
            if (mIsReturnTrip) {
                routes.add("$destPort$srcPort")
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
            pricesAPI.getCheapestPricePerMonth(mHeaders, postData)
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { observableList ->
                    observableList
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ cheapestPricePerMonthResponses ->
                                if (!mIsReturnTrip) {
                                    findOneWayCheapestResponse(pricesAPI, srcPort, destPort, cheapestPricePerMonthResponses)
                                } else {
                                    findReturnCheapestResponses(pricesAPI, srcPort, destPort, cheapestPricePerMonthResponses)
                                }
                            }, {
                                // TODO
                            })
                }

    }

    private fun findOneWayCheapestResponse(pricesAPI: PricesAPI,
                                           srcPort: String,
                                           destPort: String,
                                           cheapestPricePerMonthResponses: List<CheapestPricePerMonthResponse>) {
        Observable.fromCallable {
            val responsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.origin }
            val groupedBySrcPort = responsesGroupedByPort[srcPort]
            if (groupedBySrcPort == null || groupedBySrcPort.isEmpty()) {
                listOf()
            } else {
                val cheapestPrice = groupedBySrcPort.minBy { it.cheapestPrice }!!.cheapestPrice
                groupedBySrcPort.filter { it.cheapestPrice == cheapestPrice }
            }
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cheapestResponses ->
                    if (cheapestResponses.isEmpty()) {
                        // TODO
                    } else {
                        getDetailPrices(pricesAPI, srcPort, destPort, cheapestResponses, true)
                    }
                }
    }

    private fun findReturnCheapestResponses(pricesAPI: PricesAPI,
                                            srcPort: String,
                                            destPort: String,
                                            cheapestPricePerMonthResponses: List<CheapestPricePerMonthResponse>) {
        val pairResponses = mutableListOf<Pair<CheapestPricePerMonthResponse, CheapestPricePerMonthResponse>>()
        var outboundResponses: List<CheapestPricePerMonthResponse>? = null
        var inboundResponses: List<CheapestPricePerMonthResponse>? = null
        Observable.fromCallable {
            val responsesGroupedByPort = cheapestPricePerMonthResponses.groupBy { it.id.origin }
            val groupedBySrcPort = responsesGroupedByPort[srcPort]
            if (groupedBySrcPort == null || groupedBySrcPort.isEmpty()) {
                // TODO
                return@fromCallable
            }
            val groupedByDestPort = responsesGroupedByPort[destPort]
            if (groupedByDestPort == null || groupedByDestPort.isEmpty()) {
                // TODO
                return@fromCallable
            }
            var cheapestPrice = 0
            val calendar = Calendar.getInstance()
            groupedBySrcPort.forEach { outboundResponse ->
                outboundResponse.run {
                    calendar.set(Calendar.YEAR, this.id.year)
                    calendar.set(Calendar.MONTH, this.id.monthInYear - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, this.id.dayInMonth)
                    calendar.add(Calendar.DAY_OF_MONTH, mTripLength)
                }
                val inboundResponse = groupedByDestPort.find {
                    it.id.year == calendar.get(Calendar.YEAR)
                            && it.id.monthInYear == calendar.get(Calendar.MONTH) + 1
                            && it.id.dayInMonth == calendar.get(Calendar.DAY_OF_MONTH)
                }
                inboundResponse?.run {
                    val currentBestPrice = outboundResponse.cheapestPrice + inboundResponse.cheapestPrice
                    if (cheapestPrice == 0 || cheapestPrice > currentBestPrice) {
                        pairResponses.clear()
                        pairResponses.add(Pair(outboundResponse, inboundResponse))
                        cheapestPrice = currentBestPrice
                    } else if (cheapestPrice == currentBestPrice) {
                        pairResponses.add(Pair(outboundResponse, inboundResponse))
                    }
                }
                outboundResponses = pairResponses.map { it.first }
                inboundResponses = pairResponses.map { it.second }
            }
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (outboundResponses == null || inboundResponses == null) {
                        // TODO
                    } else {
                        getDetailPrices(pricesAPI, srcPort, destPort, outboundResponses!!, true)
                        getDetailPrices(pricesAPI, srcPort, destPort, inboundResponses!!, false)
                    }
                }
    }

    private fun getDetailPrices(pricesAPI: PricesAPI,
                                srcPort: String,
                                destPort: String,
                                cheapestResponses: List<CheapestPricePerMonthResponse>,
                                isOutbound: Boolean) {
        cheapestResponses.forEach { cheapestResponse ->
            Observable.fromCallable {
                val destination = if (isOutbound) destPort else srcPort
                val origin = if (isOutbound) srcPort else destPort
                val cheapestResponseId = cheapestResponse.id
                val strDay = StringUtils.formatDayMonthWithZero(cheapestResponseId.dayInMonth)
                val strYear = StringUtils.formatDayMonthWithZero(cheapestResponseId.year)
                val strMonth = StringUtils.formatDayMonthWithZero(cheapestResponseId.monthInYear)
                val postDataForDayRequest = PricePerDayBody(strYear, strMonth, strDay)

                pricesAPI.getPricePerDay(mHeaders, postDataForDayRequest,
                        cheapestResponse.carrier,
                        origin,
                        destination)
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { observableCheapestDay ->
                        observableCheapestDay
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ pricePerDayResponses ->
                                    handleGetDetailPricesResult(cheapestResponse, pricePerDayResponses, isOutbound)
                                }, {
                                    returnReceivedPricesWhenFull(isOutbound)
                                })
                    }

        }
    }

    private fun handleGetDetailPricesResult(cheapestResponse: CheapestPricePerMonthResponse,
                                            pricePerDayResponses: List<PricePerDayResponse>,
                                            isOutbound: Boolean) {
        Observable.fromCallable {
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
                        it.priceList != null
                                && it.priceList.isNotEmpty()
                                && it.provider == cheapestResponse.carrier
                    }
                    .minBy { it.priceList.first().priceTotal }
            pricePerDayResponse?.priceList!!.first().apply {
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
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    returnReceivedPricesWhenFull(isOutbound)
                }
    }

    private fun returnReceivedPricesWhenFull(isOutbound: Boolean) {
        // TODO
        // mListener?.onGetPricesResponse(cheapestPricePerMonthResponses)
    }
}
