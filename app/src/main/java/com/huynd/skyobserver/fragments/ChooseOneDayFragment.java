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
import com.huynd.skyobserver.databinding.FragmentChooseOneDayBinding;
import com.huynd.skyobserver.models.Airport;
import com.huynd.skyobserver.models.AvailableMonth;
import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenter;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenterImpl;
import com.huynd.skyobserver.views.ChooseOneDayView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayFragment extends BaseFragment implements ChooseOneDayView,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    public static final String TAG = ChooseOneDayFragment.class.getSimpleName();

    FragmentChooseOneDayBinding mBinding;

    private ArrayAdapter<AvailableMonth> mSpinnerOutboundMonthAdapter;
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

        mBinding.spinnerMonthOutbound.setOnItemSelectedListener(this);

        mBinding.spinnerMonthInbound.setOnItemSelectedListener(this);

        mBinding.btnFindFlights.setOnClickListener(this);

        // initialize MPV pattern
        mPresenter = new ChooseOneDayPresenterImpl(this);
        mModel = new ChooseOneDayModel();
        mPresenter.setModel(mModel);
        mPresenter.initSpinnersValues();

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_flights:
                // TODO
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_month_outbound:
                AvailableMonth selectedMonthYear = mSpinnerOutboundMonthAdapter.getItem(position);
                int selectedYear = selectedMonthYear.getYear();
                int selectedMonth = selectedMonthYear.getMonth();
                mPresenter.onOutboundMonthSelected(selectedYear, selectedMonth);
                break;
            case R.id.spinner_month_inbound:
                selectedMonthYear = mSpinnerInboundMonthAdapter.getItem(position);
                selectedYear = selectedMonthYear.getYear();
                selectedMonth = selectedMonthYear.getMonth();
                mPresenter.onInboundMonthSelected(selectedYear, selectedMonth);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}