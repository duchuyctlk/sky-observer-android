package com.huynd.skyobserver.utils;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/16/2017.
 */

public class DateUtilsTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        try {
            Constructor<DateUtils> constructor =
                    DateUtils.class.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);

            // should throws AssertionError here
            constructor.newInstance(new Object[0]);
            fail("This line is never reached!");
        } catch (Throwable e) {
            assertTrue("This line is reached!", true);
        }
    }

    @Test
    public void testConvertInvalidStringToNull() throws Exception {
        assertNull(DateUtils.convertStringToDate("2017/10/01"));
    }

    @Test
    public void testGetNumberOfDaysInMonth() throws Exception {
        // normal February
        assertEquals(28, DateUtils.getNumberOfDaysInMonth(2017, 2));
        assertEquals(28, DateUtils.getNumberOfDaysInMonth(1900, 2));

        // leap year's February
        assertEquals(29, DateUtils.getNumberOfDaysInMonth(2016, 2));

        // April/June/Sep/Nov
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 4));
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 6));
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 9));
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 11));

        // else
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 1));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 3));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 5));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 7));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 8));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 10));
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 12));
    }
}
