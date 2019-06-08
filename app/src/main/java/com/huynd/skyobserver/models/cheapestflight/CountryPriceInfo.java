package com.huynd.skyobserver.models.cheapestflight;

import android.os.Parcel;
import android.os.Parcelable;

import com.huynd.skyobserver.models.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyND on 9/28/2017.
 */

public class CountryPriceInfo implements Parcelable {
    private Country mCountry;
    private List<AirportPriceInfo> mAirportPriceInfos;

    public Country getCountry() {
        return mCountry;
    }

    public void setCountry(Country country) {
        mCountry = country;
    }

    public void setAirportPriceInfos(List<AirportPriceInfo> airportPriceInfos) {
        mAirportPriceInfos = airportPriceInfos;
    }

    public List<AirportPriceInfo> getAirportPriceInfos() {
        return mAirportPriceInfos;
    }

    public int getAirportPriceInfoCount() {
        return mAirportPriceInfos != null ? mAirportPriceInfos.size() : 0;
    }

    public AirportPriceInfo getAirportPriceInfo(int index) {
        return mAirportPriceInfos != null && index >= 0 && index < mAirportPriceInfos.size()
                ? mAirportPriceInfos.get(index) : null;
    }

    public int getBestPriceTotal() {
        if (mAirportPriceInfos == null || mAirportPriceInfos.size() == 0) {
            return 0;

        }
        return mAirportPriceInfos.get(0).getBestPriceTotal();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CountryPriceInfo createFromParcel(Parcel in) {
            CountryPriceInfo countryPriceInfo = new CountryPriceInfo();

            Country country = in.readParcelable(Country.class.getClassLoader());
            countryPriceInfo.setCountry(country);

            List<AirportPriceInfo> airports = new ArrayList<>();
            in.readList(airports, Country.class.getClassLoader());
            countryPriceInfo.setAirportPriceInfos(airports);
            return countryPriceInfo;
        }

        public CountryPriceInfo[] newArray(int size) {
            return new CountryPriceInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(mCountry, flags);
        parcel.writeList(mAirportPriceInfos);
    }
}
