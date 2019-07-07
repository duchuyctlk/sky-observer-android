package com.huynd.skyobserver.utils

import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Created by HuyND on 7/3/2019.
 */

class CoroutineUtilsTest {
    @Test
    fun testConstructor() {
        try {
            // should throws AssertionError here
            CoroutineUtils()
            fail("This line is never reached!")
        } catch (e: AssertionError) {
            assertTrue(true, "This line is reached!")
        }
    }
}
