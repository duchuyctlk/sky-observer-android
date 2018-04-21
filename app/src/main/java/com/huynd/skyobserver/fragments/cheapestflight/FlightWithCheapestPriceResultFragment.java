package com.huynd.skyobserver.fragments.cheapestflight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huynd.skyobserver.SkyObserverApp;
import com.huynd.skyobserver.databinding.FragmentFlightWithCheapestPriceResultBinding;
import com.huynd.skyobserver.fragments.BaseFragment;
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenter;
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView;

import javax.inject.Inject;

/**
 * Created by HuyND on 4/21/2018.
 */

public class FlightWithCheapestPriceResultFragment extends BaseFragment implements
        FlightWithCheapestPriceResultView {

    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = FlightWithCheapestPriceResultFragment.class.getSimpleName();

    FragmentFlightWithCheapestPriceResultBinding mBinding;

    private FlightWithCheapestPriceResultPresenter mPresenter;

    public static Fragment newInstance() {
        return new FlightWithCheapestPriceResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inject
        SkyObserverApp app = (SkyObserverApp) getActivity().getApplication();
        app.getSkyObserverComponent().inject(this);

        // initialize UI widgets
        mBinding = FragmentFlightWithCheapestPriceResultBinding.inflate(inflater, container, false);

        // initialize MPV pattern
        mPresenter = new FlightWithCheapestPriceResultPresenterImpl(this, mPricesAPI);

        return mBinding.getRoot();
    }
}
