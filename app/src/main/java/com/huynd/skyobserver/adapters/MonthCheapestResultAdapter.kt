package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huynd.skyobserver.R
import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.utils.DateUtils.Companion.dateToString
import com.huynd.skyobserver.utils.formatNumber
import kotlinx.android.synthetic.main.list_view_month_cheapest_sub_item.view.*

/**
 * Created by HuyND on 6/9/2019.
 */

class MonthCheapestResultAdapter(@NonNull private val context: Context) : DateCheapestResultAdapter(context) {
    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        val airportPriceInfo = getChild(groupPosition, childPosition) as AirportPriceInfo
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_month_cheapest_sub_item, null)
        } else {
            convertView
        }
        view.apply {
            txt_destination.text = airportPriceInfo.getAirportName()
            txt_outbound_flight.text = context.resources.getString(
                    R.string.month_cheapest_outbound_flight,
                    dateToString(airportPriceInfo.getOutboundDepartureTime(), "dd/MM"),
                    airportPriceInfo.getOutboundCarrier(),
                    airportPriceInfo.getAirportName()
            )
            txt_inbound_flight.text = context.resources.getString(
                    R.string.month_cheapest_inbound_flight,
                    dateToString(airportPriceInfo.getInboundDepartureTime(), "dd/MM"),
                    airportPriceInfo.getInboundCarrier(),
                    airportPriceInfo.getAirportName()
            )
            val bestPriceTotalStr = formatNumber(airportPriceInfo.getBestPriceTotal())
            txt_price.text = context.resources.getString(R.string.best_price_from, bestPriceTotalStr)
        }

        return view
    }
}
