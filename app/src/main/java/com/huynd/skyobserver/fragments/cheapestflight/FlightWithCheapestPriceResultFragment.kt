package com.huynd.skyobserver.fragments.cheapestflight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverApp
import com.huynd.skyobserver.fragments.BaseFragment
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenter
import com.huynd.skyobserver.presenters.cheapestflight.FlightWithCheapestPriceResultPresenterImpl
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.cheapestflight.FlightWithCheapestPriceResultView
import javax.inject.Inject

/**
 * Created by HuyND on 4/21/2018.
 */

class FlightWithCheapestPriceResultFragment : BaseFragment(), FlightWithCheapestPriceResultView {

    companion object {
        val TAG: String = FlightWithCheapestPriceResultFragment::class.java.simpleName
        fun newInstance() = FlightWithCheapestPriceResultFragment()
    }

    @Inject
    lateinit var mPricesAPI: PricesAPI

    private lateinit var mPresenter: FlightWithCheapestPriceResultPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.fragment_flight_with_cheapest_price_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // inject
        (activity!!.application as SkyObserverApp).skyObserverComponent.inject(this)

        // initialize MPV pattern
        mPresenter = FlightWithCheapestPriceResultPresenterImpl(this, mPricesAPI)

        // get data from intent
        arguments?.run {
            val yearOutbound = getInt("yearOutbound")
            val monthOutbound = getInt("monthOutbound")
            val dayOutbound = getInt("dayOutbound")
            val srcPort = getString("srcPort", "")
            val returnTrip = getBoolean("returnTrip")
            var yearInbound = 0
            var monthInbound = 0
            var dayInbound = 0
            if (returnTrip) {
                yearInbound = getInt("yearInbound")
                monthInbound = getInt("monthInbound")
                dayInbound = getInt("dayInbound")
            }

            mPresenter.getPrices(
                    yearOutbound, monthOutbound, dayOutbound,
                    yearInbound, monthInbound, dayInbound,
                    srcPort, returnTrip)
        }
    }

    override fun updateListViewInboundPrices(listCountryPriceInfo: List<CountryPriceInfo>) {
        // TODO
    }

    override fun showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message))
    }
}
