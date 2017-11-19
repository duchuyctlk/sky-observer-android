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
import com.huynd.skyobserver.fragments.cheapestflight.FlightWithCheapestPriceRequestFragment;
import com.huynd.skyobserver.fragments.cheapestflight.OnFlightWithCheapestPriceInfoSelectedListener;
import com.huynd.skyobserver.presenters.NavigationDrawerPresenter;
import com.huynd.skyobserver.views.NavigationDrawerView;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends BaseActivity implements ListView.OnItemClickListener,
        NavigationDrawerView,
        OnFlightInfoSelectedListener,
        OnFlightWithCheapestPriceInfoSelectedListener {

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

        checkForUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
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
            case 2:
                setFragment(FlightWithCheapestPriceRequestFragment.newInstance(),
                        FlightWithCheapestPriceRequestFragment.TAG, true);
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

    @Override
    public void OnFlightWithCheapestPriceInfoSelected(Bundle flightInfo) {
        // TODO
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}
