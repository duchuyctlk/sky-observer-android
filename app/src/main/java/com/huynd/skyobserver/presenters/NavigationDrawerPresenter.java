package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.adapters.NavigationDrawerListAdapter;
import com.huynd.skyobserver.views.NavigationDrawerView;

/**
 * Created by HuyND on 8/5/2017.
 */

public class NavigationDrawerPresenter {
    NavigationDrawerView mView;
    NavigationDrawerListAdapter mAdapter;

    public NavigationDrawerPresenter(NavigationDrawerView view, NavigationDrawerListAdapter adapter) {
        mView = view;
        mAdapter = adapter;
    }

    public void onItemClick(int position) {
        if (mView != null) {
            mView.selectItem(position, mAdapter.getItem(position));
        }
    }
}
