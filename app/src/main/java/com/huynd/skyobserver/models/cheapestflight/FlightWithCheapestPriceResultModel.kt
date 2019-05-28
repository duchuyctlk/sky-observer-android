package com.huynd.skyobserver.models.cheapestflight

import android.annotation.SuppressLint
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.PricePerDayBody
import com.huynd.skyobserver.models.PricePerDayResponse
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.AirportPriceInfoComparator
import com.huynd.skyobserver.utils.Constants
import com.huynd.skyobserver.utils.CountryAirportUtils.*
import com.huynd.skyobserver.utils.CountryPriceInfoComparator
import com.huynd.skyobserver.utils.RequestHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by HuyND on 4/22/2018.
 */

class FlightWithCheapestPriceResultModel {
    private var mAirports: List<Airport> = getAirports()
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
        getPrices(pricesAPI, yearOutbound, monthOutbound, dayOutbound, port, true)

        // get inbound prices
        if (isReturnTrip) {
            getPrices(pricesAPI, yearInbound, monthInbound, dayInbound, port, false)
        }
    }

    @SuppressLint("CheckResult")
    private fun getPrices(pricesAPI: PricesAPI, year: Int, month: Int, day: Int,
                          originPort: String, isOutbound: Boolean) {
        val cal = Calendar.getInstance()
        val thisYear = cal.get(Calendar.YEAR)
        val thisMonth = cal.get(Calendar.MONTH) + 1
        val today = cal.get(Calendar.DAY_OF_MONTH)

        val invalidDate = (year < thisYear)
                || (year == thisYear && month < thisMonth)
                || (year == thisYear && month == thisMonth && day < today)

        if (invalidDate) {
            mListener?.notifyInvalidDate()
        } else {
            val strYear = "$year"
            val strMonth = if (month < 10) "0$month" else "$month"
            val strDay = if (day < 10) "0$day" else "$day"

            val postData = PricePerDayBody(strYear, strMonth, strDay)
            val headers = RequestHelper.getDefaultHeaders()

            for (airport in mAirports) {
                if (originPort == airport.id) {
                    continue
                }

                for (carrier in Constants.CARRIERS) {
                    val srcPort = if (isOutbound) originPort else airport.id
                    val dstPort = if (isOutbound) airport.id else originPort

//                    headers.put("Request_Hash", RequestHelper.requestHashBuilder(srcPort, dstPort,
//                            carrier, strYear, strMonth))
//                    headers.put("Request_Carrier", carrier)

                    val observableList: Observable<List<PricePerDayResponse>> = pricesAPI
                            .getPricePerDay(headers, postData, carrier, srcPort, dstPort)

                    observableList
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ pricePerDayResponses ->
                                for (pricePerDayResponse in pricePerDayResponses) {
                                    // determine destination port
                                    val destinationPort = if (isOutbound)
                                        pricePerDayResponse.destinationCode else
                                        pricePerDayResponse.originCode

                                    val country = getCountryByCode(
                                            getAirportById(destinationPort).countryCode
                                    )

                                    // get AirportPriceInfo for destination port
                                    var dstAirportPriceInfo: AirportPriceInfo? = null
                                    var airportPriceInfos: MutableList<AirportPriceInfo>? = null
                                    var dstCountryPriceInfo: CountryPriceInfo? = null

                                    for (countryPriceInfo in mCountryPriceInfos) {
                                        if (country.countryCode == countryPriceInfo.country.countryCode) {
                                            dstCountryPriceInfo = countryPriceInfo
                                            airportPriceInfos = countryPriceInfo.airportPriceInfos

                                            if (airportPriceInfos != null) {
                                                for (airportPriceInfo in airportPriceInfos) {
                                                    if (destinationPort == airportPriceInfo.airportId) {
                                                        dstAirportPriceInfo = airportPriceInfo
                                                        break
                                                    }
                                                }
                                            }

                                            if (dstAirportPriceInfo != null) {
                                                break
                                            }
                                        }
                                    }

                                    // add new entry for destination (country & airport)
                                    if (dstAirportPriceInfo == null) {
                                        val airportPriceInfo = AirportPriceInfo()
                                        airportPriceInfo.setAirport(airport)

                                        if (airportPriceInfos == null) {
                                            airportPriceInfos = mutableListOf()
                                        }
                                        airportPriceInfos.add(airportPriceInfo)

                                        if (dstCountryPriceInfo == null) {
                                            mCountryPriceInfos.add(CountryPriceInfo()
                                                    .apply {
                                                        this.airportPriceInfos = airportPriceInfos
                                                        this.country = country
                                                    })
                                        }

                                        dstAirportPriceInfo = airportPriceInfo
                                    }

                                    val priceList = pricePerDayResponse.priceList
                                    val price = if (priceList != null && priceList.size > 0)
                                        priceList[0] else null
                                    price?.apply {
                                        setCarrier(pricePerDayResponse.provider)
                                        setArrivalTime(pricePerDayResponse.arrivalTime)
                                        setDepartureTime(pricePerDayResponse.arrivalTime)

                                        val minPriceTotal = dstAirportPriceInfo.bestPriceTotal
                                        if (minPriceTotal == 0 || priceTotal < minPriceTotal) {
                                            dstAirportPriceInfo.setPricePerDay(this)
                                        }
                                    }
                                }

                                returnReceivedPricesWhenFull(isOutbound)
                            }, {
                                returnReceivedPricesWhenFull(isOutbound)
                            })
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

        if (willLoadInbound) {
            if (isOutboundLoadingDone && isInboundLoadingDone) {
                mListener?.run {
                    for (countryPriceInfo in mCountryPriceInfos) {
                        Collections.sort(countryPriceInfo.airportPriceInfos,
                                AirportPriceInfoComparator.getInstance())
                    }
                    Collections.sort(mCountryPriceInfos,
                            CountryPriceInfoComparator.getInstance())
                    onGetPricesResponse(mCountryPriceInfos)
                }
            }
        } else {
            if (isOutboundLoadingDone) {
                mListener?.run {
                    for (countryPriceInfo in mCountryPriceInfos) {
                        Collections.sort(countryPriceInfo.airportPriceInfos,
                                AirportPriceInfoComparator.getInstance())
                    }
                    Collections.sort(mCountryPriceInfos,
                            CountryPriceInfoComparator.getInstance())
                    onGetPricesResponse(mCountryPriceInfos)
                }
            }
        }
    }

    fun setEventListener(listener: EventListener) {
        mListener = listener
    }
}
