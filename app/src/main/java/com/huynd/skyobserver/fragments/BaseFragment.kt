package com.huynd.skyobserver.fragments

import android.support.v4.app.Fragment

import com.huynd.skyobserver.R
import com.huynd.skyobserver.utils.FlowUtils
import com.huynd.skyobserver.views.BaseView

/**
 * Created by HuyND on 8/10/2017.
 */

open class BaseFragment : Fragment(), BaseView {
    override fun dismissLoadingDialog() {
        FlowUtils.instance.dismissLoadingDialog()
    }

    override fun showLoadingDialog() {
        context?.run {
            FlowUtils.instance.showLoadingDialog(this)
        }
    }

    override fun showFailedDialog(errorMessage: String?) {
        context?.run {
            val message = errorMessage ?: this.getString(R.string.failed_to_get_prices_message)
            FlowUtils.instance.showAlert(this, this.getString(R.string.error), message)
        }
    }
}
