package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.widget.ArrayAdapter
import android.widget.ListView

/**
 * Created by HuyND on 8/5/2017.
 */

class NavigationDrawerListAdapter(@NonNull context: Context, @LayoutRes resource: Int,
                                  @NonNull objects: Array<String>, @NonNull listView: ListView) :
        ArrayAdapter<String>(context, resource, objects) {

    init {
        listView.adapter = this
    }
}
