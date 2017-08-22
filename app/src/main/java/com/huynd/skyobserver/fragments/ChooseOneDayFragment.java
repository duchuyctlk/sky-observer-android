package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.databinding.FragmentChooseOneDayBinding;
import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenter;
import com.huynd.skyobserver.presenters.ChooseOneDayPresenterImpl;
import com.huynd.skyobserver.views.ChooseOneDayView;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayFragment extends BaseFragment implements ChooseOneDayView,
        View.OnClickListener {

    public static final String TAG = ChooseOneDayFragment.class.getSimpleName();

    FragmentChooseOneDayBinding mBinding;

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
        mBinding.btnFindFlights.setOnClickListener(this);

        // initialize MPV pattern
        mPresenter = new ChooseOneDayPresenterImpl(this);
        mModel = new ChooseOneDayModel(mPresenter);
        mPresenter.setModel(mModel);

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
}