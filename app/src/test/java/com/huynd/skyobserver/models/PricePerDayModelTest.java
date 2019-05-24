package com.huynd.skyobserver.models;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by HuyND on 8/13/2017.
 */

public class PricePerDayModelTest {
    @Test
    public void shouldInitSpinnersValuesIfNeeded() throws Exception {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);

        PricePerDayModel model = new PricePerDayModel();
        List<Integer> availMonths = model.getAvailMonths(thisYear + 1);
        assertNotNull(availMonths);
        assertEquals(12, availMonths.size());
    }
}