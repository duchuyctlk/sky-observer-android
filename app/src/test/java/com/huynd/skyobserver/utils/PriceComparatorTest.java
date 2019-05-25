package com.huynd.skyobserver.utils;

import com.huynd.skyobserver.models.PricePerDay;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by HuyND on 10/12/2017.
 */

public class PriceComparatorTest {

    private PriceComparator mPriceComparator;
    private PricePerDay mPricePerDay;

    @Before
    public void setUp() throws Exception {
        mPriceComparator = PriceComparator.getInstance();
        mPricePerDay = new PricePerDay();
    }

    @Test
    public void testComparing2NullValues() {
        assertEquals(0, mPriceComparator.compare(null, null));
    }

    @Test
    public void testComparingNullValueAgainstObject() {
        assertEquals(-1, mPriceComparator.compare(null, mPricePerDay));
        assertEquals(1, mPriceComparator.compare(mPricePerDay, null));
    }

    @Test
    public void testDefaultFlow() {
        mPriceComparator.setSortOrder(PriceComparator.SortOrder.UNKNOWN);
        assertEquals(0, mPriceComparator.compare(new PricePerDay(), mPricePerDay));
    }
}
