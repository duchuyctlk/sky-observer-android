package com.huynd.skyobserver.fragments.cheapestflight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.SkyObserverApp
import com.huynd.skyobserver.fragments.BaseFragment
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
    }

    override fun showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message))
    }
}
