package com.huynd.skyobserver.fragments.bestdates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenter
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenterImpl
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestFragment : BaseFragment(), BestDatesRequestView {
    companion object {
        val TAG: String = BestDatesRequestFragment::class.java.simpleName
        fun newInstance() = BestDatesRequestFragment()
    }

    private lateinit var mPresenter: BestDatesRequestPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_best_dates_request, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mPresenter = BestDatesRequestPresenterImpl(this)
    }
}
