package com.huynd.skyobserver.services

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Created by HuyND on 5/25/2019.
 */

class ServiceGeneratorTest {
    @Test
    fun testPrivateConstructor() {
        try {
            val constructor = ServiceGenerator::class.java.getDeclaredConstructor()
            constructor.isAccessible = true

            // should throws AssertionError here
            constructor.newInstance()
            fail("This line is never reached!")
        } catch (e: Throwable) {
            assertTrue("This line is reached!", true)
        }
    }
}
