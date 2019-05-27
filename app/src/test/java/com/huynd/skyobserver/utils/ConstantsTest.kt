package com.huynd.skyobserver.utils

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Created by HuyND on 5/25/2019.
 */

class ConstantsTest {
    @Test
    fun testPrivateConstructor() {
        try {
            val constructor = Constants::class.java.getDeclaredConstructor()
            constructor.isAccessible = true

            // should throws AssertionError here
            constructor.newInstance()
            fail("This line is never reached!")
        } catch (e: Throwable) {
            assertTrue("This line is reached!", true)
        }
    }
}
