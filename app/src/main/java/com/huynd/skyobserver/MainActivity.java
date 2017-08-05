package com.huynd.skyobserver;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huynd.skyobserver.adapters.NavigationDrawerListAdapter;
import com.huynd.skyobserver.databinding.ActivityMainBinding;
import com.huynd.skyobserver.presenters.NavigationDrawerPresenter;
import com.huynd.skyobserver.views.NavigationDrawerView;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, NavigationDrawerView {
    ActivityMainBinding binding;
    private NavigationDrawerListAdapter mNavigationDrawerListAdapter;
    private NavigationDrawerPresenter mNavigationDrawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.listviewLeftDrawer.setOnItemClickListener(this);

        mNavigationDrawerListAdapter = new NavigationDrawerListAdapter(this, R.layout.drawer_list_item,
                getResources().getStringArray(R.array.array_of_navigation_drawer_item_title),
                binding.listviewLeftDrawer);
        mNavigationDrawerPresenter = new NavigationDrawerPresenter(this, mNavigationDrawerListAdapter);

        mNavigationDrawerPresenter.onItemClick(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mNavigationDrawerPresenter.onItemClick(position);
    }

    @Override
    public void selectItem(int position, String title) {
        // TODO
        // Highlight the selected item, update the title, and close the drawer
        binding.listviewLeftDrawer.setItemChecked(position, true);
        setTitle(title);
        binding.layoutDrawer.closeDrawer(binding.listviewLeftDrawer);
    }
}
