package com.huynd.skyobserver.fragments.cheapestflight.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverApp
import com.huynd.skyobserver.adapters.MonthCheapestResultAdapter
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.presenters.cheapestflight.date.DateCheapestResultPresenter
import com.huynd.skyobserver.presenters.cheapestflight.date.DateCheapestResultPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.utils.CountryAirportUtils.getAirportById
import com.huynd.skyobserver.utils.DateUtils.Companion.dateToString
import com.huynd.skyobserver.views.date.DateCheapestResultView
import kotlinx.android.synthetic.main.fragment_date_cheapest_result.*
import lombok.Generated
import javax.inject.Inject

/**
 * Created by HuyND on 06/08/2019
 */

class MonthCheapestResultFragment : BaseFragment(), DateCheapestResultView {
    companion object {
        val TAG: String = MonthCheapestResultFragment::class.java.simpleName
        fun newInstance() = MonthCheapestResultFragment()
    }

    @Generated
    lateinit var mPricesAPI: PricesAPI
        @Inject set

    private lateinit var mPresenter: DateCheapestResultPresenter

    private lateinit var mAdapter: MonthCheapestResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_date_cheapest_result, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // inject
        (activity!!.application as SkyObserverApp).skyObserverComponent.inject(this)

        // UI
        mAdapter = MonthCheapestResultAdapter(context!!)
        lst_best_destinations.setAdapter(mAdapter)

        // initialize MPV pattern
        mPresenter = DateCheapestResultPresenterImpl(this, mPricesAPI)

        // get data from intent
        arguments?.run {
            val listCountryPriceInfo = getParcelableArray("listCountryPriceInfo")?.toList()
                    ?: listOf<CountryPriceInfo>()

            updateListViewInboundPrices(listCountryPriceInfo as List<CountryPriceInfo>)

            val yearOutbound = getInt("yearOutbound")
            val monthOutbound = getInt("monthOutbound")
            val srcPort = getString("srcPort", "")
            val returnTrip = getBoolean("returnTrip")
            var yearInbound = yearOutbound
            var monthInbound = monthOutbound
            if (returnTrip) {
                yearInbound = getInt("yearInbound")
                monthInbound = getInt("monthInbound")
            }

            txt_source_port_value.text = getAirportById(srcPort).toString()
            txt_date_outbound_value.text = dateToString(yearOutbound, monthOutbound - 1)
            txt_date_inbound_value.text = dateToString(yearInbound, monthInbound - 1)
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
