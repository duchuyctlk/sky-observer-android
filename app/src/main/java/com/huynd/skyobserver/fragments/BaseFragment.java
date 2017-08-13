package com.huynd.skyobserver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.utils.FlowUtils;
import com.huynd.skyobserver.views.BaseView;

/**
 * Created by HuyND on 8/10/2017.
 */

public class BaseFragment extends Fragment implements BaseView {
    @Override
    public void dismissLoadingDialog() {
        FlowUtils.getInstance().dismissLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        FlowUtils.getInstance().showLoadingDialog(this.getContext());
    }
}
