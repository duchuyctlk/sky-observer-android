package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyND on 8/23/2017.
 */

public class CountryAirportUtils {
    public CountryAirportUtils() {
        throw new AssertionError();
    }

    public static List<Airport> getAirports() {
        List<Airport> airports = new ArrayList<>();

        airports.add(new Airport("SGN", "Tân Sơn Nhất", "vn"));
        airports.add(new Airport("HAN", "Nội Bài", "vn"));
        airports.add(new Airport("VCS", "Côn Đảo", "vn"));
        airports.add(new Airport("UIH", "Phù Cát", "vn"));
        airports.add(new Airport("CAH", "Cà Mau", "vn"));
        airports.add(new Airport("VCA", "Cần Thơ", "vn"));
        airports.add(new Airport("BMV", "Buôn Ma Thuột", "vn"));
        airports.add(new Airport("DAD", "Đà Nẵng", "vn"));
        airports.add(new Airport("DIN", "Điện Biên Phủ", "vn"));
        airports.add(new Airport("PXU", "Pleiku", "vn"));
        airports.add(new Airport("HPH", "Cát Bi", "vn"));
        airports.add(new Airport("CXR", "Cam Ranh", "vn"));
        airports.add(new Airport("VKG", "Rạch Giá", "vn"));
        airports.add(new Airport("PQC", "Phú Quốc", "vn"));
        airports.add(new Airport("DLI", "Liên Khương", "vn"));
        airports.add(new Airport("TBB", "Tuy Hòa", "vn"));
        airports.add(new Airport("VDH", "Đồng Hới", "vn"));
        airports.add(new Airport("VCL", "Chu Lai", "vn"));
        airports.add(new Airport("THD", "Thọ Xuân", "vn"));
        airports.add(new Airport("HUI", "Phú Bài", "vn"));
        airports.add(new Airport("VII", "Vinh", "vn"));

        return airports;
    }

    public static List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();

        countries.add(new Country("vn", "Viet Nam"));

        return countries;
    }
}
