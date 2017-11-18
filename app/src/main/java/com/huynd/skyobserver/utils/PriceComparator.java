package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.PricePerDay;

import java.util.Comparator;

import static com.huynd.skyobserver.utils.PriceComparator.SortOrder.DEPART_EARLIEST;

/**
 * Created by HuyND on 10/11/2017.
 */

public class PriceComparator implements Comparator<PricePerDay> {

    private static class SingletonHolder {
        static PriceComparator sInstance = new PriceComparator();
    }

    public enum SortOrder {
        PRICE_ONLY_LOWEST,
        PRICE_ONLY_HIGHEST,
        TOTAL_PRICE_LOWEST,
        TOTAL_PRICE_HIGHEST,
        DEPART_EARLIEST,
        DEPART_LATEST,
        AIRLINES,
        UNKNOWN
    }

    private SortOrder mOrder = DEPART_EARLIEST;

    private PriceComparator() {}

    public static PriceComparator getInstance() {
        return SingletonHolder.sInstance;
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
            case PRICE_ONLY_LOWEST:
                return price1.getPrice() - price2.getPrice();
            case PRICE_ONLY_HIGHEST:
                return price2.getPrice() - price1.getPrice();
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
