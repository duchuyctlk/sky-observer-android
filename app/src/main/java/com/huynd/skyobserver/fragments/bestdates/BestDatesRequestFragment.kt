package com.huynd.skyobserver.fragments.bestdates

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverApp
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.fragments.cheapestflight.CheapestFlightListener
import com.huynd.skyobserver.entities.Airport
import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenter
import com.huynd.skyobserver.presenters.bestdates.BestDatesRequestPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.Constants.Companion.MAX_TRIP_LENGTH
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import kotlinx.android.synthetic.main.fragment_best_dates_request.*
import lombok.Generated
import javax.inject.Inject

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestFragment : BaseFragment(), BestDatesRequestView {
    companion object {
        val TAG: String = BestDatesRequestFragment::class.java.simpleName
        fun newInstance() = BestDatesRequestFragment()
    }

    @Generated
    lateinit var mPricesAPI: PricesAPI
        @Inject set

    private lateinit var mSpinnerSrcPortAdapter: ArrayAdapter<Airport>
    private lateinit var mSpinnerDestPortAdapter: ArrayAdapter<Airport>
    private lateinit var mPresenter: BestDatesRequestPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_best_dates_request, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // inject
        (activity!!.application as SkyObserverApp).skyObserverComponent.inject(this)

        // initialize UI widgets
        mSpinnerSrcPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        mSpinnerDestPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        spinner_src_port.adapter = mSpinnerSrcPortAdapter
        spinner_dest_port.adapter = mSpinnerDestPortAdapter
        RxView.clicks(btn_find_flights).subscribe { onBtnFindFlightsClick() }
        RxCompoundButton.checkedChanges(chk_return_trip).subscribe { isChecked ->
            onChkReturnTripCheckedChanged(isChecked)
        }

        // initialize MPV pattern
        mPresenter = BestDatesRequestPresenterImpl(this, mPricesAPI)
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

    override fun updateListView(data: List<BestDatesInfo>) {
        val srcPort = mSpinnerSrcPortAdapter.getItem(spinner_src_port.selectedItemPosition)
        val destPort = mSpinnerDestPortAdapter.getItem(spinner_dest_port.selectedItemPosition)
        val isReturnTrip = chk_return_trip.isChecked
        val tripLength = if (isReturnTrip) (spinner_trip_length.selectedItem as Int) else 0

        val bundle = Bundle().apply {
            putParcelableArray("data", data.toTypedArray())
            putBoolean("isReturnTrip", isReturnTrip)
            putInt("tripLength", tripLength)
        }
        if (srcPort != null) {
            bundle.putString("srcPort", srcPort.id)
        }
        if (destPort != null) {
            bundle.putString("destPort", destPort.id)
        }
        (activity as CheapestFlightListener).showBestDates(bundle)
    }

    private fun onBtnFindFlightsClick() {
        val srcPort = mSpinnerSrcPortAdapter.getItem(spinner_src_port.selectedItemPosition)
        val destPort = mSpinnerDestPortAdapter.getItem(spinner_dest_port.selectedItemPosition)
        val isReturnTrip = chk_return_trip.isChecked
        val tripLength = if (isReturnTrip) (spinner_trip_length.selectedItem as Int) else 0

        if (srcPort != null && destPort != null) {
            mPresenter.getPrices(srcPort.id, destPort.id, isReturnTrip, tripLength)
        }
    }

    private fun onChkReturnTripCheckedChanged(isChecked: Boolean) {
        spinner_trip_length.isEnabled = isChecked
    }
}
