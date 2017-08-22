package com.huynd.skyobserver.presenters;

import com.huynd.skyobserver.models.ChooseOneDayModel;
import com.huynd.skyobserver.views.ChooseOneDayView;

/**
 * Created by HuyND on 8/22/2017.
 */

public class ChooseOneDayPresenterImpl implements ChooseOneDayPresenter {
    private ChooseOneDayView mView;
    private ChooseOneDayModel mModel;

    public ChooseOneDayPresenterImpl(ChooseOneDayView view) {
        mView = view;
    }

    @Override
    public void setModel(ChooseOneDayModel model) {
        mModel = model;
    }
}
