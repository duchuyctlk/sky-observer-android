package com.huynd.skyobserver.fragments.bestdates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.adapters.BestDatesResultAdapter
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.models.bestdates.BestDatesInfo
import com.huynd.skyobserver.utils.Constants
import com.huynd.skyobserver.utils.CountryAirportUtils
import com.huynd.skyobserver.views.bestdates.BestDatesResultView
import kotlinx.android.synthetic.main.fragment_best_dates_result.*

/**
 * Created by HuyND on 06/27/2019
 */

class BestDatesResultFragment : BaseFragment(), BestDatesResultView {
    companion object {
        val TAG: String = BestDatesResultFragment::class.java.simpleName
        fun newInstance() = BestDatesResultFragment()
    }

    private lateinit var mAdapter: BestDatesResultAdapter
    private lateinit var mSpinnerSrcPortAdapter: ArrayAdapter<Airport>
    private lateinit var mSpinnerDestPortAdapter: ArrayAdapter<Airport>
    private lateinit var mSpinnerTripLengthAdapter: ArrayAdapter<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_best_dates_result, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // UI
        mAdapter = BestDatesResultAdapter(context!!)

        mSpinnerSrcPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        mSpinnerDestPortAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf())
        mSpinnerTripLengthAdapter = ArrayAdapter(context!!,
                android.R.layout.simple_spinner_dropdown_item,
                (1..Constants.MAX_TRIP_LENGTH).toMutableList())
        lst_best_dates.setAdapter(mAdapter)
        spinner_src_port.adapter = mSpinnerSrcPortAdapter
        spinner_dest_port.adapter = mSpinnerDestPortAdapter
        spinner_trip_length.adapter = mSpinnerTripLengthAdapter

        updateAirports()

        // get data from intent
        arguments?.run {
            val data =
                    getParcelableArray("data")?.toList()
                            ?: listOf<BestDatesInfo>()

            updateListViewData(data as List<BestDatesInfo>)

            val srcPort = getString("srcPort", "")
            val destPort = getString("destPort", "")
            val isReturnTrip = getBoolean("isReturnTrip")
            var tripLength = 0
            if (isReturnTrip) {
                tripLength = getInt("tripLength")
                mAdapter.setIsReturnTrip(isReturnTrip)
            }

            spinner_src_port.setSelection(getAirportPosition(mSpinnerSrcPortAdapter, srcPort))
            spinner_dest_port.setSelection(getAirportPosition(mSpinnerDestPortAdapter, destPort))
            chk_return_trip.isChecked = isReturnTrip
            spinner_trip_length.isEnabled = isReturnTrip
            if (isReturnTrip) {
                spinner_trip_length.setSelection(mSpinnerTripLengthAdapter.getPosition(tripLength))
            }
        }
    }

    private fun updateAirports() {
        val airports = CountryAirportUtils.getAirports()
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

    override fun updateListViewData(data: List<BestDatesInfo>) {
        mAdapter.clear()
        mAdapter.addAll(data)
        mAdapter.notifyDataSetChanged()
    }

    private fun getAirportPosition(adapter: ArrayAdapter<Airport>, portId: String): Int {
        for (index in 0 until adapter.count) {
            if (adapter.getItem(index)?.id == portId) {
                return index
            }
        }
        return -1
    }
}
