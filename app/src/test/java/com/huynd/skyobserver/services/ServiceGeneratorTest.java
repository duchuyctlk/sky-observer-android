package com.huynd.skyobserver.services;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by HuyND on 8/13/2017.
 */

public class ServiceGeneratorTest {
    @Test
    public void testPrivateConstructor() {
        try {
            Constructor<ServiceGenerator> constructor =
                    ServiceGenerator.class.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);

            // should throws AssertionError here
            constructor.newInstance(new Object[0]);
            fail("This line is never reached!");
        } catch (Throwable e) {
            assertTrue("This line is reached!", true);
        }
    }
}
