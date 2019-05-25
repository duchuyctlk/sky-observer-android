package com.huynd.skyobserver.utils

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Created by HuyND on 8/13/2017.
 */

class RequestHelperTest {
    @Test
    fun testPrivateConstructor() {
        try {
            val constructor = RequestHelper::class.java.getDeclaredConstructor()
            constructor.isAccessible = true

            // should throws AssertionError here
            constructor.newInstance()
            fail("This line is never reached!")
        } catch (e: Throwable) {
            assertTrue("This line is reached!", true)
        }
    }
}
