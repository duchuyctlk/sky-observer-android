package com.huynd.skyobserver.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.annotations.NonNull;

/**
 * Created by HuyND on 8/5/2017.
 */

public class NavigationDrawerListAdapter extends ArrayAdapter<String> {
    public NavigationDrawerListAdapter(@NonNull Context context, @LayoutRes int resource,
                                       @NonNull String[] objects, @NonNull ListView listView) {
        super(context, resource, objects);
        listView.setAdapter(this);
    }
}
