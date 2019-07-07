package com.huynd.skyobserver.fragments.cheapestflight.month

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
import com.huynd.skyobserver.entities.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.presenters.cheapestflight.month.MonthCheapestRequestPresenter
import com.huynd.skyobserver.presenters.cheapestflight.month.MonthCheapestRequestPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.date.DateCheapestRequestView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.twinkle94.monthyearpicker.picker.SkyObserverYearMonthPickerDialog
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog
import kotlinx.android.synthetic.main.fragment_date_cheapest_request.*
import lombok.Generated
import javax.inject.Inject

/**
 * Created by HuyND on 06/08/2019
 */

class MonthCheapestRequestFragment : BaseFragment(), DateCheapestRequestView {
    companion object {
        val TAG: String = MonthCheapestRequestFragment::class.java.simpleName
        fun newInstance() = MonthCheapestRequestFragment()
    }

    @Generated
    lateinit var mPricesAPI: PricesAPI
        @Inject set

    private lateinit var mSpinnerSrcPortAdapter: ArrayAdapter<Airport>
    private lateinit var mPresenter: MonthCheapestRequestPresenter

    private lateinit var mOutboundYearMonthPickerDialog: SkyObserverYearMonthPickerDialog
    private lateinit var mInboundYearMonthPickerDialog: SkyObserverYearMonthPickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_month_cheapest_request, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // inject
        (activity!!.application as SkyObserverApp).skyObserverComponent.inject(this)

        // initialize UI widgets
        mSpinnerSrcPortAdapter = ArrayAdapter(context!!,
                android.R.layout.simple_list_item_1, arrayListOf())
        spinner_src_port.adapter = mSpinnerSrcPortAdapter

        RxView.clicks(edit_text_date_outbound).subscribe { mOutboundYearMonthPickerDialog.show() }
        RxView.clicks(edit_text_date_inbound).subscribe { mInboundYearMonthPickerDialog.show() }
        RxView.clicks(btn_find_flights).subscribe { onBtnFindFlightsClick() }
        RxCompoundButton.checkedChanges(chk_return_trip).subscribe { isChecked ->
            onChkReturnTripCheckedChanged(isChecked)
        }

        // initialize MPV pattern
        mPresenter = MonthCheapestRequestPresenterImpl(this, mPricesAPI)
        mPresenter.initSpinnersValues()
    }

    private fun onBtnFindFlightsClick() {
        val flightInfo = getFlightDates()

        val srcPort = mSpinnerSrcPortAdapter.getItem(spinner_src_port.selectedItemPosition)
        srcPort?.run {
            flightInfo.run {
                val yearOutbound = getInt("yearOutbound")
                val monthOutbound = getInt("monthOutbound")
                val returnTrip = getBoolean("returnTrip")
                var yearInbound = yearOutbound
                var monthInbound = monthOutbound
                if (returnTrip) {
                    yearInbound = getInt("yearInbound")
                    monthInbound = getInt("monthInbound")
                }
                mPresenter.getPrices(
                        yearOutbound, monthOutbound,
                        yearInbound, monthInbound,
                        srcPort.id, returnTrip)
            }
        }
    }

    private fun getFlightDates(): Bundle {
        return Bundle().let {
            // outbound date
            mOutboundYearMonthPickerDialog.run {
                it.putInt("yearOutbound", year)
                it.putInt("monthOutbound", month + 1)
            }

            // inbound date
            it.putBoolean("returnTrip", chk_return_trip.isChecked)
            if (chk_return_trip.isChecked) {
                mInboundYearMonthPickerDialog.run {
                    it.putInt("yearInbound", year)
                    it.putInt("monthInbound", month + 1)
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
        mOutboundYearMonthPickerDialog = SkyObserverYearMonthPickerDialog(context,
                YearMonthPickerDialog.OnDateSetListener { year, month ->
                    mPresenter.setDateToEditText(year, month, true)
                })
        mInboundYearMonthPickerDialog = SkyObserverYearMonthPickerDialog(context,
                YearMonthPickerDialog.OnDateSetListener { year, month ->
                    mPresenter.setDateToEditText(year, month, false)
                })
    }

    override fun updateDateToEditText(dateAsString: String, isOutbound: Boolean) {
        if (isOutbound) {
            edit_text_date_outbound.setText(dateAsString)
        } else {
            edit_text_date_inbound.setText(dateAsString)
        }
    }

    override fun setDatePickersMinDate(minDate: Long) {
//        mOutboundDatePickerDialog.datePicker.minDate = minDate
//        mInboundDatePickerDialog.datePicker.minDate = minDate
        // TODO
    }

    override fun showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message))
    }

    override fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>) {
        val priceInfo = getFlightDates().apply {
            putParcelableArray("listCountryPriceInfo", listCountryPriceInfo.toTypedArray())
        }
        val srcPort = mSpinnerSrcPortAdapter.getItem(spinner_src_port.selectedItemPosition)
        srcPort?.run {
            priceInfo.putString("srcPort", id)
        }
        (activity as CheapestFlightListener)
                .showMonthInfo(priceInfo)
    }
}
