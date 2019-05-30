package com.huynd.skyobserver.fragments.cheapestflight

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.Airport
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceRequestPresenter
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceRequestPresenterImpl
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceRequestView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import kotlinx.android.synthetic.main.fragment_flight_with_cheapest_price_request.*

/**
 * Created by HuyND on 11/19/2017.
 */

class FlightWithCheapestPriceRequestRequestFragment : BaseFragment(), FlightWithCheapestPriceRequestView {
    companion object {
        val TAG: String = FlightWithCheapestPriceRequestRequestFragment::class.java.simpleName
        fun newInstance() = FlightWithCheapestPriceRequestRequestFragment()
    }

    private lateinit var mSpinnerSrcPortAdapter: ArrayAdapter<Airport>
    private lateinit var mPresenter: FlightWithCheapestPriceRequestPresenter

    private lateinit var mOutboundDatePickerDialog: DatePickerDialog
    private lateinit var mInboundDatePickerDialog: DatePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_flight_with_cheapest_price_request, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // initialize UI widgets
        mSpinnerSrcPortAdapter = ArrayAdapter(context!!,
                android.R.layout.simple_list_item_1, arrayListOf())
        spinner_src_port.adapter = mSpinnerSrcPortAdapter

        RxView.clicks(edit_text_date_outbound).subscribe { mOutboundDatePickerDialog.show() }
        RxView.clicks(edit_text_date_inbound).subscribe { mInboundDatePickerDialog.show() }
        RxView.clicks(btn_find_flights).subscribe { onBtnFindFlightsClick() }
        RxCompoundButton.checkedChanges(chk_return_trip).subscribe { isChecked ->
            onChkReturnTripCheckedChanged(isChecked)
        }

        // initialize MPV pattern
        mPresenter = FlightWithCheapestPriceRequestPresenterImpl(this)
        mPresenter.initSpinnersValues()
    }

    private fun onBtnFindFlightsClick() {
        try {
            val flightInfo = getFlightDates()

            val srcPort = mSpinnerSrcPortAdapter.getItem(spinner_src_port.selectedItemPosition)
            srcPort?.run {
                flightInfo.putString("srcPort", id)
                (activity as OnFlightWithCheapestPriceInfoSelectedListener)
                        .onFlightWithCheapestPriceInfoSelected(flightInfo)
            }
        } catch (e: ClassCastException) {
            Log.d(TAG, "Activity must implement OnFlightInfoSelectedListener.")
        }
    }

    private fun getFlightDates(): Bundle {
        return Bundle().let {
            // outbound date
            mOutboundDatePickerDialog.datePicker.run {
                it.putInt("yearOutbound", year)
                it.putInt("monthOutbound", month + 1)
                it.putInt("dayOutbound", dayOfMonth)
            }

            // inbound date
            it.putBoolean("returnTrip", chk_return_trip.isChecked)
            if (chk_return_trip.isChecked) {
                mInboundDatePickerDialog.datePicker.run {
                    it.putInt("yearInbound", year)
                    it.putInt("monthInbound", month + 1)
                    it.putInt("dayInbound", dayOfMonth)
                }
            }
            it
        }
    }

    override fun updateAirports(airports: List<Airport>) {
        mSpinnerSrcPortAdapter.run {
            clear()
            addAll(airports)
            notifyDataSetChanged()
        }
        spinner_src_port.setSelection(0)
    }

    private fun onChkReturnTripCheckedChanged(isChecked: Boolean) {
        edit_text_date_inbound.isEnabled = isChecked
    }

    override fun updateDatePickers(startYear: Int, startMonth: Int, startDayOfMonth: Int) {
        mOutboundDatePickerDialog = DatePickerDialog(context!!,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mPresenter.setDateToEditText(year, month, dayOfMonth, true)
                }, startYear, startMonth, startDayOfMonth)

        mInboundDatePickerDialog = DatePickerDialog(context!!,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mPresenter.setDateToEditText(year, month, dayOfMonth, false)
                }, startYear, startMonth, startDayOfMonth)
    }

    override fun updateDateToEditText(dateAsString: String, isOutbound: Boolean) {
        if (isOutbound) {
            edit_text_date_outbound.setText(dateAsString)
        } else {
            edit_text_date_inbound.setText(dateAsString)
        }
    }

    override fun setDatePickersMinDate(minDate: Long) {
        mOutboundDatePickerDialog.datePicker.minDate = minDate
        mInboundDatePickerDialog.datePicker.minDate = minDate
    }
}