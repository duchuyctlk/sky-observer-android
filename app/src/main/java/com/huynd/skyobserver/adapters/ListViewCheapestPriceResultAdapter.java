package com.huynd.skyobserver.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo;

import java.util.List;

/**
 * Created by HuyND on 9/28/2017.
 */

public class ListViewCheapestPriceResultAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<CountryPriceInfo> mCountryPriceInfo;

    @Override
    public int getGroupCount() {
        return mCountryPriceInfo != null ? mCountryPriceInfo.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCountryPriceInfo != null ?
                mCountryPriceInfo.get(groupPosition).getAirportPriceInfoCount() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCountryPriceInfo != null ? mCountryPriceInfo.get(groupPosition) : null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCountryPriceInfo != null ?
                mCountryPriceInfo.get(groupPosition).getAirportPriceInfo(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {
        // TODO
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
