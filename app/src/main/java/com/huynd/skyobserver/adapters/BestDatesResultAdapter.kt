package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.utils.CheapestPricePerMonthResponseComparator
import com.huynd.skyobserver.utils.formatNumber
import kotlinx.android.synthetic.main.list_view_best_dates_item.view.*
import kotlinx.android.synthetic.main.list_view_best_dates_sub_item.view.*

/**
 * Created by HuyND on 6/27/2019.
 */

class BestDatesResultAdapter(@NonNull private val context: Context) :
        BaseExpandableListAdapter() {
    private var mIsReturnTrip: Boolean = false

    private var mOutData: Map<Pair<Int, Int>, List<CheapestPricePerMonthResponse>> = mapOf()
    private var mInData: Map<Pair<Int, Int>, List<CheapestPricePerMonthResponse>> = mapOf()

    override fun getGroupCount(): Int = mOutData.size

    override fun getChildrenCount(groupPosition: Int): Int =
            mOutData.values.toList()[groupPosition].size

    override fun getGroup(groupPosition: Int): Any? =
            mOutData.values.toList()[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? =
            mOutData.values.toList()[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int): Long = 0

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View? {
        val keyAtPosition = mOutData.keys.toList()[groupPosition]
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_best_dates_item, null)
        } else {
            convertView
        }
        view.apply {
            txt_month.text = context.resources.getString(R.string.best_dates_item_month,
                    keyAtPosition.first, keyAtPosition.second)
        }

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_best_dates_sub_item, null)
        } else {
            convertView
        }
        view.apply {
            val outboundItem = mOutData.values.toList()[groupPosition][childPosition]
            txt_outbound_date.text = context.resources.getString(R.string.best_dates_sub_item_date,
                    outboundItem.id.year,
                    outboundItem.id.monthInYear,
                    outboundItem.id.dayInMonth)
            val outboundPriceStr = formatNumber(outboundItem.cheapestTotalPrice)
            txt_outbound_price.text = context.resources.getString(R.string.best_price_from, outboundPriceStr)

            if (mIsReturnTrip) {
                if (groupPosition < mInData.values.size && childPosition < mInData.values.toList()[groupPosition].size) {
                    txt_inbound_date.visibility = View.VISIBLE
                    txt_inbound_price.visibility = View.VISIBLE

                    val inboundItem = mInData.values.toList()[groupPosition][childPosition]
                    txt_inbound_date.text = context.resources.getString(R.string.best_dates_sub_item_date,
                            inboundItem.id.year,
                            inboundItem.id.monthInYear,
                            inboundItem.id.dayInMonth)
                    val inboundPriceStr = formatNumber(inboundItem.cheapestTotalPrice)
                    txt_inbound_price.text = context.resources.getString(R.string.best_price_from, inboundPriceStr)
                } else {
                    txt_inbound_date.visibility = View.GONE
                    txt_inbound_price.visibility = View.GONE
                }
            }
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    fun clear() {
        mOutData = mapOf()
        mInData = mapOf()
    }

    fun addAll(data: List<CheapestPricePerMonthResponse>, isOutData: Boolean) {
        if (isOutData) {
            mOutData = data
                    .sortedWith(CheapestPricePerMonthResponseComparator())
                    .groupBy { Pair(it.id.year, it.id.monthInYear) }
        } else {
            mInData = data
                    .sortedWith(CheapestPricePerMonthResponseComparator())
                    .groupBy { Pair(it.id.year, it.id.monthInYear) }
        }
    }

    fun setIsReturnTrip(isReturnTrip: Boolean) {
        mIsReturnTrip = isReturnTrip
    }
}
