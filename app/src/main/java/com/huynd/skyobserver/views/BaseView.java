package com.huynd.skyobserver.views;

/**
 * Created by HuyND on 8/10/2017.
 */

public interface BaseView {
    void showLoadingDialog();

    void dismissLoadingDialog();

    void showFailedDialog(String errorMessage);
}
