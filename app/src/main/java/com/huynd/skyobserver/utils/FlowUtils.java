package com.huynd.skyobserver.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.huynd.skyobserver.R;

/**
 * Created by HuyND on 8/10/2017.
 */

public class FlowUtils {
    private static FlowUtils sInstance;
    ProgressDialog mProgressDialog;

    public static FlowUtils getInstance() {
        if (sInstance == null) {
            sInstance = new FlowUtils();
        }
        return sInstance;
    }

    public void showLoadingDialog(Context context) {
        if (!isLoadingDialogShowing()) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_string));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (isLoadingDialogShowing()) {
            try {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isLoadingDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void showAlert(Context context, String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setTitle(title);
        bld.setMessage(message);
        bld.setNeutralButton(context.getString(R.string.ok), null);
        bld.create().show();
    }
}
