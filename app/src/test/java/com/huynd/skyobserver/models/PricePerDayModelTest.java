package com.huynd.skyobserver.models;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by HuyND on 8/13/2017.
 */

public class PricePerDayModelTest {
    @Test
    public void shouldInitSpinnersValuesIfNeeded() throws Exception {
        PricePerDayModel model = new PricePerDayModel(null);
        List<Integer> availMonths = model.getAvailMonths(2018);
        assertNotNull(availMonths);
        assertEquals(12, availMonths.size());
    }
}
