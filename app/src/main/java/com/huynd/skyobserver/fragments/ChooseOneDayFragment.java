package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.huynd.skyobserver.databinding.FragmentChooseOneDayBinding;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.AvailableMonth;
import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenter;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenterImpl;
import com.huynd.skyobserver.views.ChooseOneDayView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayFragment extends BaseFragment implements ChooseOneDayView {

    public static final String TAG = ChooseOneDayFragment.class.getSimpleName();

    FragmentChooseOneDayBinding mBinding;

    ArrayAdapter<AvailableMonth> mSpinnerOutboundMonthAdapter;
    private ArrayAdapter<Integer> mSpinnerOutboundDayAdapter;

    private ArrayAdapter<AvailableMonth> mSpinnerInboundMonthAdapter;
    private ArrayAdapter<Integer> mSpinnerInboundDayAdapter;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;
    private ArrayAdapter<Airport> mSpinnerDstPortAdapter;

    private ChooseOneDayPresenter mPresenter;
    private ChooseOneDayModel mModel;

    public static Fragment newInstance() {
        return new ChooseOneDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // initialize UI widgets
        mBinding = FragmentChooseOneDayBinding.inflate(inflater, container, false);

        mSpinnerOutboundMonthAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<AvailableMonth>());
        mSpinnerOutboundDayAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());

        mSpinnerInboundMonthAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<AvailableMonth>());
        mSpinnerInboundDayAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Integer>());

        mBinding.spinnerMonthOutbound.setAdapter(mSpinnerOutboundMonthAdapter);
        mBinding.spinnerDayOutbound.setAdapter(mSpinnerOutboundDayAdapter);

        mBinding.spinnerMonthInbound.setAdapter(mSpinnerInboundMonthAdapter);
        mBinding.spinnerDayInbound.setAdapter(mSpinnerInboundDayAdapter);

        mSpinnerSrcPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mSpinnerDstPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);
        mBinding.spinnerDstPort.setAdapter(mSpinnerDstPortAdapter);

        RxAdapterView.itemSelections(mBinding.spinnerMonthOutbound)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer position) throws Exception {
                        return position >= 0 && position < mSpinnerOutboundMonthAdapter.getCount();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        AvailableMonth selectedMonthYear = mSpinnerOutboundMonthAdapter.getItem(position);
                        int selectedYear = selectedMonthYear.getYear();
                        int selectedMonth = selectedMonthYear.getMonth();
                        mPresenter.onOutboundMonthSelected(selectedYear, selectedMonth);
                    }
                });

        RxAdapterView.itemSelections(mBinding.spinnerMonthInbound)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer position) throws Exception {
                        return position >= 0 && position < mSpinnerInboundMonthAdapter.getCount();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        AvailableMonth selectedMonthYear = mSpinnerInboundMonthAdapter.getItem(position);
                        int selectedYear = selectedMonthYear.getYear();
                        int selectedMonth = selectedMonthYear.getMonth();
                        mPresenter.onInboundMonthSelected(selectedYear, selectedMonth);
                    }
                });

        RxView.clicks(mBinding.btnFindFlights).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                onBtnFindFlightsClick();
            }
        });

        RxCompoundButton.checkedChanges(mBinding.chkReturnTrip).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isChecked) throws Exception {
                onChkReturnTripCheckedChanged(isChecked);
            }
        });

        // initialize MPV pattern
        mPresenter = new ChooseOneDayPresenterImpl(this);
        mModel = new ChooseOneDayModel();
        mPresenter.setModel(mModel);
        mPresenter.initSpinnersValues();

        return mBinding.getRoot();
    }

    public void onBtnFindFlightsClick() {
        try {
            AvailableMonth availableMonthOutbound = mSpinnerOutboundMonthAdapter.getItem(
                    mBinding.spinnerMonthOutbound.getSelectedItemPosition());
            int yearOutbound = availableMonthOutbound.getYear();
            int monthOutbound = availableMonthOutbound.getMonth();
            int dayOutbound = mSpinnerOutboundDayAdapter.getItem(
                    mBinding.spinnerDayOutbound.getSelectedItemPosition());

            Airport srcPort = mSpinnerSrcPortAdapter.getItem(mBinding.spinnerSrcPort.getSelectedItemPosition());
            Airport dstPort = mSpinnerDstPortAdapter.getItem(mBinding.spinnerDstPort.getSelectedItemPosition());

            Bundle flightInfo = new Bundle();
            flightInfo.putInt("yearOutbound", yearOutbound);
            flightInfo.putInt("monthOutbound", monthOutbound);
            flightInfo.putInt("dayOutbound", dayOutbound);
            flightInfo.putString("srcPort", srcPort.getId());
            flightInfo.putString("dstPort", dstPort.getId());
            flightInfo.putBoolean("returnTrip", mBinding.chkReturnTrip.isChecked());
            if (mBinding.chkReturnTrip.isChecked()) {
                AvailableMonth availableMonthInbound = mSpinnerInboundMonthAdapter.getItem(
                        mBinding.spinnerMonthInbound.getSelectedItemPosition());
                int yearInbound = availableMonthInbound.getYear();
                int monthInbound = availableMonthInbound.getMonth();
                int dayInbound = mSpinnerInboundDayAdapter.getItem(
                        mBinding.spinnerDayInbound.getSelectedItemPosition());
                flightInfo.putInt("yearInbound", yearInbound);
                flightInfo.putInt("monthInbound", monthInbound);
                flightInfo.putInt("dayInbound", dayInbound);
            }

            ((OnFlightInfoSelectedListener) getActivity()).OnFlightInfoSelected(flightInfo);
        } catch (ClassCastException e) {
            Log.d(TAG, "Activity must implement OnFlightInfoSelectedListener.");
        }
    }

    @Override
    public void updateAvailOutBoundMonths(List<AvailableMonth> months) {
        mSpinnerOutboundMonthAdapter.clear();
        mSpinnerOutboundMonthAdapter.addAll(months);
        mSpinnerOutboundMonthAdapter.notifyDataSetChanged();
        mBinding.spinnerMonthOutbound.setSelection(0);
    }

    @Override
    public void updateAvailInBoundMonths(List<AvailableMonth> months) {
        mSpinnerInboundMonthAdapter.clear();
        mSpinnerInboundMonthAdapter.addAll(months);
        mSpinnerInboundMonthAdapter.notifyDataSetChanged();
        mBinding.spinnerMonthInbound.setSelection(0);
    }

    @Override
    public void updateAvailOutBoundDays(List<Integer> days) {
        mSpinnerOutboundDayAdapter.clear();
        mSpinnerOutboundDayAdapter.addAll(days);
        mSpinnerOutboundDayAdapter.notifyDataSetChanged();
        mBinding.spinnerDayOutbound.setSelection(0);
    }

    @Override
    public void updateAvailInBoundDays(List<Integer> days) {
        mSpinnerInboundDayAdapter.clear();
        mSpinnerInboundDayAdapter.addAll(days);
        mSpinnerInboundDayAdapter.notifyDataSetChanged();
        mBinding.spinnerDayInbound.setSelection(0);
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

    public void onChkReturnTripCheckedChanged(boolean isChecked) {
        mBinding.spinnerMonthInbound.setEnabled(isChecked);
        mBinding.spinnerDayInbound.setEnabled(isChecked);
    }
}