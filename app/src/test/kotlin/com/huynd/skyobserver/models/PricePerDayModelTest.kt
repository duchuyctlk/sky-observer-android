package com.huynd.skyobserver.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by HuyND on 9/11/2017.
 */

class PricePerDayModelTest {
    @Test
    @Throws(Exception::class)
    fun shouldInitSpinnersValuesIfNeeded() {
        val model = PricePerDayModel(null)
        val availMonths = model.getAvailMonths(2018)
        assertNotNull(availMonths)
        assertEquals(12, availMonths.size)
    }
}
