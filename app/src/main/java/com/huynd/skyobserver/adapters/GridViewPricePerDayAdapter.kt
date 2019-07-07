package com.huynd.skyobserver.adapters

import android.content.Context
import android.support.annotation.NonNull
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.huynd.skyobserver.R
import com.huynd.skyobserver.entities.PricePerDay
import com.huynd.skyobserver.utils.Constants.Companion.CONVENIENCE_FEE_IN_K
import com.huynd.skyobserver.utils.RequestHelper
import kotlinx.android.synthetic.main.grid_view_price_per_day_item.view.*

/**
 * Created by HuyND on 8/12/2017.
 */

class GridViewPricePerDayAdapter(@NonNull context: Context) :
        ArrayAdapter<PricePerDay>(context, R.layout.grid_view_price_per_day_item) {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = mInflater.inflate(R.layout.grid_view_price_per_day_item, parent, false)
            viewHolder = ViewHolder(view.image_view_airline, view.text_view_day, view.text_view_price)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.run {
            val item = getItem(position)
            if (item != null) {
                tvDay.text = item.day.toString()
                tvPrice.text = "${(item.priceTotal / 1000 + CONVENIENCE_FEE_IN_K)}"

                val carrier = item.carrier
                if (!TextUtils.isEmpty(carrier)) {
                    Glide.with(context)
                            .load(RequestHelper.airlinesIconUrlBuilder(carrier))
                            .into(imgvAirline)
                } else {
                    Glide.with(context).clear(imgvAirline)
                }
            } else {
                Glide.with(context).clear(imgvAirline)
                tvDay.text = ""
                tvPrice.text = ""
            }
        }
        return view
    }

    class ViewHolder(val imgvAirline: ImageView, val tvDay: TextView, val tvPrice: TextView)
}
