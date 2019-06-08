package com.huynd.skyobserver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HuyND on 12/30/2017.
 */

public class Country implements Parcelable {
    private String mCode;
    private String mName;

    public Country(String code, String name) {
        mCode = code;
        mName = name;
    }

    public String getCountryCode() {
        return mCode;
    }

    public String getCountryName() {
        return mName;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Country createFromParcel(Parcel in) {
            String code = in.readString();
            String name = in.readString();
            return new Country(code, name);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCode);
        parcel.writeString(mName);
    }
}
