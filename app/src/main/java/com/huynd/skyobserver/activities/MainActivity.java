package com.huynd.skyobserver.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.adapters.NavigationDrawerListAdapter;
import com.huynd.skyobserver.databinding.ActivityMainBinding;
import com.huynd.skyobserver.fragments.ChooseOneDayFragment;
import com.huynd.skyobserver.fragments.OnFlightInfoSelectedListener;
import com.huynd.skyobserver.fragments.PriceOneDayFragment;
import com.huynd.skyobserver.fragments.PricePerDayFragment;
import com.huynd.skyobserver.presenters.NavigationDrawerPresenter;
import com.huynd.skyobserver.views.NavigationDrawerView;

public class MainActivity extends BaseActivity implements ListView.OnItemClickListener,
        NavigationDrawerView,
        OnFlightInfoSelectedListener {

    ActivityMainBinding binding;

    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationDrawerListAdapter mNavigationDrawerListAdapter;
    private NavigationDrawerPresenter mNavigationDrawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        setupNavigationDrawer();

        mNavigationDrawerPresenter.onItemClick(0);
    }

    private void setupNavigationDrawer() {
        binding.listviewLeftDrawer.setOnItemClickListener(this);

        mNavigationDrawerListAdapter = new NavigationDrawerListAdapter(this, R.layout.drawer_list_item,
                getResources().getStringArray(R.array.array_of_navigation_drawer_item_title),
                binding.listviewLeftDrawer);
        mNavigationDrawerPresenter = new NavigationDrawerPresenter(this, mNavigationDrawerListAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, binding.layoutDrawer,
                R.string.drawer_open, R.string.drawer_close);

        binding.layoutDrawer.addDrawerListener(mDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mNavigationDrawerPresenter.onItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectItem(int position, String title) {
        switch (position) {
            case 0:
                setFragment(PricePerDayFragment.newInstance(), PricePerDayFragment.TAG, true);
                break;
            case 1:
                setFragment(ChooseOneDayFragment.newInstance(), ChooseOneDayFragment.TAG, true);
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        binding.listviewLeftDrawer.setItemChecked(position, true);
        setTitle(title);
        binding.layoutDrawer.closeDrawer(binding.listviewLeftDrawer);
    }

    @Override
    public void OnFlightInfoSelected(Bundle flightInfo) {
        PriceOneDayFragment fragment = (PriceOneDayFragment) PriceOneDayFragment.newInstance();
        fragment.setArguments(flightInfo);
        setFragment(fragment, PriceOneDayFragment.TAG, false);
    }
}
