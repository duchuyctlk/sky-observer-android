package com.huynd.skyobserver.models.cheapestflight;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.Country;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.models.PricePerDayBody;
import com.huynd.skyobserver.models.PricePerDayResponse;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.AirportPriceInfoComparator;
import com.huynd.skyobserver.utils.Constants;
import com.huynd.skyobserver.utils.CountryPriceInfoComparator;
import com.huynd.skyobserver.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById;
import static com.huynd.skyobserver.utils.CountryAirportUtils.getAirports;
import static com.huynd.skyobserver.utils.CountryAirportUtils.getCountryByCode;

/**
 * Created by HuyND on 4/22/2018.
 */

public class FlightWithCheapestPriceResultModel {

    private List<Airport> mAirports;

    private List<CountryPriceInfo> mCountryPriceInfos;

    private int mNoOfReceivedOutboundRequests;
    private int mNoOfReceivedInboundRequests;

    private boolean isOutboundLoadingDone, isInboundLoadingDone, willLoadInbound;

    public interface EventListener {
        void notifyInvalidDate();

        void onGetPricesResponse(List<CountryPriceInfo> countryPriceInfos);
    }

    public void getPrices(PricesAPI pricesAPI,
                          int yearOutbound, int monthOutbound, int dayOutbound,
                          int yearInbound, int monthInbound, int dayInbound,
                          String port, boolean isReturnTrip) {
        // reset counts
        if (mCountryPriceInfos == null) {
            mCountryPriceInfos = new ArrayList<>();
        } else {
            mCountryPriceInfos.clear();
        }

        mNoOfReceivedOutboundRequests = 0;
        mNoOfReceivedInboundRequests = 0;

        isOutboundLoadingDone = false;
        isInboundLoadingDone = false;
        willLoadInbound = isReturnTrip;

        // get outbound prices
        getPrices(pricesAPI, yearOutbound, monthOutbound, dayOutbound, port, true);

        // get inbound prices
        if (isReturnTrip) {
            getPrices(pricesAPI, yearInbound, monthInbound, dayInbound, port, false);
        }
    }

    private void getPrices(PricesAPI pricesAPI, int year, int month, int day,
                           final String originPort,
                           final boolean isOutbound) {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH) + 1;
        int today = cal.get(Calendar.DAY_OF_MONTH);

        boolean invalidDate = (year < thisYear)
                || (year == thisYear && month < thisMonth)
                || (year == thisYear && month == thisMonth && day < today);

        if (invalidDate) {
            if (mListener != null) {
                mListener.notifyInvalidDate();
            }
        } else {
            if (mAirports == null) {
                mAirports = getAirports();
            }

            String strYear = String.valueOf(year);
            String strMonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
            String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);

            PricePerDayBody postData = new PricePerDayBody(strYear, strMonth, strDay);
            Map<String, String> headers = RequestHelper.getDefaultHeaders();

