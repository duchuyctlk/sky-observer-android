package com.huynd.skyobserver.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by HuyND on 5/25/2019.
 */

class DateUtilsTest {
    @Test
    fun testPrivateConstructor() {
        try {
            val constructor = DateUtils::class.java.getDeclaredConstructor()
            constructor.isAccessible = true

            // should throws AssertionError here
            constructor.newInstance()
            fail("This line is never reached!")
        } catch (e: Throwable) {
            assertTrue("This line is reached!", true)
        }
    }

    @Test
    fun testConvertInvalidStringToNull() {
        assertNull(DateUtils.convertStringToDate("2017/10/01"))
    }

    @Test
    fun testGetNumberOfDaysInMonth() {
        // normal February
        assertEquals(28, DateUtils.getNumberOfDaysInMonth(2017, 2))
        assertEquals(28, DateUtils.getNumberOfDaysInMonth(1900, 2))

        // leap year's February
        assertEquals(29, DateUtils.getNumberOfDaysInMonth(2016, 2))

        // April/June/Sep/Nov
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 4))
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 6))
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 9))
        assertEquals(30, DateUtils.getNumberOfDaysInMonth(2017, 11))

        // else
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 1))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 3))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 5))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 7))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 8))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 10))
        assertEquals(31, DateUtils.getNumberOfDaysInMonth(2017, 12))
    }
}
