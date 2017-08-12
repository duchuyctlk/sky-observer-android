package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.databinding.FragmentPricePerDayBinding;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.PricePerDayModel;
import com.huynd.skyobserver.presenters.PricePerDayPresenter;
import com.huynd.skyobserver.presenters.PricePerDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.PricePerDayView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragment extends BaseFragment implements PricePerDayView, View.OnClickListener {
    public static final String TAG = PricePerDayFragment.class.getSimpleName();

    FragmentPricePerDayBinding mBinding;

    private ArrayAdapter<Integer> mSpinnerYearAdapter;
    private ArrayAdapter<Integer> mSpinnerMonthAdapter;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;
    private ArrayAdapter<Airport> mSpinnerDstPortAdapter;

    private PricePerDayPresenter mPresenter;
    private PricePerDayModel mModel;

    public static Fragment newInstance() {
        return new PricePerDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPricePerDayBinding.inflate(inflater, container, false);

        // initialize UI widgets
        mSpinnerYearAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());
        mSpinnerMonthAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());
        mBinding.spinnerYear.setAdapter(mSpinnerYearAdapter);
        mBinding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedYear = mSpinnerYearAdapter.getItem(position);
                mPresenter.onYearSelected(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBinding.spinnerMonth.setAdapter(mSpinnerMonthAdapter);

        mSpinnerSrcPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mSpinnerDstPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);
        mBinding.spinnerDstPort.setAdapter(mSpinnerDstPortAdapter);

        // initialize MPV pattern
        mPresenter = new PricePerDayPresenterImpl(this);
        mModel = new PricePerDayModel(mPresenter);
        mPresenter.setModel(mModel);
        mPresenter.initSpinnersValues();

        return mBinding.getRoot();
    }

    @Override
    public void updateAvailYears(List<Integer> years) {
        mSpinnerYearAdapter.clear();
        mSpinnerYearAdapter.addAll(years);
        mSpinnerYearAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateAvailMonths(List<Integer> months) {
        mSpinnerMonthAdapter.clear();
        mSpinnerMonthAdapter.addAll(months);
        mSpinnerMonthAdapter.notifyDataSetChanged();
        mBinding.spinnerMonth.setSelection(0);
    }

    @Override
    public void updateAirports(List<Airport> airports) {
        mSpinnerSrcPortAdapter.clear();
        mSpinnerSrcPortAdapter.addAll(airports);
        mSpinnerSrcPortAdapter.notifyDataSetChanged();
        mBinding.spinnerSrcPort.setSelection(0);

        mSpinnerDstPortAdapter.clear();
        mSpinnerDstPortAdapter.addAll(airports);
        mSpinnerDstPortAdapter.notifyDataSetChanged();
        mBinding.spinnerDstPort.setSelection(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_prices:
                int year = mSpinnerYearAdapter.getItem(mBinding.spinnerYear.getSelectedItemPosition());
                int month = mSpinnerMonthAdapter.getItem(mBinding.spinnerMonth.getSelectedItemPosition());

                Airport srcPort = mSpinnerSrcPortAdapter.getItem(mBinding.spinnerSrcPort.getSelectedItemPosition());
                Airport dstPort = mSpinnerDstPortAdapter.getItem(mBinding.spinnerDstPort.getSelectedItemPosition());

                mPresenter.onBtnGetPricesClick(year, month, srcPort.getId(), dstPort.getId());
                break;
        }
    }
}
