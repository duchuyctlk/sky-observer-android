package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huynd.skyobserver.databinding.FragmentPricePerDayBinding;

/**
 * Created by HuyND on 8/6/2017.
 */

public class PricePerDayFragment extends Fragment {
    public static final String TAG = PricePerDayFragment.class.getSimpleName();

    FragmentPricePerDayBinding mBinding;

    public static Fragment newInstance() {
        return new PricePerDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPricePerDayBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
}
