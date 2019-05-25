package com.huynd.skyobserver.adapters

import com.huynd.skyobserver.activities.MainActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * Created by HuyND on 9/12/2017.
 */

@RunWith(RobolectricTestRunner::class)
class ListViewPriceOneDayAdapterTest {
    private lateinit var mActivity: MainActivity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mActivity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun testGetViewWithNullItem() {
        val adapter = ListViewPriceOneDayAdapter(mActivity)
        adapter.add(null)
        val view = adapter.getView(0, null, null)
        val vH = view.tag as ListViewPriceOneDayAdapter.ViewHolder
        vH.run {
            assertEquals("", textViewDepartTime.text)
            assertEquals("", textViewArriveTime.text)
        }
    }
}
