package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.huynd.skyobserver.R
import com.huynd.skyobserver.models.PricePerDay
import com.huynd.skyobserver.utils.Constants.CONVENIENCE_FEE_IN_K
import com.huynd.skyobserver.utils.RequestHelper
import kotlinx.android.synthetic.main.list_view_price_one_day_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by HuyND on 8/15/2017.
 */

class ListViewPriceOneDayAdapter(@NonNull context: Context) :
        ArrayAdapter<PricePerDay>(context, R.layout.list_view_price_one_day_item),
        View.OnClickListener {
    private var mInflater = LayoutInflater.from(context)
    private var mShouldShowTotalPrice = false

    override
    fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_view_price_one_day_item, parent, false)
            viewHolder = ViewHolder(view.image_view_airline, view.text_view_depart_time,
                    view.text_arrive_time, view.btn_select_price)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.run {
            val item = getItem(position)
            btnSelect.setOnClickListener(null)
            if (item != null) {
                val localDateFormat = SimpleDateFormat("HH:mm")
                localDateFormat.timeZone = TimeZone.getTimeZone("GMT+7:00")

                tvDepart.text = localDateFormat.format(item.departureTime)
                tvArrive.text = localDateFormat.format(item.arrivalTime)
                btnSelect.setOnClickListener(this@ListViewPriceOneDayAdapter)
                val price =
                        if (mShouldShowTotalPrice)
                            item.priceTotal / 1000 + CONVENIENCE_FEE_IN_K
                        else item.price / 1000
                btnSelect.text = price.toString()

                Glide.with(context)
                        .load(RequestHelper.airlinesIconUrlBuilder(item.carrier))
                        .into(imgvAirline)
            } else {
                tvDepart.text = ""
                tvArrive.text = ""
            }
        }

        return view
    }

    override
    fun onClick(v: View) {
        // TODO
        // implement in another story
        // use BaseActivity.getCurrentFragment() to send message to PriceOneDay fragment
    }

    fun setShouldShowTotalPrice(shouldShowTotalPrice: Boolean) {
        mShouldShowTotalPrice = shouldShowTotalPrice
    }

    class ViewHolder(val imgvAirline: ImageView, val tvDepart: TextView,
                     val tvArrive: TextView, val btnSelect: Button)
}
