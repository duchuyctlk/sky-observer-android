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
import com.huynd.skyobserver.presenters.PricePerDayPresenter;
import com.huynd.skyobserver.presenters.PricePerDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.DateUtils;
import com.huynd.skyobserver.views.PricePerDayView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.twinkle94.monthyearpicker.picker.SkyObserverYearMonthPickerDialog;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragment extends BaseFragment implements PricePerDayView {

    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = PricePerDayFragment.class.getSimpleName();

    FragmentPricePerDayBinding mBinding;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;
    private ArrayAdapter<Airport> mSpinnerDstPortAdapter;

    private GridViewPricePerDayAdapter mGridViewAdapter;

    private PricePerDayPresenter mPresenter;

    private SkyObserverYearMonthPickerDialog mYearMonthPickerDialog;

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

        mSpinnerSrcPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mSpinnerDstPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);
        mBinding.spinnerDstPort.setAdapter(mSpinnerDstPortAdapter);

        mYearMonthPickerDialog = new SkyObserverYearMonthPickerDialog(getContext(),
                new YearMonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onYearMonthSet(int year, int month) {
                        mBinding.editTextMonthYear.setText(DateUtils.dateToString(year, month));
                    }
                });

        RxView.clicks(mBinding.editTextMonthYear).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mYearMonthPickerDialog.show();
            }
        });

        RxView.clicks(mBinding.btnGetPrices).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                int year = mYearMonthPickerDialog.getYear();
                int month = mYearMonthPickerDialog.getMonth() + 1;

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
                        int yearOutbound = mYearMonthPickerDialog.getYear();
                        int monthOutbound = mYearMonthPickerDialog.getMonth() + 1;
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
        mPresenter.initSpinnersValues();

        return mBinding.getRoot();
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

    @Override
    public void updateDateToEditText(String dateAsString) {
        mBinding.editTextMonthYear.setText(dateAsString);
    }
}
