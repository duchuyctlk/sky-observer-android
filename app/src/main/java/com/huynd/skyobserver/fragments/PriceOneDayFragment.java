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
import com.huynd.skyobserver.models.PriceOneDayModel;
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

    ListViewPriceOneDayAdapter mListViewAdapter;

    private PriceOneDayPresenter mPresenter;
    private PriceOneDayModel mModel;

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

        // initialize UI widgets
        mBinding = FragmentPriceOneDayBinding.inflate(inflater, container, false);
        mBinding.txtRoutine.setText(srcPort + " - " + dstPort);
        mBinding.txtFlightDate.setText(dayOutbound + "/" + monthOutbound + "/" + yearOutbound);
        mListViewAdapter = new ListViewPriceOneDayAdapter(this.getContext());
        mBinding.lstPrices.setAdapter(mListViewAdapter);

        // initialize MPV pattern
        mPresenter = new PriceOneDayPresenterImpl(this, mPricesAPI);
        mModel = new PriceOneDayModel(mPresenter);
        mPresenter.setModel(mModel);

        if (!TextUtils.isEmpty(srcPort) && !TextUtils.isEmpty(dstPort)) {
            mPresenter.getPrices(yearOutbound, monthOutbound, dayOutbound, srcPort, dstPort);
        }

        return mBinding.getRoot();
    }

    @Override
    public void updateListViewPrices(List<PricePerDay> prices) {
        mListViewAdapter.clear();
        mListViewAdapter.addAll(prices);
        mListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message));
    }
}
