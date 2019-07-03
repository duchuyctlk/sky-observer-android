package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.huynd.skyobserver.R
import com.huynd.skyobserver.entities.bestdates.BestDatesInfo
import com.huynd.skyobserver.utils.BestDatesInfoComparator
import com.huynd.skyobserver.utils.DateUtils
import com.huynd.skyobserver.utils.formatNumber
import kotlinx.android.synthetic.main.list_view_best_dates_item.view.*
import kotlinx.android.synthetic.main.list_view_best_dates_sub_item.view.*
import java.util.*

/**
 * Created by HuyND on 6/27/2019.
 */

class BestDatesResultAdapter(@NonNull private val context: Context) :
        BaseExpandableListAdapter() {
    private var mIsReturnTrip: Boolean = false

    private var mBestDates: Map<Pair<Int, Int>, List<BestDatesInfo>> = mapOf()

    override fun getGroupCount(): Int = mBestDates.size

    override fun getChildrenCount(groupPosition: Int): Int =
            mBestDates.values.toList()[groupPosition].size

    override fun getGroup(groupPosition: Int): Any? =
            mBestDates.values.toList()[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? =
            mBestDates.values.toList()[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int): Long = 0

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View? {
        val keyAtPosition = mBestDates.keys.toList()[groupPosition]
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_best_dates_item, null)
        } else {
            convertView
        }
        view.apply {
            txt_month.text = getDisplayYearMonth(keyAtPosition.first, keyAtPosition.second - 1)
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

        val bestDateInfo = mBestDates.values.toList()[groupPosition][childPosition]
        view.apply {
            val outboundId = bestDateInfo.outboundId
            txt_outbound_date.text = context.resources.getString(R.string.best_dates_sub_item_date,
                    outboundId.year,
                    outboundId.monthInYear,
                    outboundId.dayInMonth)
            val outboundPriceStr = formatNumber(bestDateInfo.outboundTotalPrice)
            txt_outbound_price.text = context.resources.getString(R.string.best_price_from, outboundPriceStr)

            val viewVisibility = if (mIsReturnTrip) View.VISIBLE else View.GONE
            txt_inbound_date.visibility = viewVisibility
            txt_inbound_price.visibility = viewVisibility
            if (mIsReturnTrip) {
                val inboundId = bestDateInfo.inboundId
                txt_inbound_date.text = context.resources.getString(R.string.best_dates_sub_item_date,
                        inboundId.year,
                        inboundId.monthInYear,
                        inboundId.dayInMonth)
                val inboundPriceStr = formatNumber(bestDateInfo.inboundTotalPrice)
                txt_inbound_price.text = context.resources.getString(R.string.best_price_from, inboundPriceStr)
            }
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    fun clear() {
        mBestDates = mapOf()
    }

    fun addAll(data: List<BestDatesInfo>) {
        mBestDates = data
                .sortedWith(BestDatesInfoComparator.instance)
                .groupBy { Pair(it.outboundId.year, it.outboundId.monthInYear) }
    }

    fun setIsReturnTrip(isReturnTrip: Boolean) {
        mIsReturnTrip = isReturnTrip
    }

    private fun getDisplayYearMonth(year: Int, month: Int) : String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        return DateUtils.dateToString(cal.time, "MMM yyyy")
    }
}
