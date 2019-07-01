package com.huynd.skyobserver.utils

import com.huynd.skyobserver.models.bestdates.BestDatesInfo
import com.huynd.skyobserver.models.cheapestflight.month.ResponseId
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by HuyND on 7/1/2019.
 */
class BestDatesInfoComparatorTest {
    private lateinit var mComparator: BestDatesInfoComparator
    private lateinit var mBestDatesInfo: BestDatesInfo

    @Before
    fun setUp() {
        mComparator = BestDatesInfoComparator.instance
        mBestDatesInfo = BestDatesInfo()
    }

    @Test
    fun testComparing2NullValues() {
        assertEquals(0, mComparator.compare(null, null))
    }

    @Test
    fun testComparingNullValueAgainstObject() {
        assertEquals(-1, mComparator.compare(null, mBestDatesInfo))
        assertEquals(1, mComparator.compare(mBestDatesInfo, null))
    }

    @Test
    fun testComparing2Objects() {
        val bestDatesInfo2 = BestDatesInfo()
        bestDatesInfo2.outboundId = ResponseId(year = 2019, monthInYear = 6)

        // same year, different months
        mBestDatesInfo.outboundId = ResponseId(year = 2019, monthInYear = 1)
        assertEquals(-1, mComparator.compare(mBestDatesInfo, bestDatesInfo2))

        // same year, same month
        mBestDatesInfo.outboundId = ResponseId(year = 2019, monthInYear = 6)
        assertEquals(0, mComparator.compare(mBestDatesInfo, bestDatesInfo2))

        // different years
        mBestDatesInfo.outboundId = ResponseId(year = 2020)
        assertEquals(1, mComparator.compare(mBestDatesInfo, bestDatesInfo2))
    }
}
