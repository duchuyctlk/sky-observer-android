package com.huynd.skyobserver.utils;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/26/2017.
 */

public class CountryAirportUtilsTest {
    @Test
    public void testPrivateConstructor() {
        try {
            Constructor<CountryAirportUtils> constructor =
                    CountryAirportUtils.class.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);

            // should throws AssertionError here
            constructor.newInstance(new Object[0]);
            fail("This line is never reached!");
        } catch (Throwable e) {
            assertTrue("This line is reached!", true);
        }
    }
}
