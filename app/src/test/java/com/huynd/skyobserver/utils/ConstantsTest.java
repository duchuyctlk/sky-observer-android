package com.huynd.skyobserver.utils;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/13/2017.
 */

public class ConstantsTest {
    @Test
    public void testPrivateConstructor() {
        try {
            Constructor<Constants> constructor =
                    Constants.class.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);

            // should throws AssertionError here
            constructor.newInstance(new Object[0]);
            fail("This line is never reached!");
        } catch (Throwable e) {
            assertTrue("This line is reached!", true);
        }
    }
}
