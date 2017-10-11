package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverApp;
import com.huynd.skyobserver.adapters.ListViewPriceOneDayAdapter;
import com.huynd.skyobserver.databinding.FragmentPriceOneDayBinding;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.presenters.PriceOneDayPresenter;
import com.huynd.skyobserver.presenters.PriceOneDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.PriceOneDayView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by HuyND on 8/14/2017.
 */

public class PriceOneDayFragment extends BaseFragment implements PriceOneDayView {
    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = PriceOneDayFragment.class.getSimpleName();

    FragmentPriceOneDayBinding mBinding;

    ListViewPriceOneDayAdapter mListViewOutboundAdapter;
    ListViewPriceOneDayAdapter mListViewInboundAdapter;

    private PriceOneDayPresenter mPresenter;

    public static Fragment newInstance() {
        return new PriceOneDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inject
        SkyObserverApp app = (SkyObserverApp) getActivity().getApplication();
        app.getSkyObserverComponent().inject(this);

        // get data from intent
        Bundle args = getArguments();
        int yearOutbound = args.getInt("yearOutbound");
        int monthOutbound = args.getInt("monthOutbound");
        int dayOutbound = args.getInt("dayOutbound");
        String srcPort = args.getString("srcPort");
        String dstPort = args.getString("dstPort");
        boolean returnTrip = args.getBoolean("returnTrip");
        int yearInbound = 0, monthInbound = 0, dayInbound = 0;
        if (returnTrip) {
            yearInbound = args.getInt("yearInbound");
            monthInbound = args.getInt("monthInbound");
            dayInbound = args.getInt("dayInbound");
        }

        // initialize UI widgets
        mBinding = FragmentPriceOneDayBinding.inflate(inflater, container, false);
        mBinding.txtRoutineOutbound.setText(srcPort + " - " + dstPort);
        mBinding.txtFlightDateOutbound.setText(dayOutbound + "/" + monthOutbound + "/" + yearOutbound);
        mListViewOutboundAdapter = new ListViewPriceOneDayAdapter(this.getContext());
        mBinding.lstPricesOutbound.setAdapter(mListViewOutboundAdapter);
        if (returnTrip) {
            mBinding.layoutInbound.setVisibility(View.VISIBLE);
            mBinding.txtRoutineInbound.setText(dstPort + " - " + srcPort);
            mBinding.txtFlightDateInbound.setText(dayInbound + "/" + monthInbound + "/" + yearInbound);
            mListViewInboundAdapter = new ListViewPriceOneDayAdapter(this.getContext());
            mBinding.lstPricesInbound.setAdapter(mListViewInboundAdapter);
        } else {
            mBinding.layoutInbound.setVisibility(View.GONE);
        }

        // initialize MPV pattern
        mPresenter = new PriceOneDayPresenterImpl(this, mPricesAPI);

        if (!TextUtils.isEmpty(srcPort) && !TextUtils.isEmpty(dstPort)) {
            mPresenter.getPrices(yearOutbound, monthOutbound, dayOutbound, srcPort, dstPort, true);

            if (returnTrip) {
                mPresenter.getPrices(yearInbound, monthInbound, dayInbound, dstPort, srcPort, false);
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void updateListViewOutboundPrices(List<PricePerDay> prices) {
        mListViewOutboundAdapter.clear();
        mListViewOutboundAdapter.addAll(prices);
        mListViewOutboundAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateListViewInboundPrices(List<PricePerDay> prices) {
        mListViewInboundAdapter.clear();
        mListViewInboundAdapter.addAll(prices);
        mListViewInboundAdapter.notifyDataSetChanged();
    }

    @Override
    public void showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message));
    }
}
