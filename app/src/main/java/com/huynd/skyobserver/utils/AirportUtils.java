package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.Airport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyND on 8/23/2017.
 */

public class AirportUtils {
    public AirportUtils() {
        throw new AssertionError();
    }

    public static List<Airport> getAirports() {
        List<Airport> airports = new ArrayList<>();
        airports.add(new Airport("SGN", "Tân Sơn Nhất"));
        airports.add(new Airport("HAN", "Nội Bài"));
        airports.add(new Airport("VCS", "Côn Đảo"));
        airports.add(new Airport("UIH", "Phù Cát"));
        airports.add(new Airport("CAH", "Cà Mau"));
        airports.add(new Airport("VCA", "Cần Thơ"));
        airports.add(new Airport("BMV", "Buôn Ma Thuột"));
        airports.add(new Airport("DAD", "Đà Nẵng"));
        airports.add(new Airport("DIN", "Điện Biên Phủ"));
        airports.add(new Airport("PXU", "Pleiku"));
        airports.add(new Airport("HPH", "Cát Bi"));
        airports.add(new Airport("CXR", "Cam Ranh"));
        airports.add(new Airport("VKG", "Rạch Giá"));
        airports.add(new Airport("PQC", "Phú Quốc"));
        airports.add(new Airport("DLI", "Liên Khương"));
        airports.add(new Airport("TBB", "Tuy Hòa"));
        airports.add(new Airport("VDH", "Đồng Hới"));
        airports.add(new Airport("VCL", "Chu Lai"));
        airports.add(new Airport("THD", "Thọ Xuân"));
        airports.add(new Airport("HUI", "Phú Bài"));
        airports.add(new Airport("VII", "Vinh"));
        return airports;
    }
}
