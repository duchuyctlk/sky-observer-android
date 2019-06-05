package com.huynd.skyobserver.fragments.cheapestflight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverApp
import com.huynd.skyobserver.adapters.ListViewCheapestPriceResultAdapter
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenter
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.DateUtils.Companion.dateToString
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView
import kotlinx.android.synthetic.main.fragment_flight_with_cheapest_price_result.*
import lombok.Generated
import javax.inject.Inject

/**
 * Created by HuyND on 4/21/2018.
 */

class FlightWithCheapestPriceResultFragment : BaseFragment(), FlightWithCheapestPriceResultView {

    companion object {
        val TAG: String = FlightWithCheapestPriceResultFragment::class.java.simpleName
        fun newInstance() = FlightWithCheapestPriceResultFragment()
    }

    @Generated
    lateinit var mPricesAPI: PricesAPI
        @Inject set

    private lateinit var mPresenter: FlightWithCheapestPriceResultPresenter

    private lateinit var mAdapter: ListViewCheapestPriceResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_flight_with_cheapest_price_result, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // inject
        (activity!!.application as SkyObserverApp).skyObserverComponent.inject(this)

        // UI
        mAdapter = ListViewCheapestPriceResultAdapter(context!!)
        lst_best_destinations.setAdapter(mAdapter)

        // initialize MPV pattern
        mPresenter = FlightWithCheapestPriceResultPresenterImpl(this, mPricesAPI)

        // get data from intent
        arguments?.run {
            val listCountryPriceInfo = getParcelableArray("listCountryPriceInfo")?.toList()
                    ?: listOf<CountryPriceInfo>()

            updateListViewInboundPrices(listCountryPriceInfo as List<CountryPriceInfo>)

            val yearOutbound = getInt("yearOutbound")
            val monthOutbound = getInt("monthOutbound")
            val dayOutbound = getInt("dayOutbound")
            val srcPort = getString("srcPort", "")
            val returnTrip = getBoolean("returnTrip")
            var yearInbound = yearOutbound
            var monthInbound = monthOutbound
            var dayInbound = dayOutbound
            if (returnTrip) {
                yearInbound = getInt("yearInbound")
                monthInbound = getInt("monthInbound")
                dayInbound = getInt("dayInbound")
            }

            txt_source_port_value.text = getAirportById(srcPort).toString()
            txt_date_outbound_value.text = dateToString(yearOutbound, monthOutbound - 1, dayOutbound)
            txt_date_inbound_value.text = dateToString(yearInbound, monthInbound - 1, dayInbound)
        }
    }

    override fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>) {
        mAdapter.clear()
        mAdapter.addAll(listCountryPriceInfo)
        mAdapter.notifyDataSetChanged()
    }

    override fun showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message))
    }
}
