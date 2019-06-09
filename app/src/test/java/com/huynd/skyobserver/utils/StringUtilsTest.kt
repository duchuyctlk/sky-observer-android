package com.huynd.skyobserver.utils

import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Created by HuyND on 06/09/2019.
 */

class StringUtilsTest {
    @Test
    fun testPrivateConstructor() {
        try {
            val constructor = StringUtils::class.java.getDeclaredConstructor()
            constructor.isAccessible = true

            // should throws AssertionError here
            constructor.newInstance()
            fail("This line is never reached!")
        } catch (e: Throwable) {
            assertTrue(true, "This line is reached!")
        }
    }
}
