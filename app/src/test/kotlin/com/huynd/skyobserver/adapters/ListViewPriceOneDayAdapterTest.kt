package com.huynd.skyobserver.adapters

import android.os.Build.VERSION_CODES.LOLLIPOP
import com.huynd.skyobserver.BuildConfig
import com.huynd.skyobserver.activities.MainActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Created by HuyND on 9/12/2017.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(LOLLIPOP))
class ListViewPriceOneDayAdapterTest {
    var mActivity: MainActivity? = null

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
        assertEquals("", vH.textViewDepartTime.text.toString())
        assertEquals("", vH.textViewArriveTime.text.toString())
    }
}
