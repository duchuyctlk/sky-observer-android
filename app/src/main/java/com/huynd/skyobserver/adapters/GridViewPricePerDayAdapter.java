package com.huynd.skyobserver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.models.PricePerDay;

import static com.huynd.skyobserver.utils.Constants.CONVENIENCE_FEE_IN_K;

/**
 * Created by HuyND on 8/12/2017.
 */

public class GridViewPricePerDayAdapter extends ArrayAdapter<PricePerDay> {
    private LayoutInflater mInflater;

    public GridViewPricePerDayAdapter(Context context) {
        super(context, R.layout.grid_view_price_per_day_item);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView textViewDay, textViewPrice;

        if (view == null) {
            view = mInflater.inflate(R.layout.grid_view_price_per_day_item, parent, false);
            textViewDay = (TextView) view.findViewById(R.id.text_view_day);
            textViewPrice = (TextView) view.findViewById(R.id.text_view_price);
            ViewHolder viewHolder = new ViewHolder(textViewDay, textViewPrice);
            view.setTag(viewHolder);
        } else {
            ViewHolder viewHolder = (ViewHolder)view.getTag();
            textViewDay = viewHolder.getTextViewDay();
            textViewPrice = viewHolder.getTextViewPrice();
        }

        PricePerDay item = getItem(position);

        if  (item != null) {
            textViewDay.setText(String.valueOf(item.getDay()));
            textViewPrice.setText(String.valueOf(item.getPriceTotal() / 1000 + CONVENIENCE_FEE_IN_K));
        } else {
            textViewDay.setText("");
            textViewPrice.setText("");
        }
        return view;
    }

    public static class ViewHolder {
        private TextView mTextViewDay;
        private TextView mTextViewPrice;

        public ViewHolder(TextView tvDay, TextView tvPrice) {
            mTextViewDay = tvDay;
            mTextViewPrice = tvPrice;
        }

        public TextView getTextViewDay() {
            return mTextViewDay;
        }

        public TextView getTextViewPrice() {
            return mTextViewPrice;
        }
    }
}
