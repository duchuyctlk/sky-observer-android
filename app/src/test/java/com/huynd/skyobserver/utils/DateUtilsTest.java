package com.huynd.skyobserver.utils;

import org.junit.Test;

import java.lang.reflect.Constructor;

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
}
