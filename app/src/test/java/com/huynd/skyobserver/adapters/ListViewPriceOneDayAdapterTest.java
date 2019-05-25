package com.huynd.skyobserver.adapters;

import android.view.View;

import com.huynd.skyobserver.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by HuyND on 9/10/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class ListViewPriceOneDayAdapterTest {
    private MainActivity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testGetViewWithNullItem() throws Exception {
        ListViewPriceOneDayAdapter adapter = new ListViewPriceOneDayAdapter(mActivity);
        adapter.add(null);
        View view = adapter.getView(0, null, null);
        ListViewPriceOneDayAdapter.ViewHolder vH = (ListViewPriceOneDayAdapter.ViewHolder) view.getTag();
        assertEquals("", vH.getTextViewDepartTime().getText().toString());
        assertEquals("", vH.getTextViewArriveTime().getText().toString());
    }
}
