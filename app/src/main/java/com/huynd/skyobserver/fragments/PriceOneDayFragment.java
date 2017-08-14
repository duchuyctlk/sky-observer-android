package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huynd.skyobserver.SkyObserverApp;
import com.huynd.skyobserver.databinding.FragmentPriceOneDayBinding;
import com.huynd.skyobserver.models.PriceOneDayModel;
import com.huynd.skyobserver.presenters.PriceOneDayPresenter;
import com.huynd.skyobserver.presenters.PriceOneDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.views.PriceOneDayView;

import javax.inject.Inject;

/**
 * Created by HuyND on 8/14/2017.
 */

public class PriceOneDayFragment extends BaseFragment implements PriceOneDayView {
    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = PriceOneDayFragment.class.getSimpleName();

    FragmentPriceOneDayBinding mBinding;

    private PriceOneDayPresenter mPresenter;
    private PriceOneDayModel mModel;

    public static Fragment newInstance() {
        return new PriceOneDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inject
        SkyObserverApp app = (SkyObserverApp)getActivity().getApplication();
        app.getSkyObserverComponent().inject(this);

        // initialize UI widgets
        mBinding = FragmentPriceOneDayBinding.inflate(inflater, container, false);

        // initialize MPV pattern
        mPresenter = new PriceOneDayPresenterImpl(this, mPricesAPI);
        mModel = new PriceOneDayModel(mPresenter);
        mPresenter.setModel(mModel);

        return mBinding.getRoot();
    }
}
