package com.huynd.skyobserver.utils

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AlertDialog

import com.huynd.skyobserver.R

/**
 * Created by HuyND on 8/10/2017.
 */

class FlowUtils {
    companion object {
        val instance = FlowUtils()
    }

    var mProgressDialog: ProgressDialog? = null

    fun showLoadingDialog(context: Context) {
        if (!isLoadingDialogShowing()) {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog?.run {
                setMessage(context.getString(R.string.loading_string))
                setCanceledOnTouchOutside(false)
                show()
            }
        }
    }

    fun dismissLoadingDialog() {
        if (isLoadingDialogShowing()) {
            try {
                mProgressDialog?.dismiss()
                mProgressDialog = null
            } catch (ex: IllegalArgumentException) {
                ex.printStackTrace()
            }
        }
    }

    fun showAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context).let {
            it.setTitle(title)
            it.setMessage(message)
            it.setNeutralButton(context.getString(R.string.ok), null)
            it.create()
        }.show()
    }

    private fun isLoadingDialogShowing() = mProgressDialog?.isShowing ?: false
}
