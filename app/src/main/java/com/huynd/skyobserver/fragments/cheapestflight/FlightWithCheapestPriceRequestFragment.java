package com.huynd.skyobserver.fragments.cheapestflight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.huynd.skyobserver.databinding.FragmentFlightWithCheapestPriceRequestBinding;
import com.huynd.skyobserver.fragments.BaseFragment;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.AvailableMonth;
import com.huynd.skyobserver.models.cheapestflight.FlightWithCheapestPriceModel;
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPricePresenter;
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPricePresenterImpl;
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by HuyND on 11/19/2017.
 */

public class FlightWithCheapestPriceRequestFragment extends BaseFragment implements FlightWithCheapestPriceView {

    public static final String TAG = FlightWithCheapestPriceRequestFragment.class.getSimpleName();

    FragmentFlightWithCheapestPriceRequestBinding mBinding;

    ArrayAdapter<AvailableMonth> mSpinnerOutboundMonthAdapter;
    private ArrayAdapter<Integer> mSpinnerOutboundDayAdapter;

    private ArrayAdapter<AvailableMonth> mSpinnerInboundMonthAdapter;
    private ArrayAdapter<Integer> mSpinnerInboundDayAdapter;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;

    private FlightWithCheapestPricePresenter mPresenter;
    private FlightWithCheapestPriceModel mModel;

    public static Fragment newInstance() {
        return new FlightWithCheapestPriceRequestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // initialize UI widgets
        mBinding = FragmentFlightWithCheapestPriceRequestBinding.inflate(inflater, container, false);

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
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);

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
        mPresenter = new FlightWithCheapestPricePresenterImpl(this);
        mModel = new FlightWithCheapestPriceModel();
        mPresenter.setModel(mModel);
        mPresenter.initSpinnersValues();

        return mBinding.getRoot();
    }

    public void onBtnFindFlightsClick() {
        try {
            Bundle flightInfo = getFlightDates();

            Airport srcPort = mSpinnerSrcPortAdapter.getItem(mBinding.spinnerSrcPort.getSelectedItemPosition());
            flightInfo.putString("srcPort", srcPort.getId());

            ((OnFlightWithCheapestPriceInfoSelectedListener) getActivity()).OnFlightWithCheapestPriceInfoSelected(flightInfo);
        } catch (ClassCastException e) {
            Log.d(TAG, "Activity must implement OnFlightInfoSelectedListener.");
        }
    }

    private Bundle getFlightDates() {
        Bundle flightInfo = new Bundle();

        // outbound date
        AvailableMonth availableMonthOutbound = mSpinnerOutboundMonthAdapter.getItem(
                mBinding.spinnerMonthOutbound.getSelectedItemPosition());
        int yearOutbound = availableMonthOutbound.getYear();
        int monthOutbound = availableMonthOutbound.getMonth();
        int dayOutbound = mSpinnerOutboundDayAdapter.getItem(
                mBinding.spinnerDayOutbound.getSelectedItemPosition());
        flightInfo.putInt("yearOutbound", yearOutbound);
        flightInfo.putInt("monthOutbound", monthOutbound);
        flightInfo.putInt("dayOutbound", dayOutbound);

        // inbound date
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

        return flightInfo;
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
    }

    public void onChkReturnTripCheckedChanged(boolean isChecked) {
        mBinding.spinnerMonthInbound.setEnabled(isChecked);
        mBinding.spinnerDayInbound.setEnabled(isChecked);
    }
}