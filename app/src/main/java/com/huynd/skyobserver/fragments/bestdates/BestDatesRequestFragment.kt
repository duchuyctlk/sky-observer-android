package com.huynd.skyobserver.fragments.bestdates

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenter
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenterImpl
import com.huynd.skyobserver.utils.Constants.Companion.MAX_TRIP_LENGTH
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import kotlinx.android.synthetic.main.fragment_best_dates_request.*

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestFragment : BaseFragment(), BestDatesRequestView {
    companion object {
        val TAG: String = BestDatesRequestFragment::class.java.simpleName
        fun newInstance() = BestDatesRequestFragment()
    }

    private lateinit var mSpinnerSrcPortAdapter: ArrayAdapter<Airport>
    private lateinit var mSpinnerDestPortAdapter: ArrayAdapter<Airport>
    private lateinit var mPresenter: BestDatesRequestPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_best_dates_request, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // initialize UI widgets
        mSpinnerSrcPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        mSpinnerDestPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        spinner_src_port.adapter = mSpinnerSrcPortAdapter
        spinner_dest_port.adapter = mSpinnerDestPortAdapter
        RxCompoundButton.checkedChanges(chk_return_trip).subscribe { isChecked ->
            onChkReturnTripCheckedChanged(isChecked)
        }

        // initialize MPV pattern
        mPresenter = BestDatesRequestPresenterImpl(this)
        mPresenter.initSpinnersValues()
    }

    override fun updateAirports(airports: List<Airport>) {
        mSpinnerSrcPortAdapter.run {
            clear()
            addAll(airports)
            notifyDataSetChanged()
        }
        spinner_src_port.setSelection(0)

        mSpinnerDestPortAdapter.run {
            clear()
            addAll(airports)
            notifyDataSetChanged()
        }
        spinner_dest_port.setSelection(1)
    }

    override fun updatePossibleTripLength() {
        spinner_trip_length.adapter =
                ArrayAdapter<Int>(
                        context!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        (1..MAX_TRIP_LENGTH).toMutableList()
                )
    }

    private fun onChkReturnTripCheckedChanged(isChecked: Boolean) {
        spinner_trip_length.isEnabled = isChecked
    }
}
