package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.models.cheapestflight.AirportPriceInfo
import com.huynd.skyobserver.models.cheapestflight.CountryPriceInfo
import com.huynd.skyobserver.utils.formatNumber
import kotlinx.android.synthetic.main.list_view_best_destinations_item.view.*
import kotlinx.android.synthetic.main.list_view_date_cheapest_sub_item.view.*


/**
 * Created by HuyND on 9/28/2017.
 */

open class DateCheapestResultAdapter(@NonNull private val context: Context) :
        BaseExpandableListAdapter() {
    private var mCountryPriceInfo = mutableListOf<CountryPriceInfo>()

    override fun getGroupCount(): Int = mCountryPriceInfo.size

    override fun getChildrenCount(groupPosition: Int): Int =
            mCountryPriceInfo[groupPosition].airportPriceInfoCount

    override fun getGroup(groupPosition: Int): Any? =
            mCountryPriceInfo[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? =
            mCountryPriceInfo[groupPosition].getAirportPriceInfo(childPosition)

    override fun getGroupId(groupPosition: Int): Long = 0

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?,
                              parent: ViewGroup): View? {
        val countryPriceInfo = getGroup(groupPosition) as CountryPriceInfo
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_best_destinations_item, null)
        } else {
            convertView
        }
        view.apply {
            txt_country.text = countryPriceInfo.country.countryName
            if (getChildrenCount(groupPosition) > 0) {
                val airportPriceInfo = getChild(groupPosition, 0) as AirportPriceInfo
                val bestPriceTotalStr = formatNumber(airportPriceInfo.getBestPriceTotal())
                txt_price_per_country.text =
                        context.resources.getString(R.string.best_price_from, bestPriceTotalStr)
            }
        }

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        val airportPriceInfo = getChild(groupPosition, childPosition) as AirportPriceInfo
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_date_cheapest_sub_item, null)
        } else {
            convertView
        }
        view.apply {
            txt_destination.text = airportPriceInfo.getAirportName()
            txt_outbound_flight.text = context.resources.getString(R.string.date_cheapest_outbound_flight,
                    airportPriceInfo.getOutboundCarrier(), airportPriceInfo.getAirportName()
            )
            txt_inbound_flight.text = context.resources.getString(R.string.date_cheapest_inbound_flight,
                    airportPriceInfo.getInboundCarrier(), airportPriceInfo.getAirportName())
            val bestPriceTotalStr = formatNumber(airportPriceInfo.getBestPriceTotal())
            txt_price.text = context.resources.getString(R.string.best_price_from, bestPriceTotalStr)
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    fun clear() {
        mCountryPriceInfo.clear()
    }

    fun addAll(listCountryPriceInfo: List<CountryPriceInfo>) {
        mCountryPriceInfo.addAll(listCountryPriceInfo)
    }
}
