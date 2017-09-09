package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.huynd.skyobserver.SkyObserverApp;
import com.huynd.skyobserver.adapters.GridViewPricePerDayAdapter;
import com.huynd.skyobserver.databinding.FragmentPricePerDayBinding;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.models.PricePerDayModel;
import com.huynd.skyobserver.presenters.PricePerDayPresenter;
import com.huynd.skyobserver.presenters.PricePerDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.PricePerDayView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragment extends BaseFragment implements PricePerDayView {

    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = PricePerDayFragment.class.getSimpleName();

    FragmentPricePerDayBinding mBinding;

    ArrayAdapter<Integer> mSpinnerYearAdapter;
    private ArrayAdapter<Integer> mSpinnerMonthAdapter;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;
    private ArrayAdapter<Airport> mSpinnerDstPortAdapter;

    private GridViewPricePerDayAdapter mGridViewAdapter;

    private PricePerDayPresenter mPresenter;
    private PricePerDayModel mModel;

    public static Fragment newInstance() {
        return new PricePerDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inject
        SkyObserverApp app = (SkyObserverApp) getActivity().getApplication();
        app.getSkyObserverComponent().inject(this);

        // initialize UI widgets
        mBinding = FragmentPricePerDayBinding.inflate(inflater, container, false);

        mSpinnerYearAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());
        mSpinnerMonthAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());
        mBinding.spinnerYear.setAdapter(mSpinnerYearAdapter);
        mBinding.spinnerMonth.setAdapter(mSpinnerMonthAdapter);
        RxAdapterView.itemSelections(mBinding.spinnerYear)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer position) throws Exception {
                        return position >= 0 && position < mSpinnerYearAdapter.getCount();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        int selectedYear = mSpinnerYearAdapter.getItem(position);
                        mPresenter.onYearSelected(selectedYear);
                    }
                });

        mSpinnerSrcPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mSpinnerDstPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);
        mBinding.spinnerDstPort.setAdapter(mSpinnerDstPortAdapter);

        RxView.clicks(mBinding.btnGetPrices).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                int year = mSpinnerYearAdapter.getItem(mBinding.spinnerYear.getSelectedItemPosition());
                int month = mSpinnerMonthAdapter.getItem(mBinding.spinnerMonth.getSelectedItemPosition());

                Airport srcPort = mSpinnerSrcPortAdapter.getItem(mBinding.spinnerSrcPort.getSelectedItemPosition());
                Airport dstPort = mSpinnerDstPortAdapter.getItem(mBinding.spinnerDstPort.getSelectedItemPosition());

                mPresenter.onBtnGetPricesClick(year, month, srcPort.getId(), dstPort.getId());
            }
        });

        mGridViewAdapter = new GridViewPricePerDayAdapter(this.getContext());
        mBinding.gridViewPrice.setAdapter(mGridViewAdapter);

        RxAdapterView.itemClicks(mBinding.gridViewPrice).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer position) throws Exception {
                PricePerDay price = mGridViewAdapter.getItem(position);
                if (price != null) {
                    try {
                        int yearOutbound = mSpinnerYearAdapter.getItem(mBinding.spinnerYear.getSelectedItemPosition());
                        int monthOutbound = mSpinnerMonthAdapter.getItem(mBinding.spinnerMonth.getSelectedItemPosition());
                        int dayOutbound = price.getDay();

                        Airport srcPort = mSpinnerSrcPortAdapter.getItem(mBinding.spinnerSrcPort.getSelectedItemPosition());
                        Airport dstPort = mSpinnerDstPortAdapter.getItem(mBinding.spinnerDstPort.getSelectedItemPosition());

                        Bundle flightInfo = new Bundle();
                        flightInfo.putBoolean("return", false);
                        flightInfo.putInt("yearOutbound", yearOutbound);
                        flightInfo.putInt("monthOutbound", monthOutbound);
                        flightInfo.putInt("dayOutbound", dayOutbound);
                        flightInfo.putString("srcPort", srcPort.getId());
                        flightInfo.putString("dstPort", dstPort.getId());

                        ((OnFlightInfoSelectedListener) getActivity()).OnFlightInfoSelected(flightInfo);
                    } catch (ClassCastException e) {
                        Log.d(TAG, "Activity must implement OnFlightInfoSelectedListener.");
                    }
                }
            }
        });

        // initialize MPV pattern
        mPresenter = new PricePerDayPresenterImpl(this, mPricesAPI);
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
    public void updateGridViewPrices(List<PricePerDay> prices) {
        mGridViewAdapter.clear();
        mGridViewAdapter.addAll(prices);
        mGridViewAdapter.notifyDataSetChanged();
    }
}
