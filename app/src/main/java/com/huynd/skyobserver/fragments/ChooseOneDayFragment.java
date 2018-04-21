package com.huynd.skyobserver.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.huynd.skyobserver.databinding.FragmentChooseOneDayBinding;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenter;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenterImpl;
import com.huynd.skyobserver.views.ChooseOneDayView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayFragment extends BaseFragment implements ChooseOneDayView {

    public static final String TAG = ChooseOneDayFragment.class.getSimpleName();

    FragmentChooseOneDayBinding mBinding;

    private ArrayAdapter<Airport> mSpinnerSrcPortAdapter;
    private ArrayAdapter<Airport> mSpinnerDstPortAdapter;

    private ChooseOneDayPresenter mPresenter;
    private ChooseOneDayModel mModel;

    DatePickerDialog mOutboundDatePickerDialog, mInboundDatePickerDialog;

    public static Fragment newInstance() {
        return new ChooseOneDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // initialize UI widgets
        mBinding = FragmentChooseOneDayBinding.inflate(inflater, container, false);

        mSpinnerSrcPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mSpinnerDstPortAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<Airport>());
        mBinding.spinnerSrcPort.setAdapter(mSpinnerSrcPortAdapter);
        mBinding.spinnerDstPort.setAdapter(mSpinnerDstPortAdapter);

        RxView.clicks(mBinding.editTextDateOutbound).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mOutboundDatePickerDialog.show();
            }
        });

        RxView.clicks(mBinding.editTextDateInbound).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mInboundDatePickerDialog.show();
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
            DatePicker outboundDatePicker = mOutboundDatePickerDialog.getDatePicker();
            int yearOutbound = outboundDatePicker.getYear();
            int monthOutbound = outboundDatePicker.getMonth() + 1;
            int dayOutbound = outboundDatePicker.getDayOfMonth();

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
                DatePicker inboundDatePicker = mInboundDatePickerDialog.getDatePicker();
                int yearInbound = inboundDatePicker.getYear();
                int monthInbound = inboundDatePicker.getMonth() + 1;
                int dayInbound = inboundDatePicker.getDayOfMonth();
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
    public void updateDatePickers(int startYear, int startMonth, int startDayOfMonth) {
        mOutboundDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mPresenter.setDateToEditText(year, month, dayOfMonth, true);
            }
        }, startYear, startMonth, startDayOfMonth);

        mInboundDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mPresenter.setDateToEditText(year, month, dayOfMonth, false);
            }
        }, startYear, startMonth, startDayOfMonth);
    }

    @Override
    public void updateDateToEditText(String dateAsString, boolean isOutbound) {
        if (isOutbound) {
            mBinding.editTextDateOutbound.setText(dateAsString);
        } else {
            mBinding.editTextDateInbound.setText(dateAsString);
        }
    }

    public void onChkReturnTripCheckedChanged(boolean isChecked) {
        mBinding.editTextDateInbound.setEnabled(isChecked);
    }

    @Override
    public void setDatePickersMinDate(long minDate) {
        mOutboundDatePickerDialog.getDatePicker().setMinDate(minDate);
        mInboundDatePickerDialog.getDatePicker().setMinDate(minDate);
    }
}