            for (final Airport airport : mAirports) {
                if (originPort.equals(airport.getId())) {
                    continue;
                }

                for (String carrier : Constants.CARRIERS) {
                    String srcPort = isOutbound ? originPort : airport.getId();
                    String dstPort = isOutbound ? airport.getId() : originPort;

                    headers.put("Request_Hash", RequestHelper.requestHashBuilder(srcPort, dstPort,
                            carrier, strYear, strMonth));

                    headers.put("Request_Carrier", carrier);

                    Observable<List<PricePerDayResponse>> observableList = pricesAPI
                            .getPricePerDay(headers, postData, carrier, srcPort, dstPort);

                    observableList
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<PricePerDayResponse>>() {

                                @Override
                                public void accept(List<PricePerDayResponse> pricePerDayResponses)
                                        throws Exception {
                                    for (PricePerDayResponse pricePerDayResponse : pricePerDayResponses) {
                                        // determine destination port
                                        String destinationPort = isOutbound ?
                                                pricePerDayResponse.getDestinationCode() :
                                                pricePerDayResponse.getOriginCode();

                                        Airport airport = getAirportById(destinationPort);
                                        Country country = getCountryByCode(airport.getCountryCode());

                                        // get AirportPriceInfo for destination port
                                        AirportPriceInfo dstAirportPriceInfo = null;
                                        List<AirportPriceInfo> airportPriceInfos = null;
                                        CountryPriceInfo dstCountryPriceInfo = null;

                                        for (CountryPriceInfo countryPriceInfo : mCountryPriceInfos) {
                                            if (country.getCountryCode()
                                                    .equals(countryPriceInfo.getCountry().getCountryCode())) {
                                                dstCountryPriceInfo = countryPriceInfo;
                                                airportPriceInfos = countryPriceInfo.getAirportPriceInfos();

                                                if (airportPriceInfos != null) {
                                                    for (AirportPriceInfo airportPriceInfo : airportPriceInfos) {
                                                        if (destinationPort.equals(airportPriceInfo.getAirportId())) {
                                                            dstAirportPriceInfo = airportPriceInfo;
                                                            break;
                                                        }
                                                    }
                                                }

                                                if (dstAirportPriceInfo != null) {
                                                    break;
                                                }
                                            }
                                        }

                                        // add new entry for destination (country & airport)
                                        if (dstAirportPriceInfo == null) {
                                            AirportPriceInfo airportPriceInfo = new AirportPriceInfo();
                                            airportPriceInfo.setAirport(airport);

                                            if (airportPriceInfos == null) {
                                                airportPriceInfos = new ArrayList<>();
                                            }
                                            airportPriceInfos.add(airportPriceInfo);

                                            if (dstCountryPriceInfo == null) {
                                                CountryPriceInfo countryPriceInfo = new CountryPriceInfo();
                                                countryPriceInfo.setAirportPriceInfos(airportPriceInfos);
                                                countryPriceInfo.setCountry(country);
                                                mCountryPriceInfos.add(countryPriceInfo);
                                            }

                                            dstAirportPriceInfo = airportPriceInfo;
                                        }

                                        List<PricePerDay> priceList = pricePerDayResponse.getPriceList();
                                        PricePerDay price = priceList != null && priceList.size() > 0 ?
                                                priceList.get(0) : null;
                                        if (price != null) {
                                            int minPriceTotal = dstAirportPriceInfo.getBestPriceTotal();
                                            if (minPriceTotal == 0 || price.getPriceTotal() < minPriceTotal) {
                                                dstAirportPriceInfo.setPricePerDay(price);
                                            }
                                        }
                                    }

                                    returnReceivedPricesWhenFull(isOutbound);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    returnReceivedPricesWhenFull(isOutbound);
                                }
                            });
                }
            }
        }
    }

    private void returnReceivedPricesWhenFull(boolean isOutbound) {
        int numOfOneWayRequests = Constants.CARRIERS.length * (mAirports.size() - 1);
        if (isOutbound) {
            mNoOfReceivedOutboundRequests++;
            isOutboundLoadingDone = mNoOfReceivedOutboundRequests == numOfOneWayRequests;
        } else {
            mNoOfReceivedInboundRequests++;
            isInboundLoadingDone = mNoOfReceivedInboundRequests == numOfOneWayRequests;
        }

        if (willLoadInbound) {
            if (isOutboundLoadingDone && isInboundLoadingDone) {
                if (mListener != null) {
                    for (CountryPriceInfo countryPriceInfo : mCountryPriceInfos) {
                        Collections.sort(countryPriceInfo.getAirportPriceInfos(),
                                AirportPriceInfoComparator.getInstance());
                    }
                    Collections.sort(mCountryPriceInfos,
                            CountryPriceInfoComparator.getInstance());
                    mListener.onGetPricesResponse(mCountryPriceInfos);
                }
            }
        } else {
            if (isOutboundLoadingDone) {
                if (mListener != null) {
                    for (CountryPriceInfo countryPriceInfo : mCountryPriceInfos) {
                        Collections.sort(countryPriceInfo.getAirportPriceInfos(),
                                AirportPriceInfoComparator.getInstance());
                    }
                    Collections.sort(mCountryPriceInfos,
                            CountryPriceInfoComparator.getInstance());
                    mListener.onGetPricesResponse(mCountryPriceInfos);
                }
            }
        }
    }

    private EventListener mListener;

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }
}
