package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.PricePerDay;

import java.util.Comparator;

import static com.huynd.skyobserver.utils.PriceComparator.SortOrder.DEPART_EARLIEST;

/**
 * Created by HuyND on 10/11/2017.
 */

public class PriceComparator implements Comparator<PricePerDay> {

    public enum SortOrder {
        TOTAL_PRICE_LOWEST,
        TOTAL_PRICE_HIGHEST,
        DEPART_EARLIEST,
        DEPART_LATEST,
        AIRLINES,
        UNKNOWN
    }

    private static PriceComparator sInstance;

    private SortOrder mOrder = DEPART_EARLIEST;

    public static PriceComparator getInstance() {
        if (sInstance == null) {
            sInstance = new PriceComparator();
        }
        return sInstance;
    }

    @Override
    public int compare(PricePerDay price1, PricePerDay price2) {
        if (price1 == null && price2 == null) {
            return 0;
        }

        if (price1 == null) {
            return -1;
        }

        if (price2 == null) {
            return 1;
        }

        switch (mOrder) {
            case TOTAL_PRICE_LOWEST:
                return price1.getPriceTotal() - price2.getPriceTotal();
            case TOTAL_PRICE_HIGHEST:
                return price2.getPriceTotal() - price1.getPriceTotal();
            case DEPART_EARLIEST:
                return price1.getDepartureTime().compareTo(price2.getDepartureTime());
            case DEPART_LATEST:
                return price2.getDepartureTime().compareTo(price1.getDepartureTime());
            case AIRLINES:
                return price1.getCarrier().compareTo(price2.getCarrier());
        }
        return 0;
    }

    public void setSortOrder(SortOrder order) {
        mOrder = order;
    }
}
